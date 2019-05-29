package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.fragments.MyDraftsFragment;

public class MyDraftsActivity extends AppCompatActivity implements MyDraftsFragment.OnMyDraftsInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drafts);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
