package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private int cardsThatShouldBeShown = 15;

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
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key.equals(AllMyConstants.CARD_ID) && prefs.getBoolean(AllMyConstants.UPDATE_DRAFT, false)) {

                updateBoard();
            }

            if(key.equals(AllMyConstants.CURRENT_SEAT)) { currentSeatNum = prefs.getInt(AllMyConstants.CURRENT_SEAT, 0); }
            if(key.equals(AllMyConstants.PACKS_NUMBER)) { currentPackNum = prefs.getInt(AllMyConstants.PACKS_NUMBER, 0); }
            if(key.equals(AllMyConstants.CURRENT_PICK)) { currentPickNum = prefs.getInt(AllMyConstants.CURRENT_PICK, 1); }
            if(key.equals("cards_left")) { cardsThatShouldBeShown = prefs.getInt("cards_left", 15); }
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
        unbinder = ButterKnife.bind(this, view);;
        String title = "Pack " + currentPackNum + ", Pick " + currentPickNum + ", Seat " + currentSeatNum + ", Cards: " + cardsThatShouldBeShown;
        sendDataBackToActivity(title);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
        draftCardsRecyclerView.setHasFixedSize(true);
        draftCardsAdapter = new DraftCardsAdapter();
        draftCardsRecyclerView.setAdapter(draftCardsAdapter);

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        packViewModel = ViewModelProviders.of(getActivity()).get(PackViewModel.class);

        packViewModel.getAllPacks().observe(this, new Observer<List<Pack>>() {
            @Override
            public void onChanged(List<Pack> packs) {
                String title = "PA" + currentPackNum + ", PK" + currentPickNum + ", S" + currentSeatNum + ", Cs:" + cardsThatShouldBeShown;
                sendDataBackToActivity(title);
                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();
                    if (p.getSeat_num() == currentSeatNum && p.getBooster_num() == currentPackNum) {
                        magicCardViewModel.getmAllCards().observe(getActivity(), new Observer<List<MagicCard>>() {
                            @Override
                            public void onChanged(List<MagicCard> magicCards) {
                                String title = "PA" + currentPackNum + ", PK" + currentPickNum + ", S" + currentSeatNum + ", Cs:" + cardsThatShouldBeShown;
                                sendDataBackToActivity(title);
                                currentCards.clear();
                                for (MagicCard card : magicCards) {
                                    if (cardIds.contains(card.getMultiverseid())) {
                                        currentCards.add(card);
                                    }
                                }

                                draftCardsAdapter.setCards(currentCards);
                                draftCardsAdapter.setOnClickListener(new DraftCardsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, int cardId, View v, String url) {
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

    private int getRandomFromList(List<Integer> idPool) {
        Random r = new Random();
        return idPool.get(r.nextInt(idPool.size()));
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
            Timber.tag("fart").i(" ");
            Timber.tag("fart").d("*******");

            // set to hold the chosen card ids
            Set<String> cardsHash = new HashSet<>();
            mEditor = prefs.edit();
            mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, false);

            // the card id that was picked, the current seat, and the current pack number
            int cardIdPicked = prefs.getInt(AllMyConstants.CARD_ID, 0);
            int seat = prefs.getInt(AllMyConstants.CURRENT_SEAT, 1);
            int currentPackNumber = prefs.getInt(AllMyConstants.PACKS_NUMBER, 0);
            int pick = 1;






            // check for the string set so we can assign it and not overwrite stuff
            if(prefs.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                cardsHash = prefs.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);
            }

            cardsHash.add(String.valueOf(cardIdPicked));
            pick = cardsHash.size() + 1;
            Timber.tag("fart CARDHASH:").i("total cards picked: %s", cardsHash.size());
            Timber.tag("fart CARDHASH:").i("current player picks: %s", cardsHash.toString());

            // put the string set into prefs.
            mEditor.putStringSet(AllMyConstants.THE_CHOSEN_CARDS, cardsHash);






            if (cardsHash.size() == 45) {
                mEditor.commit();
                // game's over man. send it off to the next fragment
            } else {
                // get the current pack object
                Pack currentPack = packViewModel.getPlayerPacksByNum(seat, currentPackNumber);
                List<Integer> currentPackCardIds = currentPack.getCardIDs();
                Timber.tag("fart CURRENT PACK").i("currentPackCardIds size = %s", currentPackCardIds.size());




                // operations on the pack/database
                if (currentPackCardIds.size() != 0) {
                    // remove the card ID that was picked from the current packs id list
                    currentPackCardIds.remove(Integer.valueOf(cardIdPicked));

                    // update the pack
                    currentPack.setCardIDs(currentPackCardIds);





                    // a card from each other pack also needs to be removed.
                    List<Pack> allPacks = packViewModel.getAllPacksStatic();
                    for (Pack p : allPacks) {
                        // only get packs that are using the current pack number and not the current pack
                        if(p.getPackId() != currentPack.getPackId() && p.getBooster_num() == currentPackNumber) {

                            // make a list to hold the card ids in this pack
                            List<Integer> ids = p.getCardIDs();

                            // get a random card out of the list and remove it
                            int randomCardId = getRandomFromList(ids);
                            Timber.tag("fart").i("pack size = %s || pack seat = %s, cardID = %s", ids.size(), p.getSeat_num(), randomCardId);
                            ids.remove(Integer.valueOf(randomCardId));

                            // reset the ids in the pack
                            p.setCardIDs(ids);



                            // update this pack in the database
                            try {
                                packViewModel.updatePack(p);
                            } catch (Exception e) {
                                Timber.tag("fart").w(" ");
                                Timber.tag("fart").e("---------------------------------------");
                                Timber.tag("fart").e("CRASH AT ALL NON USER PACKS Integer.valueOf(randomCardId)");
                                Timber.tag("fart").e("pack size = %s || pack seat = %s, cardID = %s", ids.size(), p.getSeat_num(), randomCardId);
                                e.printStackTrace();
                                Timber.tag("fart").e("---------------------------------------");
                                Timber.tag("fart").e("STACKTRACE MESSAGE::: %s", e.getMessage());
                                Timber.tag("fart").w(" ");
                            }
                        }
                    }





                    // adjust the seat
                    seat = (seat == 8) ? 1 : seat + 1;



                    // ends: if currentPackCardIds.size() != 0
                }  else {

                    Timber.tag("fart").i("currentPackCardIds size is zero???? = %s", currentPackCardIds.size());
                    if(currentPackNumber < 2) {
                        currentPackNumber += 1;
                        seat = 1;
                    } else {
                        // this is the end game stuff. final card in final pack has been reached
                        // cardsHash.size() should be 45 so this should never be reached
                    }

                }



                // update the SharedPreferences
                Timber.tag("fart").i("next seat: %s, next pack: %s, cards on the board: %s", seat, currentPackNumber, currentPackCardIds.size());
                mEditor.putInt(AllMyConstants.CURRENT_SEAT, seat);
                mEditor.putInt(AllMyConstants.PACKS_NUMBER, currentPackNumber);
                mEditor.putInt(AllMyConstants.CURRENT_PICK, pick);
                mEditor.putInt("cards_left", currentPackCardIds.size());

                // update the database
                try {
                    packViewModel.updatePack(currentPack);
                } catch (Exception e) {
                    Timber.tag("fart").w(" ");
                    Timber.tag("fart").e("---------------------------------------");
                    Timber.tag("fart").e("CRASH AT UPDATING CURRENTPACK IN THE DATABASE");
                    Timber.tag("fart").e("currentPack IDS size = %s || currentPack id = %s || seat = %s  || pack(booster) number = %s", currentPack.getCardIDs().size(), currentPack.getPackId(), currentPack.getSeat_num(), currentPack.getBooster_num());
                    e.printStackTrace();
                    Timber.tag("fart").e("---------------------------------------");
                    Timber.tag("fart").e("STACKTRACE MESSAGE::: %s", e.getMessage());
                    Timber.tag("fart").w(" ");
                }

                mEditor.commit();

            } // else for :if (cardsHash.size() == 45)

            Timber.tag("fart").d("*******");
            Timber.tag("fart").i(" ");
        } // ends Run()
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
