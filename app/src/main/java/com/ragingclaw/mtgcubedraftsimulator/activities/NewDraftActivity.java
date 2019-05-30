package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewDraftBuilderFragment;
import com.ragingclaw.mtgcubedraftsimulator.fragments.NewDraftStepOneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewDraftActivity extends AppCompatActivity implements NewDraftBuilderFragment.OnBuildDraftFragmentInteractionListener,
        NewDraftStepOneFragment.OnMyDraftStepOneFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ActionBar actionBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_draft);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        toolbar.setTitle(getString(R.string.my_drafts_activity_title));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
    }

    private void goToLogin() {
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
    public void OnBuildDraftFragmentInteractionListener(Uri uri) {

    }

    @Override
    public void OnMyDraftStepOneFragmentInteractionListener(Uri uri) {

    }
}
