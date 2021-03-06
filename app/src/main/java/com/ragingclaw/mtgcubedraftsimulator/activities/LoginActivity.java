package com.ragingclaw.mtgcubedraftsimulator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    public static final String ANONYMOUS = AllMyConstants.ANONYMOUS;
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        mUsername = ANONYMOUS;

        mAuthStateListener = firebaseAuth -> {
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
        };
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
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
                Toast.makeText(this, getResources().getString(R.string.toast_login_canceled_text), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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