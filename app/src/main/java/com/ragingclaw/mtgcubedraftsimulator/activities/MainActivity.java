package com.ragingclaw.mtgcubedraftsimulator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_new_cube) com.google.android.material.button.MaterialButton newCubeButton;
    @BindView(R.id.btn_my_cubes) com.google.android.material.button.MaterialButton myCubesButton;
    @BindView(R.id.btn_new_draft) com.google.android.material.button.MaterialButton newDraftButton;
    @BindView(R.id.btn_my_drafts) com.google.android.material.button.MaterialButton myDraftsButton;
    @BindView(R.id.insetData) com.google.android.material.button.MaterialButton mInsertData;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private MagicCardViewModel magicCardViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new NotLoggingTree());
        }

        toolbar.setTitle(getString(R.string.main_activity_title));;
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            goToLogin();
        }


        newCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewCubeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myCubesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyCubesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        newDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewDraftActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myDraftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyDraftsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Timber.tag("fart").e("up was pressed");
                return true;
            case R.id.logout:
                mAuth.signOut();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}