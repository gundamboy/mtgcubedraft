package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.DraftCardsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;
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
    private LinearLayoutManager linearLayoutManager;
    private DraftCardsAdapter draftCardsAdapter;
    private OnDraftingHappyFunTimeInteraction mListener;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    private int currentPackNum = 0;
    private int currentPickNum = 1;
    private int currentSeatNum = 1;
    private int cardsThatShouldBeShown = 15;

    private List<Pack> packs = new ArrayList<>();
    private List<MagicCard> currentCards = new ArrayList<>();
    private Set<String> cardsHash = new HashSet<>();
    private boolean timeToChangePacks = false;

    public DraftingHappyFunTimeFragment() {
        // Required empty public constructor
    }

    public static com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment newInstance() {
        com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment fragment = new com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment();

        return fragment;
    }

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key.equals(AllMyConstants.CARD_ID) && prefs.getBoolean(AllMyConstants.UPDATE_DRAFT, false)) {

            }

            if(key.equals(AllMyConstants.CURRENT_SEAT)) { currentSeatNum = prefs.getInt(AllMyConstants.CURRENT_SEAT, 0); }
            if(key.equals(AllMyConstants.CURRENT_PACK)) { currentPackNum = prefs.getInt(AllMyConstants.CURRENT_PACK, 0); }
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

        final int[] flag = {0};

        packViewModel.getAllPacks().observe(this, new Observer<List<Pack>>() {
            @Override
            public void onChanged(List<Pack> packs) {
                cardsThatShouldBeShown = 15 - cardsHash.size();
                String title = "PA" + currentPackNum + ", PK" + currentPickNum + ", S" + currentSeatNum + ", Cs:" + cardsThatShouldBeShown;
                sendDataBackToActivity(title);
                mEditor = mPreferences.edit();

                if(!mPreferences.getBoolean(AllMyConstants.START_DRAFT, true)) {
                    if (flag[0] == 0) {
                        if(mPreferences.getBoolean(AllMyConstants.UPDATE_DRAFT, true)) {
                            mEditor.putBoolean(AllMyConstants.START_DRAFT, false);
                            mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, false);

                            int cardIdPicked = mPreferences.getInt(AllMyConstants.CARD_ID, 0);
                            currentSeatNum = mPreferences.getInt(AllMyConstants.CURRENT_SEAT, 1);
                            currentPackNum = mPreferences.getInt(AllMyConstants.CURRENT_PACK, 0);

                            if (mPreferences.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                                cardsHash = mPreferences.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);
                            }

                            cardsHash.add(String.valueOf(cardIdPicked));
                            mEditor.putStringSet(AllMyConstants.THE_CHOSEN_CARDS, cardsHash);




                            Pack currentPack = null;
                            List<Integer> currentPackCardIds = new ArrayList<>();


                            for (Pack p : packs) {

                                if (p.getSeat_num() == currentSeatNum) {
                                    if (p.getBooster_num() == currentPackNum) {
                                        currentPack = p;
                                        currentPackCardIds = p.getCardIDs();
                                        Timber.tag("fart").w("getting currentPAckCardIds. line 168: size: %s", currentPackCardIds.size());
                                    }
                                }
                            }


                            if (currentPackCardIds.size() != 0) {
                                for (Pack p : packs) {
                                    if (p.getPackId() != currentPack.getPackId()) {
                                        if (p.getBooster_num() == currentPackNum) {
                                            List<Integer> ids = p.getCardIDs();
                                            Collections.shuffle(ids);
                                            ids.remove(0);
                                            p.setCardIDs(ids);
                                            if (ids.size() == 0) {
                                                packViewModel.deletePack(p);
                                            } else {
                                                packViewModel.updatePack(p);
                                            }
                                        }
                                    }

                                    if (p.getPackId() == currentPack.getPackId()) {
                                        currentPackCardIds.remove(Integer.valueOf(cardIdPicked));
                                        Timber.tag("fart").v("currentPackIds2: size: %s, ids: %s", currentPackCardIds.size(), currentPackCardIds.toString());
                                        p.setCardIDs(currentPackCardIds);
                                        if (currentPackCardIds.size() == 0) {
                                            Timber.tag("fart").e("currentPackCardIds is 0");
                                            packViewModel.deletePack(p);
                                            timeToChangePacks = true;
                                        } else {
                                            packViewModel.updatePack(p);
                                        }


                                        if (currentSeatNum == 8) {
                                            currentSeatNum = 1;
                                        } else {
                                            currentSeatNum += 1;
                                        }
                                    }
                                }
                            } else {
                                Timber.tag("fart").w("OH SHIT SON! CHANGE PACKS!");
                                Timber.tag("fart").v("currentPackNum: %s", currentPackNum);
                                if (currentPackNum < 2) {
                                    currentPackNum += 1;
                                    currentSeatNum = 1;
                                    Timber.tag("fart").i("currentPackNum is now: %s", currentPackNum);
                                    timeToChangePacks = false;
                                    mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                                }
                            }

                            if(timeToChangePacks) {
                                if (currentPackNum < 2) {
                                    currentPackNum += 1;
                                    currentSeatNum = 1;
                                    Timber.tag("fart").i("currentPackNum is now: %s", currentPackNum);
                                    timeToChangePacks = false;
                                    mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                                }
                            }


                            mEditor.putInt(AllMyConstants.CURRENT_PACK, currentPackNum);
                            mEditor.putInt(AllMyConstants.CURRENT_SEAT, currentSeatNum);

                            mEditor.commit();

                            flag[0] = 1;
                        }
                    }
                }






                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();

                    if(p.getBooster_num() == currentPackNum) {
                        Timber.tag("fart").d("             OBSERVER   Seat: %s, PackID: %s, BoosterNum: %s, CardIdsSize: %s, currentSeat: %s", p.getSeat_num(), p.getPackId(), p.getBooster_num(), p.getCardIDs().size(), currentSeatNum);
                    }

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
                                        Timber.tag("fart").i("the id of the clicked card: %s", cardId);
                                        Bundle b = new Bundle();
                                        b.putInt(AllMyConstants.CARD_ID, cardId);
                                        b.putInt(AllMyConstants.CURRENT_SEAT, currentSeatNum);
                                        b.putInt(AllMyConstants.CURRENT_PACK, currentPackNum);
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

        t = new Thread(new com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment.UpdateBoard(handler, mPreferences, mEditor, packViewModel, magicCardViewModel));
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
        if (context instanceof com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment.OnDraftingHappyFunTimeInteraction) {
            mListener = (com.ragingclaw.mtgcubedraftsimulator.fragments.DraftingHappyFunTimeFragment.OnDraftingHappyFunTimeInteraction) context;
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


    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        mListState = draftCardsRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(AllMyConstants.RECYCLER_RESTORE);
                    draftCardsRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }


        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
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

            mEditor = prefs.edit();
            mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, false);

            Timber.tag("fart").w("OH SHIT SON! CHANGE PACKS!");
            if(currentPackNum < 2) {
                currentPackNum += 1;
                currentSeatNum = 1;
            }


            // the card id that was picked, the current seat, and the current pack number
            int cardIdPicked = prefs.getInt(AllMyConstants.CARD_ID, 0);
            int seat = prefs.getInt(AllMyConstants.CURRENT_SEAT, 1);
            int currentPackNumber = prefs.getInt(AllMyConstants.CURRENT_PACK, 0);
            int pick = 1;


            /********************************************************/
            List<Pack> allPacks = packViewModel.getAllPacksStatic();
            Timber.tag("fart").w("BEFORE PROCESSING");
            for (Pack p : allPacks) {
                if(p.getBooster_num() == currentPackNumber) {
                    Timber.tag("fart").i("      Seat: %s, PackID: %s, BoosterNum: %s, CardIdsSize: %s", p.getSeat_num(), p.getPackId(), p.getBooster_num(), p.getCardIDs().size());
                }
            }




            // set to hold the chosen card ids
            Set<String> cardsHash = new HashSet<>();
            // check for the string set so we can assign it and not overwrite stuff

            if(prefs.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                cardsHash = prefs.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);
            }

            cardsHash.add(String.valueOf(cardIdPicked));
            pick = cardsHash.size() + 1;
            //Timber.tag("fart CARDHASH:").i("total cards picked: %s, current player picks: %s", cardsHash.size(), cardsHash.toString());

            // put the string set into prefs.
            mEditor.putStringSet(AllMyConstants.THE_CHOSEN_CARDS, cardsHash);






            if (cardsHash.size() == 45) {
                mEditor.commit();
                // game's over man. send it off to the next fragment
                Timber.tag("fart").i("GAME OVER MAN!");
            } else {
                // get the current pack object
                Pack currentPack = packViewModel.getPlayerPacksByNum(seat, currentPackNumber);
                List<Integer> currentPackCardIds = currentPack.getCardIDs();
                //Timber.tag("fart CURRENT PACK").i("currentPackCardIds size = %s", currentPackCardIds.size());




                // operations on the pack/database
                if (currentPackCardIds.size() != 0) {


                    // a card from each other pack also needs to be removed.
                    for (Pack p : allPacks) {
                        // only get packs that are using the current pack number and not the current pack
                        if(p != currentPack && p.getBooster_num() == currentPackNumber) {

                            // make a list to hold the card ids in this pack
                            List<Integer> ids = p.getCardIDs();

                            // get a random card out of the list and remove it
                            int randomCardId = getRandomFromList(ids);
                            //Timber.tag("fart").i("BEFORE:: pack size = %s || pack seat = %s, cardID = %s", ids.size(), p.getSeat_num(), randomCardId);
//                            ids.remove(Integer.valueOf(randomCardId));

                            Collections.shuffle(ids);
                            ids.remove(0);
                            // Timber.tag("fart").i("AFTER:: pack size = %s || pack seat = %s, cardID = %s", ids.size(), p.getSeat_num(), randomCardId);

                            // reset the ids in the pack
                            p.setCardIDs(ids);



                            // update this pack in the database
                            try {
                                Timber.tag("fart").i("the pack being updated: ID: %s, Seat: %s, PackNumber: %s, idCount: %s, cardIds: %s", p.getPackId(), p.getSeat_num(), p.getBooster_num(), p.getCardIDs().size(), p.getCardIDs().toString());
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







                    // remove the card ID that was picked from the current packs id list
                    //currentPackCardIds.remove(Integer.valueOf(cardIdPicked));
                    currentPackCardIds.remove(0);

                    // update the pack
                    currentPack.setCardIDs(currentPackCardIds);

                    // update the database
                    try {
                        Timber.tag("fart").i("the PACK being updated: ID: %s, Seat: %s, PackNumber: %s, idCount: %s, cardIds: %s", currentPack.getPackId(), currentPack.getSeat_num(), currentPack.getBooster_num(), currentPack.getCardIDs().size(), currentPack.getCardIDs().toString());
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
                Timber.tag("fart").i("next seat: %s, next pack: %s, cards on the board should be: %s", seat, currentPackNumber, currentPackCardIds.size());
                mEditor.putInt(AllMyConstants.CURRENT_SEAT, seat);
                mEditor.putInt(AllMyConstants.CURRENT_PACK, currentPackNumber);
                mEditor.putInt(AllMyConstants.CURRENT_PICK, pick);
                mEditor.putInt("cards_left", currentPackCardIds.size());



                /******************************************************/
                Timber.tag("fart").v("AFTER PROCESSING");
                List<Pack> verifyPacks = packViewModel.getAllPacksStatic();
                for (Pack p : verifyPacks) {
                    if(p.getBooster_num() == currentPackNumber) {
                        Timber.tag("fart").v("   VERIFYPACKS   Seat: %s, PackID: %s, BoosterNum: %s, CardIdsSize: %s", p.getSeat_num(), p.getPackId(), p.getBooster_num(), p.getCardIDs().size());
                    }
                }

                mEditor.commit();

            } // else for :if (cardsHash.size() == 45)

            Timber.tag("fart").d("*******");

        } // ends Run()
    }
}
