package com.ragingclaw.mtgcubedraftsimulator.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview;
import com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.EndGameFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.MainActivityFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.MyCubesFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewCubeBuilderFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewCubeStepOneFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewDraftBuilderFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.SingleCardDisplayFragment;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

// TODO: i screwed up. Remake the database insert from crawling the set files. sigh.
public class MainActivity extends AppCompatActivity implements
        MainActivityFragment.OnMainActivityFragmentInteraction,
        NewCubeStepOneFragment.OnFragmentInteractionListenerStepOne,
        NewCubeBuilderFragment.OnFragmentInteractionListenerStepTwo,
        CubeCardsReview.OnCubeReviewFragmentInteractionListener,
        MyCubesFragment.OnMyCubesFragmentInteraction,
        NewDraftBuilderFragment.OnBuildDraftFragmentInteractionListener,
        DraftingHappyFunTimeFragment.OnDraftingHappyFunTimeInteraction,
        SingleCardDisplayFragment.OnSingleCardFragmentInteractionListener,
        EndGameFragment.OnEndGameFragmentInteractionListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    ActionBar actionBar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private MagicCardViewModel magicCardViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getFragmentManager().popBackStack();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();

        // take care of widget stuff
        Intent intent = getIntent();
        if(intent != null) {
            Timber.tag("fart").i("intent is not null");
            if (intent.hasExtra(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE)) {
                if(intent.getAction().equals(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE)) {

                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
//                    newCubeButton.setPressed(true);
//                    newCubeButton.invalidate();
//                    newCubeButton.performClick();
//                    newCubeButton.invalidate();
                            //goToNewCube(view);
                        }
                    };
                    handler.postDelayed(r, 0);
                }
            }
        } else {
            Timber.tag("fart").i("intent IS null");
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new NotLoggingTree());
        }

        toolbar.setTitle(getString(R.string.main_activity_title));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            goToLogin();
        }

    }
    public void goToNewCube(View view) {

    }

    public void goToMyCubes(View view) {
        Navigation.findNavController(view).navigate(R.id.action_hostFragment_to_myCubesFragment);
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
        getMenuInflater().inflate(R.menu.logout_menu, menu);
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
    public void onMainActivityFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentCubeReviewInteraction(Bundle bundle) {
        if(bundle.getString(AllMyConstants.CUBE_NAME) != null) {
            setActionBarTitle(bundle.getString(AllMyConstants.CUBE_NAME));
        }

        if(bundle.getString(AllMyConstants.TOAST_MESSAGE) != null) {
            Toast.makeText(this, bundle.getString(AllMyConstants.TOAST_MESSAGE) , Toast.LENGTH_SHORT).show();
        }
    }

    // these are fragment interaction listeners so the activity can
    // interact with the current fragment

    @Override
    public void onMyCubesFragmentInteraction(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void onFragmentInteractionStepOne(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void onFragmentInteractionStepTwo(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void OnBuildDraftFragmentInteractionListener(Uri uri) { }

    @Override
    public void onDraftingHappyFunTimeInteraction(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void onSingleCardFragmentInteraction(Uri uri) { }

    @Override
    public void onEndGameFragmentInteraction(String title) { setActionBarTitle(title); }

    @Override
    public void onBackPressed() {
        
        super.onBackPressed();
    }
}