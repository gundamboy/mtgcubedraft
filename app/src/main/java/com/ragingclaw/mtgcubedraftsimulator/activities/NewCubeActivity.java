package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.ragingclaw.mtgcubedraftsimulator.R;

import java.util.List;

import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.api.SetAPI;
import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

public class NewCubeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cube);

//        MTGThread thread = new MTGThread();
//        thread.run();
        new RetrieveMtgStuff().execute("");

    }

    public void startThread(View view) {

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread(View view) {

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

    class MTGThread extends Thread {

        @Override
        public void run() {
            int multiverseId = 461119;
            Card card = CardAPI.getCard(multiverseId);

            Timber.tag("fart").i("card: %s", card.getLegalities());
        }
    }
}
