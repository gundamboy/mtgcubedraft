package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.EmailPasswordFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.LoginFragment;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class LoginActivity extends FragmentActivity implements LoginFragment.OnFragmentInteractionListener, EmailPasswordFragment.OnFragmentInteractionListener {
    //@BindView(R.id.loginOptionsView) FrameLayout loginOptionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //ButterKnife.bind(this);

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


    private void swapFragment() {
        EmailPasswordFragment emailPasswordFragment = new EmailPasswordFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_from_top, R.anim.slide_in_from_bottom, R.anim.slide_out_from_top);
        fragmentTransaction.replace(R.id.loginOptionsView, emailPasswordFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(View view, String message) {
        Timber.tag("fart").i("message: %s, view %s", message, view);

        if (message.equals("email")) {
            Timber.tag("fart").i("email button was pressed");
            swapFragment();
        }
    }

    @Override
    public void onEmailFragmentInteraction(View view, Bundle bundle) {
        Timber.tag("fart").i("bundle: %s, view %s",bundle, view);
    }
}
