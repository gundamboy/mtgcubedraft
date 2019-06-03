package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


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
    private PackViewModel packViewModel;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


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
        packViewModel = ViewModelProviders.of(getActivity()).get(PackViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        creatingText.setText(getResources().getString(R.string.creating_draft));

        if (getArguments() != null) {
            if(mListener != null) {

            }
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();
        mEditor.putInt(AllMyConstants.CURRENT_SEAT, 1);
        mEditor.putInt(AllMyConstants.CURRENT_PACK, 0);


        buildDraft(cubeId, currentUserId, cubeViewModel, magicCardViewModel,
                packViewModel, view, mPreferences, mEditor);

        return view;
    }

    private void buildDraft(int cubeId, String userId, CubeViewModel cubeViewModel, MagicCardViewModel magicCardViewModel, PackViewModel packViewModel, View view, SharedPreferences mPreferences, SharedPreferences.Editor mEditor) {

        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                String percent = handlerBundle.getString("percent") + "%";
                completePercent.setText(percent);
            }
        };

        t = new Thread(new BuildDraft(handler, cubeId, userId, cubeViewModel,
                magicCardViewModel, packViewModel, view, mPreferences, mEditor));
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
        PackViewModel packViewModel;
        int packsPerPlayer = 3;
        View view;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        public BuildDraft(Handler handler, int cubeId, String userId,
                          CubeViewModel cubeViewModel,
                          MagicCardViewModel magicCardViewModel, PackViewModel packViewModel, View view, SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
            this.handler = handler;
            this.cubeId = cubeId;
            this.userId = userId;
            this.cubeViewModel = cubeViewModel;
            this.magicCardViewModel = magicCardViewModel;
            this.packViewModel = packViewModel;
            this.view = view;
            this.sharedPreferences = sharedPreferences;
            this.editor = editor;
        }

        @Override
        public void run() {
            packViewModel.deleteAllPacks();

            // lists to hold each players 3 booster packs
            ArrayList<ArrayList<Integer>> player1Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player2Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player3Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player4Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player5Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player6Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player7Packs = new ArrayList<>(packsPerPlayer);
            ArrayList<ArrayList<Integer>> player8Packs = new ArrayList<>(packsPerPlayer);

            Cube userCube  = cubeViewModel.getmUserCube(userId, cubeId);

            // get the 360 card ids from the cube
            List<Integer> cardsFromTheCube = userCube.getCard_ids();
            List<Integer> cardIdPool = cardsFromTheCube;

            // adds in 3 lists to each of the player pack master ArrayList objects.
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


            Set<String> seat1_pack1 = new HashSet<>();
            Set<String> seat1_pack2 = new HashSet<>();
            Set<String> seat1_pack3 = new HashSet<>();
            Set<String> seat2_pack1 = new HashSet<>();
            Set<String> seat2_pack2 = new HashSet<>();
            Set<String> seat2_pack3 = new HashSet<>();
            Set<String> seat3_pack1 = new HashSet<>();
            Set<String> seat3_pack2 = new HashSet<>();
            Set<String> seat3_pack3 = new HashSet<>();
            Set<String> seat4_pack1 = new HashSet<>();
            Set<String> seat4_pack2 = new HashSet<>();
            Set<String> seat4_pack3 = new HashSet<>();
            Set<String> seat5_pack1 = new HashSet<>();
            Set<String> seat5_pack2 = new HashSet<>();
            Set<String> seat5_pack3 = new HashSet<>();
            Set<String> seat6_pack1 = new HashSet<>();
            Set<String> seat6_pack2 = new HashSet<>();
            Set<String> seat6_pack3 = new HashSet<>();
            Set<String> seat7_pack1 = new HashSet<>();
            Set<String> seat7_pack2 = new HashSet<>();
            Set<String> seat7_pack3 = new HashSet<>();
            Set<String> seat8_pack1 = new HashSet<>();
            Set<String> seat8_pack2 = new HashSet<>();
            Set<String> seat8_pack3 = new HashSet<>();


            // generate the packs
            int maxCardsPerPlayer = 45;


            // for each pack
            for (int pack = 0; pack < 3; pack++) {

                // for each card in the pack
                for (int cards = 15; cards > 0; cards--) {

                    // for each player/seat at the table
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

                        // shuffles the id list to help make things more random.
                        Collections.shuffle(cardIdPool);
                    }
                }
            }

            // for each pack in the players packs array
            for(int p = 0; p < 3; p++) {
                packViewModel.insertPack(new Pack(0, p, 1, cubeId, player1Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 2, cubeId, player2Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 3, cubeId, player3Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 4, cubeId, player4Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 5, cubeId, player5Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 6, cubeId, player6Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 7, cubeId, player7Packs.get(p)));
                packViewModel.insertPack(new Pack(0, p, 8, cubeId, player8Packs.get(p)));
            }

            seat1_pack1 = new HashSet<String>(Lists.transform(player1Packs.get(0), Functions.toStringFunction()));
            seat1_pack2 = new HashSet<String>(Lists.transform(player1Packs.get(1), Functions.toStringFunction()));
            seat1_pack3 = new HashSet<String>(Lists.transform(player1Packs.get(2), Functions.toStringFunction()));
            seat2_pack1 = new HashSet<String>(Lists.transform(player2Packs.get(0), Functions.toStringFunction()));
            seat2_pack2 = new HashSet<String>(Lists.transform(player2Packs.get(1), Functions.toStringFunction()));
            seat2_pack3 = new HashSet<String>(Lists.transform(player2Packs.get(2), Functions.toStringFunction()));
            seat3_pack1 = new HashSet<String>(Lists.transform(player3Packs.get(0), Functions.toStringFunction()));
            seat3_pack2 = new HashSet<String>(Lists.transform(player3Packs.get(1), Functions.toStringFunction()));
            seat3_pack3 = new HashSet<String>(Lists.transform(player3Packs.get(2), Functions.toStringFunction()));
            seat4_pack1 = new HashSet<String>(Lists.transform(player4Packs.get(0), Functions.toStringFunction()));
            seat4_pack2 = new HashSet<String>(Lists.transform(player4Packs.get(1), Functions.toStringFunction()));
            seat4_pack3 = new HashSet<String>(Lists.transform(player4Packs.get(2), Functions.toStringFunction()));
            seat5_pack1 = new HashSet<String>(Lists.transform(player5Packs.get(0), Functions.toStringFunction()));
            seat5_pack2 = new HashSet<String>(Lists.transform(player5Packs.get(1), Functions.toStringFunction()));
            seat5_pack3 = new HashSet<String>(Lists.transform(player5Packs.get(2), Functions.toStringFunction()));
            seat6_pack1 = new HashSet<String>(Lists.transform(player6Packs.get(0), Functions.toStringFunction()));
            seat6_pack2 = new HashSet<String>(Lists.transform(player6Packs.get(1), Functions.toStringFunction()));
            seat6_pack3 = new HashSet<String>(Lists.transform(player6Packs.get(2), Functions.toStringFunction()));
            seat7_pack1 = new HashSet<String>(Lists.transform(player7Packs.get(0), Functions.toStringFunction()));
            seat7_pack2 = new HashSet<String>(Lists.transform(player7Packs.get(1), Functions.toStringFunction()));
            seat7_pack3 = new HashSet<String>(Lists.transform(player7Packs.get(2), Functions.toStringFunction()));
            seat8_pack1 = new HashSet<String>(Lists.transform(player8Packs.get(0), Functions.toStringFunction()));
            seat8_pack2 = new HashSet<String>(Lists.transform(player8Packs.get(1), Functions.toStringFunction()));
            seat8_pack3 = new HashSet<String>(Lists.transform(player8Packs.get(2), Functions.toStringFunction()));


            // put all this crap into shared prefs
            mEditor = mPreferences.edit();
            mEditor.putInt(AllMyConstants.CURRENT_PACK, 0);
            mEditor.putInt(AllMyConstants.CURRENT_SEAT, 1);
            mEditor.putInt(AllMyConstants.CURRENT_PICK, 1);
            mEditor.putInt(AllMyConstants.CUBE_ID, cubeId);
            mEditor.putStringSet(AllMyConstants.SEAT1_PACK1, seat1_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT1_PACK2, seat1_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT1_PACK3, seat1_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT2_PACK1, seat2_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT2_PACK2, seat2_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT2_PACK3, seat2_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT3_PACK1, seat3_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT3_PACK2, seat3_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT3_PACK3, seat3_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT4_PACK1, seat4_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT4_PACK2, seat4_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT4_PACK3, seat4_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT5_PACK1, seat5_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT5_PACK2, seat5_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT5_PACK3, seat5_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT6_PACK1, seat6_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT6_PACK2, seat6_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT6_PACK3, seat6_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT7_PACK1, seat7_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT7_PACK2, seat7_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT7_PACK3, seat7_pack3);
            mEditor.putStringSet(AllMyConstants.SEAT8_PACK1, seat8_pack1);
            mEditor.putStringSet(AllMyConstants.SEAT8_PACK2, seat8_pack2);
            mEditor.putStringSet(AllMyConstants.SEAT8_PACK3, seat8_pack3);



            mEditor.putBoolean(AllMyConstants.START_DRAFT, true);

            mEditor.commit();



            int timeout = 2000;
            for(int t = 0; t < timeout; t++) {
                // uhg, math.
                String percent = String.valueOf((t * 100) / timeout);

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString("percent", percent);
                m.setData(b);
                handler.sendMessage(m);
            }

            List<Pack> packs = packViewModel.getAllPacksStatic();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AllMyConstants.PACKS, Parcels.wrap(packs));
            Navigation.findNavController(view).navigate(R.id.action_newDraftBuilderFragment_to_draftingHappyFunTimeFragment);
        }

        private int getRandomFromList(List<Integer> idPool) {
            Random r = new Random();
            return idPool.get(r.nextInt(idPool.size()));
        }
    }
}
