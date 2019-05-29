package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

public class NewCubeStepTwoFragment extends Fragment {
    @BindView(R.id.percentage_built) TextView completePercent;
    private Unbinder unbinder;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();

    private MagicCardViewModel magicCardViewModel;

    private OnFragmentInteractionListenerStepTwo mListener;
    private List<String> mSetCodes = new ArrayList<>();
    private List<Card> mCubeCards = new ArrayList<>();
    private String cubeName;

    public NewCubeStepTwoFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepTwoFragment newInstance(String param1, String param2) {
        NewCubeStepTwoFragment fragment = new NewCubeStepTwoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_cube_step_two, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            if(mListener != null) {
                cubeName = getArguments().getString(AllMyConstants.CUBE_NAME);
                mListener.onFragmentInteractionStepTwo(cubeName);
            }
        }

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        magicCardViewModel.getmAllCards().observe(this, new Observer<List<MagicCard>>() {
            // make a list of the ids to send off for draft building
            List<Integer> ids = new ArrayList<>();
            List<MagicCard> cards = new ArrayList<>();

            @Override
            public void onChanged(List<MagicCard> magicCards) {
                try {
                    for (MagicCard c : magicCards) {
                        ids.add(c.getMultiverseid());
                        cards.add(c);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(ids.size() > 0) {
                    // there are ids, WOOHOO! lets build a cube!
                    getCardsFromIds(ids);
                    buildCube(cards, view);
                }
            }
        });
        return view;
    }

    private void buildCube(List<MagicCard> cards, View view) {

        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                String percent = handlerBundle.getString("percent") + "%";
                completePercent.setText(percent);
            }
        };

