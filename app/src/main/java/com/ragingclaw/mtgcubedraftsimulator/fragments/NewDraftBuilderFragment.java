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
            
            Timber.tag("fart").i("cubeId: %s, draftName: %s", cubeId, draftName);
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
            ArrayList<ArrayList<Integer>> player1 = new ArrayList<>(packsPerPlayer);
            List<Integer> player1pack1ids = new ArrayList<>();
            List<Integer> player1pack2ids = new ArrayList<>();
            List<Integer> player1pack3ids = new ArrayList<>();
            List<Integer> player2pack1ids = new ArrayList<>();
            List<Integer> player2pack2ids = new ArrayList<>();
            List<Integer> player2pack3ids = new ArrayList<>();
            List<Integer> player3pack1ids = new ArrayList<>();
            List<Integer> player3pack2ids = new ArrayList<>();
            List<Integer> player3pack3ids = new ArrayList<>();
            List<Integer> player4pack1ids = new ArrayList<>();
            List<Integer> player4pack2ids = new ArrayList<>();
            List<Integer> player4pack3ids = new ArrayList<>();
            List<Integer> player5pack1ids = new ArrayList<>();
            List<Integer> player5pack2ids = new ArrayList<>();
            List<Integer> player5pack3ids = new ArrayList<>();
            List<Integer> player6pack1ids = new ArrayList<>();
            List<Integer> player6pack2ids = new ArrayList<>();
            List<Integer> player6pack3ids = new ArrayList<>();
            List<Integer> player7pack1ids = new ArrayList<>();
            List<Integer> player7pack2ids = new ArrayList<>();
            List<Integer> player7pack3ids = new ArrayList<>();
            List<Integer> player8pack1ids = new ArrayList<>();
            List<Integer> player8pack2ids = new ArrayList<>();
            List<Integer> player8pack3ids = new ArrayList<>();

            List<MagicCard> player1pack1cards = new ArrayList<>();
            List<MagicCard> player1pack2cards = new ArrayList<>();
            List<MagicCard> player1pack3cards = new ArrayList<>();
            List<MagicCard> player2pack1cards = new ArrayList<>();
            List<MagicCard> player2pack2cards = new ArrayList<>();
            List<MagicCard> player2pack3cards = new ArrayList<>();
            List<MagicCard> player3pack1cards = new ArrayList<>();
            List<MagicCard> player3pack2cards = new ArrayList<>();
            List<MagicCard> player3pack3cards = new ArrayList<>();
            List<MagicCard> player4pack1cards = new ArrayList<>();
            List<MagicCard> player4pack2cards = new ArrayList<>();
            List<MagicCard> player4pack3cards = new ArrayList<>();
            List<MagicCard> player5pack1cards = new ArrayList<>();
            List<MagicCard> player5pack2cards = new ArrayList<>();
            List<MagicCard> player5pack3cards = new ArrayList<>();
            List<MagicCard> player6pack1cards = new ArrayList<>();
            List<MagicCard> player6pack2cards = new ArrayList<>();
            List<MagicCard> player6pack3cards = new ArrayList<>();
            List<MagicCard> player7pack1cards = new ArrayList<>();
            List<MagicCard> player7pack2cards = new ArrayList<>();
            List<MagicCard> player7pack3cards = new ArrayList<>();
            List<MagicCard> player8pack1cards = new ArrayList<>();
            List<MagicCard> player8pack2cards = new ArrayList<>();
            List<MagicCard> player8pack3cards = new ArrayList<>();

            Cube userCube  = cubeViewModel.getmUserCube(userId, cubeId);

            // get the 360 card ids from the cube
            List<Integer> cardIdPool = userCube.getCard_ids();

            for(int i=0; i < packsPerPlayer; i++) {
                player1.add(new ArrayList());
            }

            for (int p = 0; p > totalPlayers; p++) {
                // player 1. this is the user.
                if(p == 0) {
                    for( int i = 0; i > packSize; i++ ) {
                        // get a random id from the card id pool
                        int cardId = getRandomFromList(cardIdPool);;
                        if(i == 0) {
                            //player1pack1ids.add(cardId);
                            player1.get(0).add(cardId);
                        }
                        if(i == 1) { player1pack2ids.add(cardId); }
                        if(i == 2) { player1pack3ids.add(cardId); }

                        cardIdPool.remove(cardId);
                    }
                }

                if(p == 1) {}
                if(p == 2) {}
                if(p == 3) {}
                if(p == 4) {}
                if(p == 5) {}
                if(p == 6) {}
                if(p == 7) {}
            }

            Timber.tag("fart").i("Player 1 pack 1 size: %s", player1.get(0).size());
            Timber.tag("fart").i("Player 1 pack 2 size: %s", player1.get(1).size());
            Timber.tag("fart").i("Player 1 pack 3 size: %s", player1.get(3).size());
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
