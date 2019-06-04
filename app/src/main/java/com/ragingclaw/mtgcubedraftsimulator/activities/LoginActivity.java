package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.fragments.CreateAccountFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.EmailPasswordFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.LoginFragment;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;


import java.util.List;

import timber.log.Timber;

import static com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener,
        EmailPasswordFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();

        // set up Timber because it makes logging better

        if (savedInstanceState == null) {
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.loginOptionsView, loginFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (currentUser != null || account != null) {
            // Already signed in, go to the MainActivity
            loggedIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Something went wrong with Google. Try using the create account option.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Timber.tag("fart").i("firebaseAuthWithGoogle: %s", acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            loggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            View toastView = getLayoutInflater().inflate(R.layout.custom_toast_view, null);
                            TextView googleSignInErrorToast = toastView.findViewById(R.id.googleSignInErrorToast);
                            googleSignInErrorToast.setVisibility(View.VISIBLE);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setView(toastView);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 500);
                            toast.show();
                        }

                    }
                });
    }

    private void swapFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_from_top, R.anim.slide_in_from_bottom, R.anim.slide_out_from_top);
        fragmentTransaction.replace(R.id.loginOptionsView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(View view, String message) {

        if (message.equals("email")) {
            swapFragment(new EmailPasswordFragment());
        }

        if (message.equals("create")) {
            swapFragment(new CreateAccountFragment());
        }

        if (message.equals("google")) {
            googleSignIn();
        }
    }

    @Override
    public void onEmailFragmentInteraction(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, go to the MainActivity
                            user = mAuth.getCurrentUser();
                            loggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            View toastView = getLayoutInflater().inflate(R.layout.custom_toast_view, null);
                            TextView emailSignInErrorToast = toastView.findViewById(R.id.emailSignInErrorToast);
                            emailSignInErrorToast.setVisibility(View.VISIBLE);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setView(toastView);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 500);
                            toast.show();
                        }
                    }
                });
    }

    @Override
    public void onCreateAccountFragmentInteraction(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Timber.tag("fart").i("user was created");
                    user = mAuth.getCurrentUser();

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        View toastView = getLayoutInflater().inflate(R.layout.custom_toast_view, null);
                        TextView accountAlreadyExistsErrorToast = toastView.findViewById(R.id.accountAlreadyExistsErrorToast);
                        accountAlreadyExistsErrorToast.setVisibility(View.VISIBLE);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setView(toastView);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 500);
                        toast.show();
                    }
                }
            }
        });
    }

    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void loggedIn() {
        Timber.tag("fart").i("logged in?");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
