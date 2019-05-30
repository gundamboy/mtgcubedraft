package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.FragmentCubeReview;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewCubeStepOneFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewCubeStepTwoFragment;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

public class NewCubeActivity extends AppCompatActivity implements
        NewCubeStepOneFragment.OnFragmentInteractionListenerStepOne,
        NewCubeStepTwoFragment.OnFragmentInteractionListenerStepTwo,
        FragmentCubeReview.OnCubeReviewFragmentInteractionListener {
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cube);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.new_cube_activity_title));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (savedInstanceState != null) {

        }
    }

    public void setActionBarTitle(String title) {
        actionBar.setTitle(title);
    }

    public void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cube_menu, menu);
        MenuItem saveButton = menu.findItem(R.id.save);
        saveButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.goHome:
                goToHome();
                return true;
            case R.id.logout:
                mAuth.signOut();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteractionStepOne(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void onFragmentInteractionStepTwo(String title) {
        toolbar.setTitle(title);
        Timber.tag("fart").i(title);
    }

    @Override
    public void onFragmentCubeReviewInteraction(Bundle bundle) {
        if(bundle.getString(AllMyConstants.CUBE_NAME) != null) {
            toolbar.setTitle(bundle.getString(AllMyConstants.CUBE_NAME));
        }

        if(bundle.getString(AllMyConstants.TOAST_MESSAGE) != null) {
            Toast.makeText(this, bundle.getString(AllMyConstants.TOAST_MESSAGE) , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    class RetrieveMtgStuff extends AsyncTask<String, Void, Card> {
        private Exception exception;

        @Override
        protected Card doInBackground(String... strings) {
            try {
                int multiverseId = 461119;
                Card card = CardAPI.getCard(multiverseId);

              Timber.tag("fart").i("card name: %s", card.getName());
              Timber.tag("fart").i("card cmc: %s", card.getCmc());
              Timber.tag("fart").i("card color identity: %s", card.getColorIdentity());
              Timber.tag("fart").i("card image url: %s", card.getImageUrl());
              Timber.tag("fart").i("card original text: %s", card.getOriginalText());
              Timber.tag("fart").i("card flavor text: %s", card.getFlavor());
              return card;
            } catch (Exception e) {
                this.exception = e;
                return null;
            } finally {
                Timber.tag("fart").i("done");
            }
        }
    }
}
