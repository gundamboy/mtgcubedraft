package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.database.User;
import com.ragingclaw.mtgcubedraftsimulator.fragments.FragmentCubeReview;
import com.ragingclaw.mtgcubedraftsimulator.fragments.MyCubesFragment;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.DraftViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.UserViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCubesActivity extends AppCompatActivity implements MyCubesFragment.OnMyCubesFragmentInteraction, FragmentCubeReview.OnCubeReviewFragmentInteractionListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    ActionBar actionBar;
    NavHostFragment navHostFragment;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cubes);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        toolbar.setTitle(getString(R.string.my_cubes_activity_title));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
    }

    public void setActionBarTitle(String title) {
        actionBar.setTitle(title);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
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
            case R.id.logout:
                mAuth.signOut();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMyCubesFragmentInteraction(String title) {
        setActionBarTitle(title);
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
}
