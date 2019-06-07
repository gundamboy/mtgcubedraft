package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.DraftCardsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EndGameFragment extends Fragment {
    @BindView(R.id.draft_cards_recyclerview) RecyclerView draftCardsRecyclerView;
    @BindView(R.id.draft_done_button) com.google.android.material.button.MaterialButton draftDoneButton;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private GridLayoutManager gridLayoutManager;
    private DraftCardsAdapter draftCardsAdapter;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    private OnEndGameFragmentInteractionListener mListener;
    private final List<MagicCard> currentCards = new ArrayList<>();
    private List<Integer> cardIdsFromPrefs = new ArrayList<>();

    public EndGameFragment() {
        // Required empty public constructor
    }

    public static EndGameFragment newInstance() {
        return new EndGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end_game, container, false);
        Unbinder unbinder = ButterKnife.bind(this, view);

        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
            Navigation.findNavController(view).popBackStack();
        }

        sendDataToActivity(getActivity().getResources().getString(R.string.draft_deck_review));

        MagicCardViewModel magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> cardsHash = mPreferences.getStringSet(AllMyConstants.THE_CHOSEN_CARDS, null);

        // convert the string set into an integer list. one line to rule them all.
        if((cardsHash != null ? cardsHash.size() : 0) > 0) {
            cardIdsFromPrefs = cardsHash.stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
        draftCardsRecyclerView.setHasFixedSize(true);
        draftCardsAdapter = new DraftCardsAdapter();
        draftCardsRecyclerView.setAdapter(draftCardsAdapter);


        // database stuff
        magicCardViewModel.getmAllCards().observe(getActivity(), magicCards -> {
            // get the cards we need to display
            currentCards.clear();
            for (MagicCard card : magicCards) {
                if (cardIdsFromPrefs.contains(card.getMultiverseid())) {
                    currentCards.add(card);
                }
            }

            if (currentCards.size() > 0) {
                draftCardsAdapter.setCards(currentCards);
                draftCardsAdapter.setOnClickListener((cardId, v, url) -> {
                    Bundle b = new Bundle();
                    b.putInt(AllMyConstants.CARD_ID, cardId);
                    b.putString(AllMyConstants.CARD_URL, url);
                    b.putBoolean(AllMyConstants.GO_BACK_TO_DECK, true);
                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(v, AllMyConstants.SHARED_ANIMATION).build();
                    Navigation.findNavController(view).navigate(R.id.action_endGameFragment_to_singleDeckCardFragment, b, null, extras);
                });
            }
        });

        draftDoneButton.setOnClickListener(v -> {
            currentCards.clear();
            mEditor = mPreferences.edit();

            Set<String> names;
            if(mPreferences.contains(AllMyConstants.CUBE_NAMES)) {
                names = mPreferences.getStringSet(AllMyConstants.CUBE_NAMES, null);
            } else {
                names = null;
            }
            mEditor.clear();
            mEditor.putStringSet(AllMyConstants.CUBE_NAMES, names);
            mEditor.remove(AllMyConstants.THE_CHOSEN_CARDS);

            mEditor.apply();

            Navigation.findNavController(view).navigate(R.id.action_endGameFragment_to_hostFragment, null, null, null);
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
                    + getActivity().getString(R.string.fragment_interaction_error_end_text));
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
        mListState = Objects.requireNonNull(draftCardsRecyclerView.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // restore recycler view scroll position
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(() -> {
                mListState = mBundleRecyclerViewState.getParcelable(AllMyConstants.RECYCLER_RESTORE);
                Objects.requireNonNull(draftCardsRecyclerView.getLayoutManager()).onRestoreInstanceState(mListState);

            }, 50);
        }


        draftCardsRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public interface OnEndGameFragmentInteractionListener {
        void onEndGameFragmentInteraction(String string);
    }
}
