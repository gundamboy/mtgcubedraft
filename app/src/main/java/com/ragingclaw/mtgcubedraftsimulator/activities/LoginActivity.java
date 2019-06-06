package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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


import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener,
        EmailPasswordFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ActionBar actionBar;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.main_activity_title));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Html.fromHtml("<font color=\"#555555\">" + getString(R.string.app_name) + "</font>"));

        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        mUsername = ANONYMOUS;

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser theUser = firebaseAuth.getCurrentUser();
                if(theUser != null){
                    //user is signed in
                    onSignedInInitialize(theUser.getDisplayName());
                } else {
                    //User is signed out
                    onSignedOutCleanup();

                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    );
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                loggedIn();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onFragmentInteraction(View view, String message) {


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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //attach authStateListener
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //detach authStateListener
        if(mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


}