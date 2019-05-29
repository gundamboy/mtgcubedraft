package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.ragingclaw.mtgcubedraftsimulator.activities.MyCubesActivity;
import com.ragingclaw.mtgcubedraftsimulator.activities.NewDraftActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.CubeAdapter;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyCubesAdapter;
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

import static java.lang.Math.toIntExact;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCubeReview.OnCubeReviewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCubeReview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCubeReview extends Fragment {
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
    private int cubeId = -1;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();

    private FirebaseAuth mAuth;
    private String currentUserId;


    public FragmentCubeReview() {
        // Required empty public constructor
    }


    public static FragmentCubeReview newInstance() {
        FragmentCubeReview fragment = new FragmentCubeReview();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            cubeName = getArguments().getString(AllMyConstants.CUBE_NAME);

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

        // standard stuff. firebase user id, RecyclerView set up.
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Timber.tag("fart").i("current user id: %s", currentUserId);

        mCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
            cubeId = getArguments().getInt(AllMyConstants.CUBE_ID);
            mCubeMultiFunctionButton.setText(getString(R.string.delete_cube_button_text));
            getMyCube(cubeId);
        }

        // some click listeners
        mCreateDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save cube in database,
                // make a draft from the cubes cards

                if(!isSaved) {
                    new SaveCube(cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener);
                    isSaved = !isSaved;
                }

                if(isSaved) {
                    // go to create draft. send over the list of cards.
                    Intent intent = new Intent(getActivity(), NewDraftActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeAdapter.getItems()));
                    intent.putExtra(AllMyConstants.CARDS_FOR_DRAFT, bundle);
                    startActivity(intent);
                }
            }
        });

        mGoToMyCubesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag("fart").i("isSaved?");
                if (!isSaved) {
                    new SaveCube(cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener).execute();
                    isSaved = !isSaved;
                }

                getFragmentManager().popBackStack();
                Intent intent = new Intent(getActivity(), MyCubesActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mCubeMultiFunctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingle) {
                    new DeleteCube(cubeId, mAuth, currentUserId, cubeViewModel).execute();
                }

                // good bye cruel world, I quit.
                iAmAQuitter();

            }
        });

        return view;
    }


    private void iAmAQuitter() {
        getArguments().remove(AllMyConstants.CUBE_CARDS);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
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

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
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
                    new SaveCube(cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName, mListener).execute();
                    isSaved = !isSaved;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnCubeReviewFragmentInteractionListener {
        // TODO: Update argument type and name
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

        OnCubeReviewFragmentInteractionListener onCubeReviewFragmentInteractionListener;
        CubeAdapter cubeAdapter;
        FirebaseAuth mAuth;
        String currentUserId;
        CubeViewModel cubeViewModel;
        String cubeName;

        public SaveCube(CubeAdapter cubeAdapter, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel, String cubeName, OnCubeReviewFragmentInteractionListener onCubeReviewFragmentInteractionListener) {
            this.cubeAdapter = cubeAdapter;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
            this.cubeViewModel = cubeViewModel;
            this.cubeName = cubeName;
            this.onCubeReviewFragmentInteractionListener = onCubeReviewFragmentInteractionListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // get all cards from adapter/recyclerView
            List<MagicCard> cards = cubeAdapter.getItems();
            List<Integer> cardIds = new ArrayList<>();

            // for each, create a new cube
            for (MagicCard c : cards) {
                cardIds.add(c.getMultiverseid());
            }

            if(cardIds.size() == cards.size()) {
                for(int i = 0; i < 1; i++) {
                    Cube cube = new Cube(0, currentUserId, cubeName, cards.size(), cardIds);
                    cubeViewModel.insertCube(cube);
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
