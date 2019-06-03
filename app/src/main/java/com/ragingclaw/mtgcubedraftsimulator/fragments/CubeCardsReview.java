package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
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
    private MagicCardViewModel magicCardViewModel;
    private OnCubeReviewFragmentInteractionListener mListener;
    private List<MagicCard> cubeCards;
    private CubeViewModel cubeViewModel;
    private boolean isSaved = false;
    private boolean isSingle = false;
    private CubeAdapter cubeAdapter;
    private String cubeName = "";
    private int cubeId = 0;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();
    private LinearLayoutManager linearLayoutManager;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;


    public CubeCardsReview() {
        // Required empty public constructor
    }


    public static CubeCardsReview newInstance() {
        CubeCardsReview fragment = new CubeCardsReview();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

        if (savedInstanceState != null) {
            cubeName = savedInstanceState.getString(AllMyConstants.CUBE_NAME);
            cubeId = savedInstanceState.getInt(AllMyConstants.CUBE_ID);
            cubeCards = Parcels.unwrap(savedInstanceState.getParcelable(AllMyConstants.CUBE_CARDS));
            isSingle = savedInstanceState.getBoolean(AllMyConstants.IS_SINGLE);
            isSaved = savedInstanceState.getBoolean(AllMyConstants.IS_SAVED);
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

        // some click listeners
        mCreateDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save cube in database,
                if(!isSaved) {
                    new SaveCube(cubeId, cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener);
                    isSaved = !isSaved;
                }

                if(isSaved) {
                    // go to create draft. send over the list of cards.
                    getArguments().remove(AllMyConstants.CUBE_CARDS);
                    Bundle bundle = new Bundle();
                    bundle.putInt(AllMyConstants.CUBE_ID, cubeId);
                    Navigation.findNavController(view).navigate(R.id.action_cubeCardsReview_to_newDraftBuilderFragment, bundle);
                }
            }
        });

        mGoToMyCubesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save the cube
                if (!isSaved) {
                    new SaveCube(cubeId, cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener).execute();
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
                // if the user came from the list of cubes, offer a delete option
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
        // sends the listener info back to the activity. I know the method name was confusing...
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
    public void onPause() {
        super.onPause();

        // store the recyclerViews scroll position
        mBundleRecyclerViewState = new Bundle();
        mListState = mCardsRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // restore the scroll position of the recyclerView
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(AllMyConstants.RECYCLER_RESTORE);
                    mCardsRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }


        mCardsRecyclerView.setLayoutManager(linearLayoutManager);
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
                    new SaveCube(cubeId, cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener).execute();
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
        outState.putString(AllMyConstants.CUBE_NAME, cubeName);
        outState.putInt(AllMyConstants.CUBE_NAME, cubeId);
        outState.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
        outState.putBoolean(AllMyConstants.IS_SINGLE, isSingle);
        outState.putBoolean(AllMyConstants.IS_SAVED, isSaved);

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

        public SaveCube(int cubeId, CubeAdapter cubeAdapter, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel, String cubeName, OnCubeReviewFragmentInteractionListener onCubeReviewFragmentInteractionListener) {
            this.cubeId = cubeId;
            this.cubeAdapter = cubeAdapter;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
            this.cubeViewModel = cubeViewModel;
            this.cubeName = cubeName;
            this.onCubeReviewFragmentInteractionListener = onCubeReviewFragmentInteractionListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.tag("fart").i("inside doInBackground of SaveCube");
            Timber.tag("fart").i("cubeId is: %s", cubeId);
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
                        cubeViewModel.insertCube(cube);
                        Timber.tag("fart").i("cube insert should be done now");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // sends a message back to the UI thread so I can show a toast.
            super.onPostExecute(aVoid);
            Bundle bundle = new Bundle();
            bundle.putString(AllMyConstants.CUBE_NAME, null);
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
