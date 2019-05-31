package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.ui.EqualSpacingItemDecoration;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private int packNum = 0;
    private int pickNum = 1;
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


            if(key.equals(AllMyConstants.CARD_ID)) {

                updateBoard();
            }

            if(key.equals(AllMyConstants.CURRENT_SEAT)) {
                //currentSeatNum = sharedPreferences.getInt(AllMyConstants.CURRENT_SEAT, 0);
                Timber.tag("fart").i("seat pref changed.");
            }
            if(key.equals(AllMyConstants.PACKS_NUMBER)) {packNum = sharedPreferences.getInt(AllMyConstants.PACKS_NUMBER, 0);}
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
        //draftCardsRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(36, EqualSpacingItemDecoration.GRID));
        draftCardsAdapter = new DraftCardsAdapter();
        draftCardsRecyclerView.setAdapter(draftCardsAdapter);

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        packViewModel = ViewModelProviders.of(getActivity()).get(PackViewModel.class);



        packViewModel.getAllPacks().observe(this, new Observer<List<Pack>>() {
            @Override
            public void onChanged(List<Pack> packs) {
                Timber.tag("fart").i("current seat: %s, current pack: %s", currentSeatNum, packNum);
                for(Pack p : packs) {
                    List<Integer> cardIds = p.getCardIDs();

                    if (p.getSeat_num() == currentSeatNum && p.getBooster_num() == packNum) {
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
                                        b.putInt(AllMyConstants.PACKS_NUMBER, packNum);
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

        //getPack(packViewModel, magicCardViewModel, currentSeatNum, packNum);

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
            // TODO: the card got picked.
            //  remove the card from the packNum. Store it into a sharedprefs list
            //  Change the seat and pack number. load the next pack into the adapter
            //  Take 7 random cards out of the packNum
            //  Change the packNum global
            // set to hold the chosen card ids
            Set<String> cardsHash = new HashSet<>();
            mEditor = prefs.edit();

            // the card id that was picked, the current seat, and the current pack number
            int cardIdPicked = prefs.getInt(AllMyConstants.CARD_ID, 0);
            int seat = prefs.getInt(AllMyConstants.CURRENT_SEAT, 0);
            int packNum = prefs.getInt(AllMyConstants.PACKS_NUMBER, 0);

            // check for the string set so we can assign it and not overwrite stuff
            if(prefs.contains(AllMyConstants.THE_CHOSEN_CARDS)) {
                cardsHash = prefs.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);
            }

            // add the chosen id to the string set
            cardsHash.add(String.valueOf(cardIdPicked));

            // put the string set into prefs.
            mEditor.putStringSet(AllMyConstants.THE_CHOSEN_CARDS, cardsHash);

            // adjust the seat count
            if(seat == 8) {
                // this is the last seat, start a new pack
                packNum += 1;

                // reset the seat
                seat = 1;
            }

            mEditor.putInt(AllMyConstants.CURRENT_SEAT, seat);









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
