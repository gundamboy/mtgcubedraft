package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.LoginActivity;
import com.ragingclaw.mtgcubedraftsimulator.activities.MainActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.CubeAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CubeCardsReview.OnCubeReviewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CubeCardsReview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CubeCardsReview extends Fragment {
    @BindView(R.id.cube_cards_recyclerview) RecyclerView mCardsRecyclerView;
    @BindView(R.id.create_draft_button) com.google.android.material.button.MaterialButton mCreateDraftButton;
    @BindView(R.id.cube_go_to_my_cubes_button) com.google.android.material.button.MaterialButton mGoToMyCubesButton;
    @BindView(R.id.cube_multi_function_button) com.google.android.material.button.MaterialButton mCubeMultiFunctionButton;
    private Unbinder unbinder;
    private LinearLayoutManager linearLayoutManager;
    private MagicCardViewModel magicCardViewModel;
    private OnCubeReviewFragmentInteractionListener mListener;
    private List<MagicCard> cubeCards;
    private CubeViewModel cubeViewModel;
    private boolean isSaved = false;
    private boolean isSingle = false;
    private CubeAdapter cubeAdapter;
    private String cubeName = "";
    private int cubeId = 0;
    String toastMessage;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int firstVisiblePosition;

    private FirebaseAuth mAuth;
    private String currentUserId;


    public CubeCardsReview() {
        // Required empty public constructor
    }


    public static CubeCardsReview newInstance() {
        CubeCardsReview fragment = new CubeCardsReview();

        return fragment;
    }


    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key.equals(AllMyConstants.CUBE_ID)) {
                cubeId = prefs.getInt(AllMyConstants.CUBE_ID, 0);
                if (cubeId!= 0) {
                    mCreateDraftButton.setEnabled(true);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        Map<String,?> keys = mPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Timber.tag("fart").i("key: %s, value: %s", entry.getKey(), entry.getValue());
        }

        cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        cubeName = mPreferences.getString(AllMyConstants.CUBE_NAME, null);
        toastMessage = mPreferences.getString(AllMyConstants.TOAST_MESSAGE, null);
        isSaved = mPreferences.getBoolean(AllMyConstants.IS_SAVED, false);
        isSingle = mPreferences.getBoolean(AllMyConstants.IS_SINGLE, false);

        if (getArguments() != null) {
            if(savedInstanceState != null) {
                cubeName = savedInstanceState.getString(AllMyConstants.CUBE_NAME);
                cubeId = savedInstanceState.getInt(AllMyConstants.CUBE_ID);
            } else {
                cubeName = getArguments().getString(AllMyConstants.CUBE_NAME);
                cubeId = getArguments().getInt(AllMyConstants.CUBE_ID);
            }

            Timber.tag("fart").i("isSingle: %s", isSingle);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cube_card_review, container, false);
        unbinder = ButterKnife.bind(this, view);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            cubeName = savedInstanceState.getString(AllMyConstants.CUBE_NAME);
            cubeId = savedInstanceState.getInt(AllMyConstants.CUBE_ID);
            cubeCards = Parcels.unwrap(savedInstanceState.getParcelable(AllMyConstants.CUBE_CARDS));
            isSingle = savedInstanceState.getBoolean(AllMyConstants.IS_SINGLE);
            isSaved = savedInstanceState.getBoolean(AllMyConstants.IS_SAVED);
        }

        if(mPreferences.contains(AllMyConstants.CUBE_ID)) {
            cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        }

        // standard stuff. firebase user id, RecyclerView set up.
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mCardsRecyclerView.setLayoutManager(linearLayoutManager);
        mCardsRecyclerView.setHasFixedSize(true);

        cubeAdapter = new CubeAdapter();
        mCardsRecyclerView.setAdapter(cubeAdapter);

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        cubeViewModel = ViewModelProviders.of(getActivity()).get(CubeViewModel.class);

        // magic stuff. both conditions set the adapter, but in different ways.
        if (getArguments().getBoolean(AllMyConstants.NEW_CUBE)) {
            cubeCards = Parcels.unwrap(getArguments().getParcelable(AllMyConstants.CUBE_CARDS));
            cubeAdapter.setCards(cubeCards);
        } else {
            isSingle = true;
            mCubeMultiFunctionButton.setText(getString(R.string.delete_cube_button_text));
            getMyCube(cubeId);
        }

        if(isSingle) {
            mCreateDraftButton.setEnabled(true);
        } else {
            mCreateDraftButton.setEnabled(false);
        }

        if(mPreferences.contains(AllMyConstants.CUBE_ID)) {
            cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        }


        // some click listeners
        mCreateDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getArguments().remove(AllMyConstants.CUBE_CARDS);
                Bundle bundle = new Bundle();
                bundle.putInt(AllMyConstants.CUBE_ID, cubeId);
                Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_newDraftBuilderFragment, bundle);
            }
        });

        mGoToMyCubesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag("fart").i("isSaved? %s", isSaved);
                if (!isSaved) {
                    new SaveCube(cubeId, cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener, mPreferences).execute();
                    isSaved = !isSaved;
                }

                getArguments().remove(AllMyConstants.CUBE_CARDS);
                Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_myCubesFragment);
            }
        });

        // this is the start over or the delete button. both go back to the main fragment
        mCubeMultiFunctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingle) {
                    new DeleteCube(cubeId, mAuth, currentUserId, cubeViewModel).execute();
                }

                // good bye cruel world, I quit.
                getArguments().remove(AllMyConstants.CUBE_CARDS);
                Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_hostFragment);

            }
        });

        return view;
    }

    private void getMyCube(int cubeId) {
        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();

                List<MagicCard> cards = Parcels.unwrap(handlerBundle.getParcelable(AllMyConstants.CUBE_CARDS));
                cubeAdapter.setCards(cards);
            }
        };

        t = new Thread(new GetUserCubeCardsRunnable(handler, cubeId, currentUserId));
        t.start();
    }

    private void sendDataToActivity(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentCubeReviewInteraction(bundle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCubeReviewFragmentInteractionListener) {
            mListener = (OnCubeReviewFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCubeReviewFragmentInteractionListener");
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.cube_menu, menu);

        if (!isSingle) {
            MenuItem saveButton = menu.findItem(R.id.save);
            saveButton.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                if (!isSaved) {
                    Timber.tag("fart").i("saving cube");
                    new SaveCube(cubeId, cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener, mPreferences).execute();
                    isSaved = !isSaved;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        Timber.tag("fart").i("first visible position onPause: %s", firstVisiblePosition);
        mEditor = mPreferences.edit();
        mEditor.putInt("recycler_position", firstVisiblePosition);
        mEditor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCardsRecyclerView.getLayoutManager().scrollToPosition(firstVisiblePosition);
        if(mPreferences.contains("recycler_position")) {
            firstVisiblePosition = mPreferences.getInt("recycler_position", 0);
            mCardsRecyclerView.getLayoutManager().scrollToPosition(firstVisiblePosition);
        }
        Timber.tag("fart").i("first visible position onResume: %s", firstVisiblePosition);
        firstVisiblePosition = 0;
    }


    public interface OnCubeReviewFragmentInteractionListener {
        void onFragmentCubeReviewInteraction(Bundle bundle);
    }

    public class GetUserCubeCardsRunnable implements Runnable {
        int cubeId;
        String userId;
        Handler handler;

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
                m.setData(b);;
                handler.sendMessage(m);
            }
        }
    }

    public static class SaveCube extends AsyncTask<Void, Void, Void> {
        // save the cube info in the database
        int cubeId;
        OnCubeReviewFragmentInteractionListener onCubeReviewFragmentInteractionListener;
        CubeAdapter cubeAdapter;
        FirebaseAuth mAuth;
        String currentUserId;
        CubeViewModel cubeViewModel;
        String cubeName;
        SharedPreferences mPreferences;

        public SaveCube(int cubeId, CubeAdapter cubeAdapter, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel, String cubeName, OnCubeReviewFragmentInteractionListener onCubeReviewFragmentInteractionListener, SharedPreferences mPreferences) {
            this.cubeId = cubeId;
            this.cubeAdapter = cubeAdapter;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
            this.cubeViewModel = cubeViewModel;
            this.cubeName = cubeName;
            this.onCubeReviewFragmentInteractionListener = onCubeReviewFragmentInteractionListener;
            this.mPreferences = mPreferences;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.tag("fart").i("inside doInBackground of SaveCube");
            Timber.tag("fart").i("cubeId is: %s", cubeId);

            SharedPreferences.Editor mEditor = mPreferences.edit();
            if(cubeId == 0) {
                // get all cards from adapter/recyclerView
                List<MagicCard> cards = cubeAdapter.getItems();
                List<Integer> cardIds = new ArrayList<>();

                Timber.tag("fart").i("cards from adapter size: %s", cards.size());
                // for each, create a new cube
                for (MagicCard c : cards) {
                    cardIds.add(c.getMultiverseid());
                }

                Timber.tag("fart").i("cardIds size: %s", cardIds.size());

                if (cardIds.size() == cards.size()) {
                    for (int i = 0; i < 1; i++) {
                        Timber.tag("fart").i("should be inserting cube now");
                        Cube cube = new Cube(0, currentUserId, cubeName, cards.size(), cardIds);
                        long insert = cubeViewModel.insertCubeWithReturn(cube);

                        cubeId = (int) insert;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // sends a message back to the UI thread so I can show a toast.
            super.onPostExecute(aVoid);
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.putInt(AllMyConstants.CUBE_ID, cubeId);
            mEditor.commit();

            Bundle bundle = new Bundle();
            bundle.putString(AllMyConstants.CUBE_NAME, null);
            bundle.putInt(AllMyConstants.CUBE_ID, cubeId);
            bundle.putString(AllMyConstants.TOAST_MESSAGE, "Your Cube has been saved. Why not make a draft next?");
            onCubeReviewFragmentInteractionListener.onFragmentCubeReviewInteraction(bundle);
        }
    }

    public static class DeleteCube extends AsyncTask<Void, Void, Void> {
        // save the cube info in the database

        int cubeId;
        CubeViewModel cubeViewModel;
        FirebaseAuth mAuth;
        String currentUserId;

        public DeleteCube(int cubeId, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel) {
            this.cubeId = cubeId;
            this.cubeViewModel = cubeViewModel;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // get all cards from adapter/recyclerView

            Cube userCube = cubeViewModel.getmUserCube(currentUserId, cubeId);
            cubeViewModel.deleteCube(userCube);

            return null;
        }
    }
}
