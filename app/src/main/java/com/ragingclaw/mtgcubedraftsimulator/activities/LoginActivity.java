package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.CreateAccountFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.EmailPasswordFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.LoginFragment;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;

import timber.log.Timber;

public class LoginActivity extends FragmentActivity implements LoginFragment.OnFragmentInteractionListener, EmailPasswordFragment.OnFragmentInteractionListener, CreateAccountFragment.OnFragmentInteractionListener {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // set up Timber because it makes logging better
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new NotLoggingTree());
        }

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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
    }

    @Override
    public void onEmailFragmentInteraction(View view, Bundle bundle) {
        Timber.tag("fart").i("bundle: %s, view %s",bundle, view);
    }

    @Override
    public void onCreateAccountFragmentInteraction(View view, Bundle bundle) {
        Timber.tag("fart").i("bundle: %s, view %s",bundle, view);
    }

    private void createAccount(String email, String password) {}

    private void signIn(String email, String password) {}

    private void signOut() {}

    // might be smarter to move this into the fragment.
    private boolean validateForm() {
        boolean valid = true;

        return valid;
    }
}
