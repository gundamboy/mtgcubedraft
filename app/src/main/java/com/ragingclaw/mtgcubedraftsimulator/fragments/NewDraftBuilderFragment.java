package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.DraftViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class NewDraftBuilderFragment extends Fragment {
    @BindView(R.id.creating_cube_static_text) TextView creatingText;
    @BindView(R.id.percentage_built) TextView completePercent;
    private Unbinder unbinder;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();
    private List<MagicCard> cubeCards;
    private int cubeId = -1;
    private String draftName;
    private OnBuildDraftFragmentInteractionListener mListener;
    private CubeViewModel cubeViewModel;
    private MagicCardViewModel magicCardViewModel;
    private DraftViewModel draftViewModel;
    private PackViewModel packViewModel;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public NewDraftBuilderFragment() {
        // Required empty public constructor
    }

    public static NewDraftBuilderFragment newInstance() {
        NewDraftBuilderFragment fragment = new NewDraftBuilderFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cubeId = getArguments().getInt(AllMyConstants.CUBE_ID);
            draftName = getArguments().getString(AllMyConstants.DRAFT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creating_cube_or_draft_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        cubeViewModel = ViewModelProviders.of(getActivity()).get(CubeViewModel.class);
        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        draftViewModel = ViewModelProviders.of(getActivity()).get(DraftViewModel.class);
        packViewModel = ViewModelProviders.of(getActivity()).get(PackViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        creatingText.setText(getResources().getString(R.string.creating_draft));

        if (getArguments() != null) {
            if(mListener != null) {

            }
        }

        buildDraft(cubeId, currentUserId, cubeViewModel, magicCardViewModel, draftViewModel, packViewModel);

        return view;
    }

    private void buildDraft(int cubeId, String userId, CubeViewModel cubeViewModel, MagicCardViewModel magicCardViewModel, DraftViewModel draftViewModel, PackViewModel packViewModel) {

        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                String percent = handlerBundle.getString("percent") + "%";
                completePercent.setText(percent);
            }
        };

        t = new Thread(new BuildDraft(handler, cubeId, userId, cubeViewModel, magicCardViewModel, draftViewModel, packViewModel));
        t.start();
    }


    public void sendDataToActivity(Uri uri) {
        if (mListener != null) {
            mListener.OnBuildDraftFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBuildDraftFragmentInteractionListener) {
            mListener = (OnBuildDraftFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBuildDraftFragmentInteractionListener");
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

    public interface OnBuildDraftFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnBuildDraftFragmentInteractionListener(Uri uri);
    }

    public class BuildDraft implements Runnable {
        Handler handler;
        int cubeId;
        String userId;
        CubeViewModel cubeViewModel;
        MagicCardViewModel magicCardViewModel;
        DraftViewModel draftViewModel;
        PackViewModel packViewModel;
        int totalPlayers = 8;
        int packsPerPlayer = 3;
        int packSize = 15;

        public BuildDraft(Handler handler, int cubeId, String userId, CubeViewModel cubeViewModel, MagicCardViewModel magicCardViewModel, DraftViewModel draftViewModel, PackViewModel packViewModel) {
            this.handler = handler;
            this.cubeId = cubeId;
            this.userId = userId;
            this.cubeViewModel = cubeViewModel;
            this.magicCardViewModel = magicCardViewModel;
            this.draftViewModel = draftViewModel;
            this.packViewModel = packViewModel;
        }

        @Override
        public void run() {
            ArrayList<ArrayList<Integer>> player1Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player2Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player3Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player4Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player5Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player6Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player7Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player8Packs = new ArrayList<>(packsPerPlayer);

            ArrayList<Integer> player1Picks = new ArrayList<>();
            ArrayList<Integer> player2Picks = new ArrayList<>();
            ArrayList<Integer> player3Picks = new ArrayList<>();
            ArrayList<Integer> player4Picks = new ArrayList<>();
            ArrayList<Integer> player5Picks = new ArrayList<>();
            ArrayList<Integer> player6Picks = new ArrayList<>();
            ArrayList<Integer> player7Picks = new ArrayList<>();
            ArrayList<Integer> player8Picks = new ArrayList<>();


            Cube userCube  = cubeViewModel.getmUserCube(userId, cubeId);

            // get the 360 card ids from the cube
            List<Integer> cardsFromTheCube = userCube.getCard_ids();
            List<Integer> cardIdPool = cardsFromTheCube;

            for(int i=0; i < packsPerPlayer; i++) {
                player1Packs.add(new ArrayList());
                player2Packs.add(new ArrayList());
                player3Packs.add(new ArrayList());
                player4Packs.add(new ArrayList());
                player5Packs.add(new ArrayList());
                player6Packs.add(new ArrayList());
                player7Packs.add(new ArrayList());
                player8Packs.add(new ArrayList());
            }


            // generate the packs
            for (int pack = 0; pack < 3; pack++) {
                for (int cards = 15; cards > 0; cards--) {
                    for(int player = 0; player < 8; player++) {

                        int cardId = getRandomFromList(cardIdPool);
                        int index = cardIdPool.indexOf(cardId);

                        if (player == 0) { player1Packs.get(pack).add(cardId); }
                        if (player == 1) { player2Packs.get(pack).add(cardId); }
                        if (player == 2) { player3Packs.get(pack).add(cardId); }
                        if (player == 3) { player4Packs.get(pack).add(cardId); }
                        if (player == 4) { player5Packs.get(pack).add(cardId); }
                        if (player == 5) { player6Packs.get(pack).add(cardId); }
                        if (player == 6) { player7Packs.get(pack).add(cardId); }
                        if (player == 7) { player8Packs.get(pack).add(cardId); }

                        cardIdPool.remove(index);
                    }
                }
            }


            // packs are created. insert stuff into the DB
        }

        private int getRandomFromList(List<Integer> idPool) {
            Random r = new Random();
            return idPool.get(r.nextInt(idPool.size()));
        }

        private List<Integer> buildBoosterPackIds(List<Integer> draftCardPool) {
            List<Integer> boosterIds = new ArrayList<>();

            return boosterIds;
        }
    }
}
