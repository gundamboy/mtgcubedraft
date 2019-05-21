package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.api.SetAPI;
import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

public class NewCubeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cube);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.new_cube_activity_title));
        } else {
            Timber.tag("fart").i("actionbar is null... wtf");
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
        int id = item.getItemId();

        //logout
        if (id == R.id.logout) {
            mAuth.signOut();
            goToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
