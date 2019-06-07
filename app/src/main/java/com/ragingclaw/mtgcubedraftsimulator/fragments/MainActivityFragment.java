package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.ragingclaw.mtgcubedraftsimulator.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivityFragment extends Fragment {
    @BindView(R.id.btn_new_cube) com.google.android.material.button.MaterialButton newCubeButton;
    @BindView(R.id.btn_my_cubes) com.google.android.material.button.MaterialButton myCubesButton;
    @BindView(R.id.mainLayout) LinearLayout mainLayout;
    @BindView(R.id.no_network) LinearLayout noNetworkLayout;
    private Unbinder unbinder;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private OnMainActivityFragmentInteraction mListener;
    private boolean isDataLoaded = false;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        unbinder = ButterKnife.bind(this, view);

        sendDataToActivity(Objects.requireNonNull(getActivity()).getResources().getString(R.string.main_activity_title));

        if(!NetworkUtils.isOnline(getActivity())) {
            noNetworkLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            } else {

            // set up preferences and user stuff
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mEditor = mPreferences.edit();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            // view model for database stuff
            MagicCardViewModel magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);

            magicCardViewModel.getmAllCards().observe(this, magicCards -> {
                if (!isDataLoaded) {
                    if (magicCards.size() > 0) {
                        isDataLoaded = true;

                        // check to see if the last batch of draft cards still exists for some reason.
                        // kill it if it does.
                        if (mPreferences.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                            mEditor.remove(AllMyConstants.THE_CHOSEN_CARDS);
                        }

                        mEditor.putBoolean(AllMyConstants.IS_DATA_LOADED, true);
                        mEditor.apply();

                        if (isDataLoaded) {
                            newCubeButton.setEnabled(true);
                            myCubesButton.setEnabled(true);
                        } else {
                            newCubeButton.setEnabled(false);
                            myCubesButton.setEnabled(false);
                        }
                    }
                }
            });


            newCubeButton.setOnClickListener(v -> goToNewCube(view));

            myCubesButton.setOnClickListener(v -> goToMyCubes(view));
        }

        return view;
    }

    public void goToNewCube(View view) {
        Navigation.findNavController(view).navigate(R.id.action_hostFragment_to_newCubeStepOneFragment);
    }

    public void goToMyCubes(View view) {
        Navigation.findNavController(view).navigate(R.id.action_hostFragment_to_myCubesFragment);
    }

    public void sendDataToActivity(String string) {
        if (mListener != null) {
            mListener.onMainActivityFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainActivityFragmentInteraction) {
            mListener = (OnMainActivityFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getActivity().getString(R.string.fragment_interaction_error_end_text));
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
        void onMainActivityFragmentInteraction(String string);
    }
}
