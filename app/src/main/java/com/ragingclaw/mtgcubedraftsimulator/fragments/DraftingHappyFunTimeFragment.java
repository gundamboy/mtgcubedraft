package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.DraftCardsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DraftingHappyFunTimeFragment.OnDraftingHappyFunTimeInteraction} interface
 * to handle interaction events.
 * Use the {@link DraftingHappyFunTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DraftingHappyFunTimeFragment extends Fragment {
    @BindView(R.id.draft_cards_recyclerview) RecyclerView draftCardsRecyclerView;

    private Unbinder unbinder;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();
    private MagicCardViewModel magicCardViewModel;
    private PackViewModel packViewModel;
    private GridLayoutManager gridLayoutManager;
    private DraftCardsAdapter draftCardsAdapter;
    private OnDraftingHappyFunTimeInteraction mListener;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private int currentPackNum = 0;
    private int currentPickNum = 1;
    private int currentSeatNum = 1;

    private List<Pack> packs;
    private List<MagicCard> currentCards = new ArrayList<>();

    public DraftingHappyFunTimeFragment() {
        // Required empty public constructor
    }

    public static DraftingHappyFunTimeFragment newInstance() {
        DraftingHappyFunTimeFragment fragment = new DraftingHappyFunTimeFragment();

        return fragment;
    }

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if(key.equals(AllMyConstants.CARD_ID) && sharedPreferences.getBoolean(AllMyConstants.UPDATE_DRAFT, false)) {

                updateBoard();
            }

            if(key.equals(AllMyConstants.CURRENT_SEAT)) { currentSeatNum = sharedPreferences.getInt(AllMyConstants.CURRENT_SEAT, 0); }
            if(key.equals(AllMyConstants.PACKS_NUMBER)) { currentPackNum = sharedPreferences.getInt(AllMyConstants.PACKS_NUMBER, 0); }
            if(key.equals(AllMyConstants.CURRENT_PICK)) { currentPickNum = sharedPreferences.getInt(AllMyConstants.CURRENT_PICK, 1); }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packs = Parcels.unwrap(getArguments().getParcelable(AllMyConstants.PACKS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drafting_happy_fun_time, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
        draftCardsRecyclerView.setHasFixedSize(true);
        draftCardsAdapter = new DraftCardsAdapter();
        draftCardsRecyclerView.setAdapter(draftCardsAdapter);

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        packViewModel = ViewModelProviders.of(getActivity()).get(PackViewModel.class);

        Timber.tag("fart").i("current seat: %s, current pack: %s", currentSeatNum, currentPackNum);

        packViewModel.getAllPacks().observe(this, new Observer<List<Pack>>() {
            @Override
            public void onChanged(List<Pack> packs) {
                Timber.tag("fart").i("current seat: %s, current pack: %s", currentSeatNum, currentPackNum);
                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();

                    if (p.getSeat_num() == currentSeatNum && p.getBooster_num() == currentPackNum) {
                        magicCardViewModel.getmAllCards().observe(getActivity(), new Observer<List<MagicCard>>() {
                            @Override
                            public void onChanged(List<MagicCard> magicCards) {
                                for (MagicCard card : magicCards) {
                                    if (cardIds.contains(card.getMultiverseid())) {
                                        currentCards.add(card);
                                    }
                                }

                                draftCardsAdapter.setCards(currentCards);
                                draftCardsAdapter.setOnClickListener(new DraftCardsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, int cardId, View v, String url) {
                                        Timber.tag("fart").i("card position: %s, card id: %s", position, cardId);
                                        Bundle b = new Bundle();
                                        b.putInt(AllMyConstants.CARD_ID, cardId);
                                        b.putInt(AllMyConstants.CURRENT_SEAT, currentSeatNum);
                                        b.putInt(AllMyConstants.PACKS_NUMBER, currentPackNum);
                                        b.putString(AllMyConstants.CARD_URL, url);
                                        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(v, "mtgCardScale").build();
                                        Navigation.findNavController(view).navigate(R.id.action_draftingHappyFunTimeFragment_to_singleCardDisplayFragment, b, null, extras);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        //getPack(packViewModel, magicCardViewModel, currentSeatNum, currentPackNum);

        return view;
    }

    private void updateBoard() {

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                currentCards = Parcels.unwrap(handlerBundle.getParcelable(AllMyConstants.CURRENT_CARDS));
            }
        };

        t = new Thread(new UpdateBoard(handler, mPreferences, mEditor, packViewModel, magicCardViewModel));
        t.start();
    }

    private void getPack(PackViewModel packViewModel, MagicCardViewModel magicCardViewModel, int seatNumber, int packNumber) {

        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                currentCards = Parcels.unwrap(handlerBundle.getParcelable(AllMyConstants.CURRENT_CARDS));
            }
        };

        t = new Thread(new BuildPack(handler, packViewModel, magicCardViewModel, seatNumber, packNumber));
        t.start();
    }

    public void sendDataBackToActivity(String string) {
        if (mListener != null) {
            mListener.onDraftingHappyFunTimeInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDraftingHappyFunTimeInteraction) {
            mListener = (OnDraftingHappyFunTimeInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDraftingHappyFunTimeInteraction");
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

    private int getRandomNum(int max) {
        Random r = new Random();
        return r.nextInt((max - 1) + 1);
    }

    public interface OnDraftingHappyFunTimeInteraction {
        // TODO: Update argument type and name
        void onDraftingHappyFunTimeInteraction(String string);
    }

    public class UpdateBoard implements Runnable {
        Handler handler;
        SharedPreferences prefs;
        SharedPreferences.Editor mEditor;
        PackViewModel packViewModel;
        MagicCardViewModel magicCardViewModel;

        public UpdateBoard(Handler handler, SharedPreferences prefs, SharedPreferences.Editor mEditor, PackViewModel packViewModel, MagicCardViewModel magicCardViewModel) {
            this.handler = handler;
            this.prefs = prefs;
            this.mEditor = mEditor;
            this.packViewModel = packViewModel;
            this.magicCardViewModel = magicCardViewModel;
        }

        @Override
        public void run() {
            // set to hold the chosen card ids
            Set<String> cardsHash = new HashSet<>();
            mEditor = prefs.edit();
            mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, false);

            // the card id that was picked, the current seat, and the current pack number
            int cardIdPicked = prefs.getInt(AllMyConstants.CARD_ID, 0);
            int seat = prefs.getInt(AllMyConstants.CURRENT_SEAT, 0);
            int packNum = prefs.getInt(AllMyConstants.PACKS_NUMBER, 0);
            int pick = prefs.getInt(AllMyConstants.CURRENT_PICK, 0);

            // check for the string set so we can assign it and not overwrite stuff
            if(prefs.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                cardsHash = prefs.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);
            }

            // add the chosen id to the string set
            cardsHash.add(String.valueOf(cardIdPicked));

            // put the string set into prefs.
            mEditor.putStringSet(AllMyConstants.THE_CHOSEN_CARDS, cardsHash);

            // i just want to know whats in the hash.
            Iterator<String> itr = cardsHash.iterator();
            while(itr.hasNext()) {
                String id = itr.next();
                int currentId = Integer.parseInt(id);
                Timber.tag("fart").i("current player picks: %s", currentId);
            }

            // get the current pack object
            // SELECT * From packs WHERE seat_num = :seatNum AND booster_num = :boosterNum
            Pack currentPack = packViewModel.getPlayerPacksByNum(seat, packNum);
            List<Integer> packCardIds = currentPack.getCardIDs();

            // remove the players pick from the list
            packCardIds.remove(cardIdPicked);

            // take out 7 random cards from this pack. this represents the other 7 players picking
            // a card from it.
            for(int i = 0; i < 7; i++) {
                int r = getRandomNum(packCardIds.size());
                int idToRemove = packCardIds.get(r);
                packCardIds.remove(idToRemove);
            }

            // update the pack
            currentPack.setCardIDs(packCardIds);
            packViewModel.updatePack(currentPack);

            Timber.tag("fart").i(" ");
            Timber.tag("fart").i("inside run");
            Timber.tag("fart").i("current seat: %s, current pack: %s", seat, packNum);

            // if the pack is 3 (starts at zero so 0,1,2), start it over. otherwise, go up 1.
            packNum = (packCardIds.size() == 0) ? packNum + 1 : packNum;

            // adjust the seat count and pack number
            seat = (seat == 8) ? 1 : seat;

            // update the SharedPreferences
            mEditor.putInt(AllMyConstants.CURRENT_SEAT, seat);
            mEditor.putInt(AllMyConstants.PACKS_NUMBER, packNum);
            mEditor.putInt(AllMyConstants.CURRENT_PICK, pick + 1);

            Timber.tag("fart").i("bext seat: %s, next pack: %s", seat, packNum);

            mEditor.commit();
        }
    }

    public class BuildPack implements Runnable {
        Handler handler;
        PackViewModel packViewModel;
        MagicCardViewModel magicCardViewModel;
        int seatNumber;
        int packNumber;

        List<Integer> cardIds = new ArrayList<>();
        List<MagicCard> packCards = new ArrayList<>();

        public BuildPack(Handler handler, PackViewModel packViewModel, MagicCardViewModel magicCardViewModel, int seatNumber, int packNumber) {
            this.handler = handler;
            this.packViewModel = packViewModel;
            this.magicCardViewModel = magicCardViewModel;
            this.seatNumber = seatNumber;
            this.packNumber = packNumber;
        }

        @Override
        public void run() {
            // get the correct pack using the current seat and pack number
            Pack currentPack = packViewModel.getPlayerPacksByNum(seatNumber, packNumber);

            // get the card ids from the pack
            cardIds = currentPack.getCardIDs();

            // get the cards using the ids
            for (int id : cardIds) {
                packCards.add(magicCardViewModel.getmCard(id));
            }

            Message m = Message.obtain();
            Bundle b = new Bundle();
            b.putParcelable(AllMyConstants.CURRENT_CARDS, Parcels.wrap(packCards));
            m.setData(b);
            handler.sendMessage(m);
        }
    }
}
