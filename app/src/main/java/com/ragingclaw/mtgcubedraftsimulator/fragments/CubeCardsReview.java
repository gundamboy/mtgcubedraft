package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.CubeAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.ragingclaw.mtgcubedraftsimulator.widget.CubeDraftWidgetProvider;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview.OnCubeReviewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CubeCardsReview extends Fragment {
    @BindView(R.id.cube_cards_recyclerview) RecyclerView mCardsRecyclerView;
    @BindView(R.id.mainLayout) androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    @BindView(R.id.blocker) androidx.constraintlayout.widget.ConstraintLayout blockerLayout;
    @BindView(R.id.create_draft_button) com.google.android.material.button.MaterialButton mCreateDraftButton;
    @BindView(R.id.cube_go_to_my_cubes_button) com.google.android.material.button.MaterialButton mGoToMyCubesButton;
    @BindView(R.id.cube_multi_function_button) com.google.android.material.button.MaterialButton mCubeMultiFunctionButton;
    private Unbinder unbinder;
    private MagicCardViewModel magicCardViewModel;
    private com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview.OnCubeReviewFragmentInteractionListener mListener;
    private List<MagicCard> cubeCards;
    private CubeViewModel cubeViewModel;
    private CubeAdapter cubeAdapter;
    private String cubeName = "";
    private int cubeId = 0;
    private Bundle handlerBundle = new Bundle();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    private View view;

    final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key.equals(AllMyConstants.CUBE_ID)) {
                cubeId = prefs.getInt(AllMyConstants.CUBE_ID, 0);
                if (cubeId!= 0) {
                    mCreateDraftButton.setEnabled(true);
                }
            }

            if(key.equals(AllMyConstants.CUBE_NAMES)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(Objects.requireNonNull(getActivity()), CubeDraftWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.cubes_widget_list);
            }
        }
    };

    public CubeCardsReview() {
        // Required empty public constructor
    }


    public static CubeCardsReview newInstance() {

        return new CubeCardsReview();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        cubeName = mPreferences.getString(AllMyConstants.CUBE_NAME, null);
        String toastMessage = mPreferences.getString(AllMyConstants.TOAST_MESSAGE, null);

        if (getArguments() != null) {
            if(savedInstanceState != null) {
                cubeName = savedInstanceState.getString(AllMyConstants.CUBE_NAME);
                cubeId = savedInstanceState.getInt(AllMyConstants.CUBE_ID);
            } else {
                cubeName = getArguments().getString(AllMyConstants.CUBE_NAME);
                cubeId = getArguments().getInt(AllMyConstants.CUBE_ID);
            }

            // send the cube name back to the activity so it can be set in the actionbar
            if(mListener != null) {
                Bundle bundle = new Bundle();
                bundle.putString(AllMyConstants.CUBE_NAME, cubeName);
                bundle.putString(AllMyConstants.TOAST_MESSAGE, null);
                sendDataToActivity(bundle);
            }
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cube_card_review, container, false);
        unbinder = ButterKnife.bind(this, view);

        mainLayout.setVisibility(View.GONE);
        blockerLayout.setVisibility(View.VISIBLE);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            cubeName = savedInstanceState.getString(AllMyConstants.CUBE_NAME);
            cubeId = savedInstanceState.getInt(AllMyConstants.CUBE_ID);
            cubeCards = Parcels.unwrap(savedInstanceState.getParcelable(AllMyConstants.CUBE_CARDS));
        }

        if(mPreferences.contains(AllMyConstants.CUBE_ID)) {
            cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        }

        // standard stuff. firebase user id, RecyclerView set up.
        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mCardsRecyclerView.setLayoutManager(linearLayoutManager);
        mCardsRecyclerView.setHasFixedSize(true);

        cubeAdapter = new CubeAdapter();
        mCardsRecyclerView.setAdapter(cubeAdapter);

        magicCardViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MagicCardViewModel.class);
        cubeViewModel = ViewModelProviders.of(getActivity()).get(CubeViewModel.class);

        // magic stuff. both conditions set the adapter, but in different ways.
        mCubeMultiFunctionButton.setText(getString(R.string.delete_cube_button_text));
        getMyCube(cubeId);

        if(mPreferences.contains(AllMyConstants.CUBE_ID)) {
            cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        }

        // some click listeners
        mCreateDraftButton.setOnClickListener(v -> {
            Objects.requireNonNull(getArguments()).remove(AllMyConstants.CUBE_CARDS);
            Bundle bundle = new Bundle();
            bundle.putInt(AllMyConstants.CUBE_ID, cubeId);
            Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_newDraftBuilderFragment, bundle);
        });

        mGoToMyCubesButton.setOnClickListener(v -> {
            Objects.requireNonNull(getArguments()).remove(AllMyConstants.CUBE_CARDS);
            Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_myCubesFragment);
        });

        // this is the start over or the delete button. both go back to the main fragment
        mCubeMultiFunctionButton.setOnClickListener(v -> {
            // if the user came from the list of cubes, offer a delete option
            new DeleteCube(getActivity(), cubeId, mAuth, currentUserId, cubeViewModel, mPreferences, mEditor).execute();

            // good bye cruel world, I quit.
            Objects.requireNonNull(getArguments()).remove(AllMyConstants.CUBE_CARDS);
            Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_hostFragment);
        });

        return view;
    }

    public void killBlocker() {
        mainLayout.setVisibility(View.VISIBLE);
        blockerLayout.setVisibility(View.GONE);
    }

    private void getMyCube(int cubeId) {
        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        // blocks the user from seeing the recyclerView loading. the amount of data its taking in makes it slow.
        Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();

                List<MagicCard> cards = Parcels.unwrap(handlerBundle.getParcelable(AllMyConstants.CUBE_CARDS));
                cubeAdapter.setCards(cards);

                // blocks the user from seeing the recyclerView loading. the amount of data its taking in makes it slow.
                killBlocker();
            }
        };

        Thread t = new Thread(new GetUserCubeCardsRunnable(handler, cubeId, currentUserId));
        t.start();
    }

    private void sendDataToActivity(Bundle bundle) {
        // sends the listener info back to the activity. I know the method name was confusing...
        if (mListener != null) {
            mListener.onFragmentCubeReviewInteraction(bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview.OnCubeReviewFragmentInteractionListener) {
            mListener = (com.ragingclaw.mtgcubedraftsimulator.fragments.CubeCardsReview.OnCubeReviewFragmentInteractionListener) context;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();

        // store the recyclerViews scroll position
        mBundleRecyclerViewState = new Bundle();
        mListState = Objects.requireNonNull(mCardsRecyclerView.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // restore the scroll position of the recyclerView
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(() -> {
                mListState = mBundleRecyclerViewState.getParcelable(AllMyConstants.RECYCLER_RESTORE);
                Objects.requireNonNull(mCardsRecyclerView.getLayoutManager()).onRestoreInstanceState(mListState);
            }, 50);
        }


        mCardsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AllMyConstants.CUBE_NAME, cubeName);
        outState.putInt(AllMyConstants.CUBE_NAME, cubeId);
        outState.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
    }

    public interface OnCubeReviewFragmentInteractionListener {
        void onFragmentCubeReviewInteraction(Bundle bundle);
    }

    public class GetUserCubeCardsRunnable implements Runnable {
        final int cubeId;
        final String userId;
        final Handler handler;

        public GetUserCubeCardsRunnable(Handler handler, int cubeId, String userId) {
            this.handler = handler;
            this.cubeId = cubeId;
            this.userId = userId;
        }

        @Override
        public void run() {
            // some database operations and send a message back to the handler to display the list

            Cube userCube = cubeViewModel.getmUserCube(userId, cubeId);
            List<Integer> cardIds = userCube.getCard_ids();
            List<MagicCard> cards = new ArrayList<>();

            for(int id : cardIds) {
                cards.add(magicCardViewModel.getmCard(id));
            }

            if (cards.size() == cardIds.size()) {
                Message m = Message.obtain();
                Bundle b = new Bundle();
                b.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cards));
                m.setData(b);
                handler.sendMessage(m);
            }
        }
    }

    public static class DeleteCube extends AsyncTask<Void, Void, Void> {
        // save the cube info in the database
        @SuppressLint("StaticFieldLeak")
        final
        Context context;
        final int cubeId;
        final CubeViewModel cubeViewModel;
        final FirebaseAuth mAuth;
        final String currentUserId;
        final SharedPreferences mPreferences;
        SharedPreferences.Editor mEditor;

        public DeleteCube(Context context, int cubeId, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel, SharedPreferences mPreferences, SharedPreferences.Editor mEditor) {
            this.context = context;
            this.cubeId = cubeId;
            this.cubeViewModel = cubeViewModel;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
            this.mPreferences = mPreferences;
            this.mEditor = mEditor;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // get all cards from adapter/recyclerView

            Cube userCube = cubeViewModel.getmUserCube(currentUserId, cubeId);
            cubeViewModel.deleteCube(userCube);

            Set<String> names = new HashSet<>();

            List<Cube> cubes = cubeViewModel.getUserCubesStatic(currentUserId);
            for (Cube c : cubes) {
                if (c.getCubeId() != userCube.getCubeId()) {
                    names.add(c.getCube_name());
                }
            }

            mEditor = mPreferences.edit();
            mEditor.remove(AllMyConstants.CUBE_NAMES);
            mEditor.putStringSet(AllMyConstants.CUBE_NAMES, names);
            mEditor.apply();

            return null;
        }
    }
}