        t = new Thread(new BuildCube(handler, cards, view));
        t.start();

    }

    private void getCubeCards(List<MagicCard> cards, View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MagicCard> allCards = cards;
                List<MagicCard> cubeCards = new ArrayList<>();

                // a cube is 360 cards.
                int cubeSize = 360;

                for (int i = 0; i < cubeSize; i++) {
                    int r = getRandomNum(allCards.size());
                    cubeCards.add(allCards.get(r));
                    allCards.remove(r);

                    // Percentage = (Obtained score x 100) / Total Score
                    int percent = (i * 100) / cubeSize;
                }

                Timber.tag("fart").i("cards size: %s", allCards.size());

                if (cubeCards.size() == cubeSize) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AllMyConstants.NEW_CUBE, true);
                    bundle.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
                    bundle.putString(AllMyConstants.CUBE_NAME, cubeName);

                    NavOptions.Builder navBuilder = new NavOptions.Builder();
                    NavOptions navOptions = navBuilder.setPopUpTo(R.id.newCubeStepOneFragment, true).build();
                    Navigation.findNavController(view).navigate(R.id.action_newCubeStepTwoFragment_to_fragmentCubeReview, bundle, navOptions);
                }
            }
        }).start();
    }

    private void getCardsFromIds(List<Integer> ids) {
        // copy the list to we can change it.
        List<Integer> multiversePool = ids;

        // a list to hold only the cards picked for the 3 boosters
        List<Integer> draftCardIds = new ArrayList<>();

        // a cube is 360 cards.
        int cubeSize = 360;

        // get 360 random ids (cards) from the total card id list
        // cubes cannot have duplicate cards
        for (int i = 0; i < cubeSize; i++) {
            int r = getRandomNum(multiversePool.size());
            draftCardIds.add(multiversePool.get(r));
            multiversePool.remove(r);
        }


        // build the 3 packs that will be passed around for the draft
        if(draftCardIds.size() == cubeSize) {
            buildBoosterPacks(draftCardIds);
        }

    }

    private void buildBoosterPacks(List<Integer> draftCardIds) {
        // copy the id list so we can make changes
        // 3 list copies. boosters dont have copies, but we have 3 booster, so there can be
        // the same cards in different boosters.
        List<Integer> idPool = draftCardIds;
        List<Integer> idPool2 = draftCardIds;
        List<Integer> idPool3 = draftCardIds;

        // 3 id lists. 1 for each booster pack
        List<Integer> boosterIdsPack1 = new ArrayList<>();
        List<Integer> boosterIdsPack2 = new ArrayList<>();
        List<Integer> boosterIdsPack3 = new ArrayList<>();

        // there are 3 packs
        int packCount = 3;

        // a pack (booster) is 15 cards
        int boosterPackSize = 15;

        // build the booster ids
        for(int p = 0; p < packCount; p++) {

            if(p == 0) {
                for(int i = 0; i < boosterPackSize; i++) {
                    int id = getRandomFromList(idPool, idPool.size());
                    int index = idPool.indexOf(id);
                    boosterIdsPack1.add(id);
                    idPool.remove(index);
                }
            }

            if(p == 1) {
                for(int i = 0; i < boosterPackSize; i++) {
                    int id = getRandomFromList(idPool2, idPool2.size());
                    int index = idPool2.indexOf(id);
                    boosterIdsPack2.add(id);
                    idPool2.remove(index);
                }
            }

            if(p == 2) {
                for(int i = 0; i < boosterPackSize; i++) {
                    int id = getRandomFromList(idPool3, idPool3.size());
                    int index = idPool3.indexOf(id);
                    boosterIdsPack3.add(id);
                    idPool3.remove(index);
                }
            }
        }

        // we can build the card objects now. The sdk requires this to be on a separate thread,
        // which is why I am doing id lists THEN card objects, and not at the same time.

        //Runnable r = new CardRunnable(boosterIdsPack1, boosterIdsPack2, boosterIdsPack3);
        Runnable r = new CardRunnable(boosterIdsPack1, boosterIdsPack2, boosterIdsPack3);
        new Thread(r).start();
    }

    private int getRandomFromList(List<Integer> idPool, int max) {
        Random r = new Random();
        return idPool.get(r.nextInt(max));
    }

    private int getRandomNum(int max) {
        Random r = new Random();
        return r.nextInt((max - 1) + 1);
    }

    public void sendDataToActivity(String string) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepTwo(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerStepTwo) {
            mListener = (OnFragmentInteractionListenerStepTwo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerStepTwo");
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

    public interface OnFragmentInteractionListenerStepTwo {

        void onFragmentInteractionStepTwo(String string);
    }

    public class CardRunnable implements Runnable {
        List<Integer> b1;
        List<Integer> b2;
        List<Integer> b3;

        int packCount = 3;

        public CardRunnable(List<Integer> b1, List<Integer> b2, List<Integer> b3) {
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
        }

        @Override
        public void run() {
            List<MagicCard> pack1 = new ArrayList<>();
            List<MagicCard> pack2 = new ArrayList<>();
            List<MagicCard> pack3 = new ArrayList<>();

            for(int i = 0; i < packCount; i++) {
                if (i == 0) {
                    for (int id : b1) {
                        MagicCard card = magicCardViewModel.getmCard(id);
                        pack1.add(card);
                    }
                }

                if (i == 1) {
                    for (int id : b2) {
                        MagicCard card = magicCardViewModel.getmCard(id);
                        pack2.add(card);
                    }
                }

                if(i == 2) {
                    for (int id : b3) {
                        MagicCard card = magicCardViewModel.getmCard(id);
                        pack3.add(card);
                    }
                }
            }

            Timber.tag("fart").i("pack 1 size: %s", pack1.size());

        }
    }

    public class BuildCube implements Runnable {
        Handler handler;
        List<MagicCard> cards;
        View view;

        public BuildCube(Handler handler, List<MagicCard> cards, View view) {
            this.handler = handler;
            this.cards = cards;
            this.view = view;
        }

        @Override
        public void run() {
            List<MagicCard> allCards = cards;
            List<MagicCard> cubeCards = new ArrayList<>();

            // a cube is 360 cards.
            int cubeSize = 360;

            for (int i = 0; i < cubeSize; i++) {
                int r = getRandomNum(allCards.size());
                cubeCards.add(allCards.get(r));
                allCards.remove(r);

                // uhg, math.
                String percent = String.valueOf((i * 100) / cubeSize);

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString("percent", percent);
                m.setData(b);
                handler.sendMessage(m);

            }

            if (cubeCards.size() == cubeSize) {
                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString("percent", "100");
                m.setData(b);
                handler.sendMessage(m);

                Bundle bundle = new Bundle();
                bundle.putBoolean(AllMyConstants.NEW_CUBE, true);
                bundle.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
                bundle.putString(AllMyConstants.CUBE_NAME, cubeName);

                NavOptions.Builder navBuilder = new NavOptions.Builder();
                NavOptions navOptions = navBuilder.setPopUpTo(R.id.newCubeStepOneFragment, true).build();
                Navigation.findNavController(view).navigate(R.id.action_newCubeStepTwoFragment_to_fragmentCubeReview, bundle, navOptions);
            }

        }
    }
}
