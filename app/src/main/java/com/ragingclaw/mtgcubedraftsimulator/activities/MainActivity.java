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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ragingclaw.mtgcubedraftsimulator.BuildConfig;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.NotLoggingTree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

// TODO: i screwed up. Remake the database insert from crawling the set files. sigh.
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_new_cube) com.google.android.material.button.MaterialButton newCubeButton;
    @BindView(R.id.btn_my_cubes) com.google.android.material.button.MaterialButton myCubesButton;
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
        getFragmentManager().popBackStack();

        magicCardViewModel = ViewModelProviders.of(this).get(MagicCardViewModel.class);

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
                insertData();
            }
        });
    }

    public void insertData() {
        new Thread(new Runnable() {
            @Override
            public void run() { String [] list;
                String json = null;
                String baseImageUrl = "https://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=";
                String imageUrlArgs = "&type=card";

                Gson converter = new Gson();
                Type type = new TypeToken<ArrayList<String>>(){}.getType();

                try {
                    list = getAssets().list("");
                    if (list.length > 0) {
                        // This is a folder
                        for (String file : list) {
                            InputStream is = getAssets().open(file);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            is.read(buffer);
                            is.close();
                            json = new String(buffer, "UTF-8");
                            JSONObject obj = new JSONObject(json);

                            Timber.tag("fart").i("***** file: %s", file);
                            JSONArray cardsArray = obj.getJSONArray("cards");

                            for(int i = 0; i < cardsArray.length(); i++) {
                                JSONObject cardObj = cardsArray.getJSONObject(i);

                                if(i > 0 && i < 10) {

                                }


                                if (cardObj.has("multiverseId")) {
                                    int multiverseId = cardObj.optInt("multiverseId");
                                    String id = cardObj.optString("id");
                                    String layout = cardObj.optString("layout");
                                    String name = cardObj.optString("name");
                                    ArrayList<String> names = converter.fromJson(String.valueOf(cardObj.optJSONArray("names")), type);
                                    String manaCost = cardObj.optString("manaCost");
                                    Double convertedManaCost = cardObj.optDouble("convertedManaCost");
                                    ArrayList<String> colors = converter.fromJson(String.valueOf(cardObj.optJSONArray("colors")), type);
                                    ArrayList<String> colorIdentity = converter.fromJson(String.valueOf(cardObj.optJSONArray("colorIdentity")), type);
                                    String creatureTrype = cardObj.optString("type");
                                    ArrayList<String> supertypes = converter.fromJson(String.valueOf(cardObj.optJSONArray("supertypes")), type);
                                    ArrayList<String> types = converter.fromJson(String.valueOf(cardObj.optJSONArray("types")), type);
                                    ArrayList<String> subtypes = converter.fromJson(String.valueOf(cardObj.optJSONArray("subtypes")), type);
                                    String rarity = cardObj.optString("rarity");
                                    String text = cardObj.optString("text");
                                    String originalText = cardObj.optString("originalText");
                                    String flavorText = cardObj.optString("flavorText");
                                    String artist = cardObj.optString("artist");
                                    String number = cardObj.optString("number");
                                    String power = cardObj.optString("power");
                                    String toughness = cardObj.optString("toughness");
                                    String loyalty = cardObj.optString("loyalty");
                                    String border = cardObj.optString("border");
                                    String releaseDate = cardObj.optString("releaseDate");
                                    String setCode = obj.optString("code");
                                    String setName = obj.optString("mcmName");
                                    String imageUrl = baseImageUrl + multiverseId + imageUrlArgs;

                                    MagicCard card = new MagicCard(
                                            multiverseId, id, layout, name, names, manaCost, convertedManaCost,
                                            colors, colorIdentity, creatureTrype, supertypes,
                                            types, subtypes, rarity, text, originalText, flavorText,
                                            artist, number, power, toughness, loyalty, border,
                                            releaseDate, setCode, setName, imageUrl);

                                    magicCardViewModel.insertCard(card);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void goToLogin() {
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