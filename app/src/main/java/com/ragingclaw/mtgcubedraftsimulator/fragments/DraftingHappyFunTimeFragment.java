package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
    private MagicCardViewModel magicCardViewModel;
    private PackViewModel packViewModel;
    private GridLayoutManager gridLayoutManager;
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

                            // if the cardHash has 45 cards in it the draft is over, show a dialog
                            // and go to the deck review fragment
                            if (cardsHash.size() == 45) {
                                // commit the shared prefs so the hash is complete on the
                                // next fragment
                                mEditor.commit();
                                showDraftDoneDialog(view);
                            } else {

                                Pack currentPack = null;
                                List<Integer> currentPackCardIds = new ArrayList<>();

                                // get the current card ids for the active pack
                                for (Pack p : packs) {
                                    if (p.getSeat_num() == currentSeatNum) {
                                        if (p.getBooster_num() == currentPackNum) {
                                            currentPack = p;
                                            currentPackCardIds = p.getCardIDs();
                                        }
                                    }
                                }

                                // remove cards from the non active players packs
                                // do the 'game' processing here
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

                                        // remove the picked card from the pack
                                        if (p.getPackId() == currentPack.getPackId()) {
                                            currentPackCardIds.remove(Integer.valueOf(cardIdPicked));
                                            p.setCardIDs(currentPackCardIds);
                                            if (currentPackCardIds.size() == 0) {
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
                                    // pack is empty, time to switch
                                    if (currentPackNum < 2) {
                                        currentPackNum += 1;
                                        currentSeatNum = 1;
                                        timeToChangePacks = false;
                                        mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                                    }
                                }

                                // do the pack switch
                                if (timeToChangePacks) {
                                    if (currentPackNum < 2) {
                                        currentPackNum += 1;
                                        currentSeatNum = 1;
                                        timeToChangePacks = false;
                                        mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                                    }
                                }

                                mEditor.putInt(AllMyConstants.CURRENT_PACK, currentPackNum);
                                mEditor.putInt(AllMyConstants.CURRENT_SEAT, currentSeatNum);
                                mEditor.commit();

                                // flag to prevent multiple runs
                                flag[0] = 1;
                            }
                        }
                    }
                }


                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();

                    // livedata observers for data display
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
        // recycler view position saving
        mBundleRecyclerViewState = new Bundle();
        mListState = draftCardsRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // restore recycler view scroll position
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

    private void showDraftDoneDialog(View view) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(inflater.inflate(R.layout.dialog_draft_complete, null))
                .setPositiveButton(R.string.dialog_draft_complete_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Navigation.findNavController(view).navigate(R.id.action_draftingHappyFunTimeFragment_to_endGameFragment, null, null, null);
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface OnDraftingHappyFunTimeInteraction {
        void onDraftingHappyFunTimeInteraction(String string);
    }
}
