package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.LoginActivity;
import com.ragingclaw.mtgcubedraftsimulator.activities.MainActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.DraftCardsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


public class EndGameFragment extends Fragment {
    @BindView(R.id.draft_cards_recyclerview) RecyclerView draftCardsRecyclerView;
    @BindView(R.id.draft_done_button) com.google.android.material.button.MaterialButton draftDoneButton;
    private Unbinder unbinder;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private MagicCardViewModel magicCardViewModel;
    private GridLayoutManager gridLayoutManager;
    private DraftCardsAdapter draftCardsAdapter;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    private OnEndGameFragmentInteractionListener mListener;
    private List<MagicCard> currentCards = new ArrayList<>();
    private List<Integer> cardIdsFromPrefs = new ArrayList<>();

    public EndGameFragment() {
        // Required empty public constructor
    }

    public static EndGameFragment newInstance() {
        EndGameFragment fragment = new EndGameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end_game, container, false);
        unbinder = ButterKnife.bind(this, view);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
            Navigation.findNavController(view).popBackStack();
        }

        sendDataToActivity("Draft Deck Review");

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> cardsHash = mPreferences.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);

        // convert the string set into an integer list. one line to rule them all.
        if(cardsHash.size() > 0) {
            cardIdsFromPrefs = cardsHash.stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
        draftCardsRecyclerView.setHasFixedSize(true);
        draftCardsAdapter = new DraftCardsAdapter();
        draftCardsRecyclerView.setAdapter(draftCardsAdapter);


        // database stuff
        magicCardViewModel.getmAllCards().observe(getActivity(), new Observer<List<MagicCard>>() {
            @Override
            public void onChanged(List<MagicCard> magicCards) {
                // get the cards we need to display
                currentCards.clear();
                for (MagicCard card : magicCards) {
                    if (cardIdsFromPrefs.contains(card.getMultiverseid())) {
                        currentCards.add(card);
                    }
                }

                if (currentCards.size() > 0) {
                    draftCardsAdapter.setCards(currentCards);
                    draftCardsAdapter.setOnClickListener(new DraftCardsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, int cardId, View v, String url) {
                            Bundle b = new Bundle();
                            b.putInt(AllMyConstants.CARD_ID, cardId);
                            b.putString(AllMyConstants.CARD_URL, url);
                            b.putBoolean(AllMyConstants.GO_BACK_TO_DECK, true);
                            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(v, "mtgCardScale").build();
                            Navigation.findNavController(view).navigate(R.id.action_endGameFragment_to_singleDeckCardFragment, b, null, extras);
                        }
                    });
                }
            }
        });

        draftDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCards.clear();
                mEditor = mPreferences.edit();

                Set<String> names = new ArraySet<>();
                if(mPreferences.contains(AllMyConstants.CUBE_NAMES)) {
                    names = mPreferences.getStringSet(AllMyConstants.CUBE_NAME, null);
                } else {
                    names = null;
                }
                mEditor.clear();
                mEditor.putStringSet(AllMyConstants.CUBE_NAMES, names);
                mEditor.remove(AllMyConstants.THE_CHOSEN_CARDS);

                mEditor.commit();

                Navigation.findNavController(view).navigate(R.id.action_endGameFragment_to_hostFragment, null, null, null);
            }
        });

        return view;
    }


    public void sendDataToActivity(String string) {
        if (mListener != null) {
            mListener.onEndGameFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEndGameFragmentInteractionListener) {
            mListener = (OnEndGameFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEndGameFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnEndGameFragmentInteractionListener {
        void onEndGameFragmentInteraction(String string);
    }
}
