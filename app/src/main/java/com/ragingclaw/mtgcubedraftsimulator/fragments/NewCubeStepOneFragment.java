package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.ragingclaw.mtgcubedraftsimulator.widget.CubeDraftWidgetProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListenerStepOne} interface
 * to handle interaction events.
 * Use the {@link NewCubeStepOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCubeStepOneFragment extends Fragment {
    @BindView(R.id.step1) androidx.constraintlayout.widget.ConstraintLayout stepOneLayout;
    @BindView(R.id.building_cube_frame) LinearLayout building_cube_frame;
    @BindView(R.id.btn_generate_cube) com.google.android.material.button.MaterialButton generateCubeButton;
    @BindView(R.id.cube_name_edit_text) TextInputEditText cubeName;
    @BindView(R.id.creating_cube_static_text) TextView gatheringCardsTextView;
    @BindView(R.id.percentage_built) TextView completePercent;
    private Unbinder unbinder;

    private Bundle handlerBundle = new Bundle();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private OnFragmentInteractionListenerStepOne mListener;

    public NewCubeStepOneFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepOneFragment newInstance() {
        NewCubeStepOneFragment fragment = new NewCubeStepOneFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // standard stuff. validate input and move to the next fragment

        View view = inflater.inflate(R.layout.fragment_new_cube_step_one, container, false);
        unbinder = ButterKnife.bind(this, view);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(building_cube_frame.getVisibility() == View.VISIBLE) {
            stepOneLayout.setVisibility(View.VISIBLE);
            building_cube_frame.setVisibility(View.GONE);
        }

        generateCubeButton.setOnClickListener(v -> {
            // validate name

            if(!TextUtils.isEmpty(Objects.requireNonNull(cubeName.getText()).toString())) {

                // try and hide the keyboard when the button is pressed
                try  {
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stepOneLayout.setVisibility(View.GONE);
                building_cube_frame.setVisibility(View.VISIBLE);
                buildCube(view, cubeName.getText().toString());


            } else {
                // show toast because i dont have time for fancy shit.
                Toast.makeText(getContext(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.the_cube_has_no_name), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private int getRandomNum(int max) {
        Random r = new Random();
        return r.nextInt((max - 1) + 1);
    }


    private void buildCube(View view, String cubeName) {
        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        // updates the % completed in the layout
        // update widget
        Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // updates the % completed in the layout
                handlerBundle = msg.getData();
                String percent = handlerBundle.getString(AllMyConstants.PERCENT_DONE) + "%";

                if (handlerBundle.containsKey(AllMyConstants.SWAP_TEXT)) {
                    if (handlerBundle.getBoolean(AllMyConstants.SWAP_TEXT)) {
                        gatheringCardsTextView.setText(Objects.requireNonNull(getActivity()).getResources().getString(R.string.creating_cube));
                    }
                }

                if (handlerBundle.containsKey(AllMyConstants.CUBE_NAMES)) {
                    if (Objects.requireNonNull(handlerBundle.getStringArrayList(AllMyConstants.CUBE_NAMES)).size() > 0) {
                        // update widget

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                new ComponentName(Objects.requireNonNull(getActivity()), CubeDraftWidgetProvider.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.cubes_widget_list);
                    }
                }

                if (handlerBundle.containsKey(AllMyConstants.MOVE_ALONG)) {
                    if (handlerBundle.getBoolean(AllMyConstants.MOVE_ALONG)) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AllMyConstants.CUBE_CARDS, handlerBundle.getParcelable(AllMyConstants.CUBE_CARDS));
                        bundle.putString(AllMyConstants.CUBE_NAME, handlerBundle.getString(AllMyConstants.CUBE_NAME));
                        bundle.putInt(AllMyConstants.CUBE_ID, handlerBundle.getInt(AllMyConstants.CUBE_ID));
                        Navigation.findNavController(view).navigate(R.id.action_newCubeStepOneFragment_to_cubeCardsReview, bundle);
                    }
                }
                completePercent.setText(percent);
            }
        };

        Thread t = new Thread(new BuildCube(handler, view, mPreferences, mEditor, cubeName));
        t.start();
    }

    public class BuildCube implements Runnable {
        // builds the cube and ships it off

        final Handler handler;
        final View view;
        final SharedPreferences mPreferences;
        SharedPreferences.Editor mEditor;
        final String cubeName;


        BuildCube(Handler handler, View view, SharedPreferences mPreferences, SharedPreferences.Editor mEditor, String cubeName) {
            this.handler = handler;
            this.view = view;
            this.mPreferences = mPreferences;
            this.mEditor = mEditor;
            this.cubeName = cubeName;
        }

        @Override
        public void run() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            CubeViewModel cubeViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(CubeViewModel.class);
            MagicCardViewModel magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
            List<MagicCard> allCards = magicCardViewModel.getAllCardsStatic();
            List<MagicCard> cubeCards = new ArrayList<>();
            ArrayList<Integer> cardIds = new ArrayList<>();
            Set<String> names = new HashSet<>();
            Set<String> ids = new HashSet<>();

            // a cube is 360 cards.
            int cubeSize = 360;

            for (int i = 0; i < cubeSize; i++) {
                int r = getRandomNum(allCards.size());
                cubeCards.add(allCards.get(r));
                cardIds.add(allCards.get(r).getMultiverseid());
                ids.add(String.valueOf(allCards.get(r).getMultiverseid()));
                allCards.remove(r);

                // uhg, math.
                String percent = String.valueOf((i * 100) / cubeSize);

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString(AllMyConstants.PERCENT_DONE, percent);
                b.putBoolean(AllMyConstants.SWAP_TEXT, false);
                m.setData(b);
                handler.sendMessage(m);

            }

            if (cubeCards.size() == cubeSize) {
                Cube cube = new Cube(0, currentUserId, cubeName, cubeSize, cardIds);

                long cubeId = cubeViewModel.insertCubeWithReturn(cube);
                int theId = (int) cubeId;


                List<Cube> cubes = cubeViewModel.getUserCubesStatic(currentUserId);
                ArrayList<String> allCubeNames = new ArrayList<>();

                boolean doSwapText = true;

                // get names for prefs to set the widget
                int count = 0;
                for (Cube c : cubes) {
                    names.add(c.getCube_name());
                    allCubeNames.add(c.getCube_name());
                    count++;

                    // uhg, math.
                    String percent = String.valueOf((count * 100) / cubes.size());

                    Message m = Message.obtain();
                    Bundle b = new Bundle();

                    if(doSwapText) {
                        b.putBoolean(AllMyConstants.SWAP_TEXT, true);
                        doSwapText = false;
                    }

                    b.putString(AllMyConstants.PERCENT_DONE, percent);
                    m.setData(b);
                    handler.sendMessage(m);

                }
                mEditor = mPreferences.edit();

                if(mPreferences.contains(AllMyConstants.CUBE_NAMES)) {
                    if (Objects.requireNonNull(mPreferences.getStringSet(AllMyConstants.CUBE_NAMES, null)).size() > 0) {
                        mEditor.remove(AllMyConstants.CUBE_NAMES);
                    }
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> cubeToInsert = new HashMap<>();
                cubeToInsert.put(getActivity().getString(R.string.insert_to_fb_ids), cardIds);
                cubeToInsert.put(getActivity().getString(R.string.insert_to_fb_cubeId), cubeId);
                cubeToInsert.put(getActivity().getString(R.string.insert_to_fb_cubeName), cubeName);
                cubeToInsert.put(getActivity().getString(R.string.insert_to_fb_totalCards), 360);
                cubeToInsert.put(getActivity().getString(R.string.insert_to_fb_uid), currentUserId);

                db.collection(getActivity().getString(R.string.firebase_cube_collection_path_name)).add(cubeToInsert)
                .addOnSuccessListener(documentReference -> {
                    // firebase says this needs to be here
                }).addOnFailureListener(e -> {
                    // firebase says this needs to be here
                });

                mEditor.putInt(AllMyConstants.CUBE_ID, theId);
                mEditor.remove(AllMyConstants.CUBE_NAMES);
                mEditor.putStringSet(AllMyConstants.CUBE_NAMES, names);
                mEditor.putStringSet(AllMyConstants.CUBE_IDS, ids);
                mEditor.apply();

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString(AllMyConstants.PERCENT_DONE, getActivity().getString(R.string.one_hundred_percent));
                b.putBoolean(AllMyConstants.MOVE_ALONG, true);
                //b.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
                b.putStringArrayList(AllMyConstants.CUBE_NAMES, allCubeNames);
                b.putString(AllMyConstants.CUBE_NAME, cubeName);
                b.putInt(AllMyConstants.CUBE_ID, theId);
                m.setData(b);
                handler.sendMessage(m);
            }
        }
    }
    public void sendDataToActivity(String title) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepOne(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerStepOne) {
            mListener = (OnFragmentInteractionListenerStepOne) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getActivity().getString(R.string.fragment_interaction_error_end_text));        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(building_cube_frame.getVisibility() == View.VISIBLE) {
            stepOneLayout.setVisibility(View.VISIBLE);
            building_cube_frame.setVisibility(View.GONE);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListenerStepOne {
        void onFragmentInteractionStepOne(String title);
    }
}
