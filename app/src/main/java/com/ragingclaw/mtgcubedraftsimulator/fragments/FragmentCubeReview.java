package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.MainActivity;
import com.ragingclaw.mtgcubedraftsimulator.activities.NewDraftActivity;
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
 * {@link FragmentCubeReview.OnCubeReviewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCubeReview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCubeReview extends Fragment {
    @BindView(R.id.cube_cards_recyclerview) RecyclerView mCardsRecyclerView;
    @BindView(R.id.create_draft_button) com.google.android.material.button.MaterialButton mCreateDraftButton;
    @BindView(R.id.start_over_button) com.google.android.material.button.MaterialButton mStartOVerButton;
    private Unbinder unbinder;
    private MagicCardViewModel magicCardViewModel;
    private OnCubeReviewFragmentInteractionListener mListener;
    private List<MagicCard> cubeCards;
    private CubeViewModel cubeViewModel;
    private boolean isSaved = false;
    private CubeAdapter cubeAdapter;
    private String cubeName = "";

    private FirebaseAuth mAuth;
    private String currentUserId;

    private Handler handler;

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
            cubeCards = Parcels.unwrap(getArguments().getParcelable(AllMyConstants.CUBE_CARDS));

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
        getFragmentManager().popBackStack();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCardsRecyclerView.setHasFixedSize(true);

        cubeAdapter = new CubeAdapter();
        mCardsRecyclerView.setAdapter(cubeAdapter);
        cubeAdapter.setCards(cubeCards);

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        cubeViewModel = ViewModelProviders.of(getActivity()).get(CubeViewModel.class);

        if (savedInstanceState != null) {

        } else {

        }

        mCreateDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save cube in database,
                // make a draft from the cubes cards

                if(!isSaved) {
                    new SaveCube(cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName);
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

        mStartOVerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    public void sendDataToActivity(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cube_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                return true;
            case R.id.save:
                if (!isSaved) {
                    new SaveCube(cubeAdapter, mAuth, currentUserId, cubeViewModel, cubeName).execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnCubeReviewFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle);
    }

    public static class SaveCube extends AsyncTask<Void, Void, Void> {
        CubeAdapter cubeAdapter;
        FirebaseAuth mAuth;
        String currentUserId;
        CubeViewModel cubeViewModel;
        String cubeName;

        public SaveCube(CubeAdapter cubeAdapter, FirebaseAuth mAuth, String currentUserId, CubeViewModel cubeViewModel, String cubeName) {
            this.cubeAdapter = cubeAdapter;
            this.mAuth = mAuth;
            this.currentUserId = currentUserId;
            this.cubeViewModel = cubeViewModel;
            this.cubeName = cubeName;
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
                Cube cube = new Cube(0, mAuth.getCurrentUser().getUid(), cubeName, cards.size(), cardIds);
                cubeViewModel.insertCube(cube);

                LiveData<Cube> verifyCube = cubeViewModel.getmUserCube(currentUserId.toLowerCase(), cube.getCubeId());
                if (verifyCube != null) {
                    // cube was inserted. show saved toast, disable save icon
                    Bundle bundle = new Bundle();
                    bundle.putString(AllMyConstants.CUBE_NAME, null);
                    bundle.putString(AllMyConstants.TOAST_MESSAGE, "Your Cube has been saved. Why not make a draft next?");

                    Timber.tag("fart").i("cube should be good in the database");
                } else {
                    Timber.tag("fart").i("cube didn't go i guess...");
                }
            }
            return null;
        }
    }
}
