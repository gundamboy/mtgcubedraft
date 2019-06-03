package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivityFragment extends Fragment {
    @BindView(R.id.btn_new_cube) com.google.android.material.button.MaterialButton newCubeButton;
    @BindView(R.id.btn_my_cubes) com.google.android.material.button.MaterialButton myCubesButton;
    @BindView(R.id.insetData) com.google.android.material.button.MaterialButton mInsertData;
    private Unbinder unbinder;
    private MagicCardViewModel magicCardViewModel;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private OnMainActivityFragmentInteraction mListener;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    public static MainActivityFragment newInstance(String param1, String param2) {
        MainActivityFragment fragment = new MainActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        unbinder = ButterKnife.bind(this, view);

        // take care of widget stuff
        Intent intent = getActivity().getIntent();
        if(intent.getAction().equals(AllMyConstants.WIDGET_INTENT_ACTION_NEW_CUBE)) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    newCubeButton.setPressed(true);
                    newCubeButton.invalidate();
                    newCubeButton.performClick();
                    newCubeButton.invalidate();
                }
            };
            handler.postDelayed(r, 0);

        } else  if(intent.getAction().equals(AllMyConstants.WIDGET_INTENT_ACTION_MY_CUBES)) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    myCubesButton.setPressed(true);
                    myCubesButton.invalidate();
                    myCubesButton.performClick();
                    myCubesButton.invalidate();
                }
            };
            handler.postDelayed(r, 0);
        }

        // set up preferences and user stuff
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.commit();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // view model for database stuff
        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);

        newCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewCube(view);
            }
        });

        myCubesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyCubes(view);
            }
        });

        mInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        return view;
    }

    public void goToNewCube(View view) {
        Navigation.findNavController(view).navigate(R.id.action_hostFragment_to_newCubeStepOneFragment);
    }

    public void goToMyCubes(View view) {
        Navigation.findNavController(view).navigate(R.id.action_hostFragment_to_myCubesFragment);
    }

    public void sendDataToActivity(Uri uri) {
        if (mListener != null) {
            mListener.onMainActivityFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainActivityFragmentInteraction) {
            mListener = (OnMainActivityFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainActivityFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnMainActivityFragmentInteraction {
        // TODO: Update argument type and name
        void onMainActivityFragmentInteraction(Uri uri);
    }

    public void insertData() {
        // database card insertion.

        new Thread(new Runnable() {
            @Override
            public void run() { String [] list;
                String json = null;
                String baseImageUrl = "https://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=";
                String imageUrlArgs = "&type=card";

                Gson converter = new Gson();
                Type type = new TypeToken<ArrayList<String>>(){}.getType();

                try {
                    list = getActivity().getAssets().list("");
                    if (list.length > 0) {
                        // This is a folder
                        for (String file : list) {
                            InputStream is = getActivity().getAssets().open(file);
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
}
