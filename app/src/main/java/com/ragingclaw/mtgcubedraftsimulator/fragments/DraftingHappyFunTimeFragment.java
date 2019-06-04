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
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
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
    @BindView(R.id.draft_complete_layout) FrameLayout draftCompleteLayout;
    @BindView(R.id.draft_done_dialog_button) Button draftDoneButton;

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
        int pck = currentPackNum+1;
        String title = "Pack" + pck + ", Pick" + currentPickNum;
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
                int pck = currentPackNum+1;
                String title = "Pack" + pck + ", Pick" + currentPickNum;
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

                            currentPickNum = cardsHash.size() + 1;

                            Pack currentPack = null;
                            List<Integer> currentPackCardIds = new ArrayList<>();


                            for (Pack p : packs) {

                                if (p.getSeat_num() == currentSeatNum) {
                                    if (p.getBooster_num() == currentPackNum) {
                                        currentPack = p;
                                        currentPackCardIds = p.getCardIDs();
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
                            }

                            if(timeToChangePacks) {
                                if (currentPackNum < 2) {
                                    currentPackNum += 1;
                                    currentSeatNum = 1;
                                    timeToChangePacks = false;
                                    mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                                } else {
                                    mEditor.commit();
                                    draftCardsRecyclerView.setVisibility(View.GONE);
                                    draftCompleteLayout.setVisibility(View.VISIBLE);

                                    draftDoneButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Navigation.findNavController(view).navigate(R.id.action_draftingHappyFunTimeFragment_to_endGameFragment2, null, null, null);
                                        }
                                    });
                                }
                            }


                            mEditor.putInt(AllMyConstants.CURRENT_PACK, currentPackNum);
                            mEditor.putInt(AllMyConstants.CURRENT_SEAT, currentSeatNum);
                            mEditor.putInt(AllMyConstants.CURRENT_PICK, currentPickNum);

                            mEditor.commit();

                            flag[0] = 1;
                        }
                    }
                }






                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();

                    if (p.getSeat_num() == currentSeatNum && p.getBooster_num() == currentPackNum) {
                        magicCardViewModel.getmAllCards().observe(getActivity(), new Observer<List<MagicCard>>() {
                            @Override
                            public void onChanged(List<MagicCard> magicCards) {
                                int pck = currentPackNum+1;
                                String title = "Pack" + pck + ", Pick" + currentPickNum;
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

    public interface OnDraftingHappyFunTimeInteraction {
        // TODO: Update argument type and name
        void onDraftingHappyFunTimeInteraction(String string);
    }

}