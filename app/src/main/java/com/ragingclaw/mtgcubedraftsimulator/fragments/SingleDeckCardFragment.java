package com.ragingclaw.mtgcubedraftsimulator.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleDeckCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleDeckCardFragment extends Fragment {
    @BindView(R.id.mtg_card)
    ImageView mtgCardImage;
    @BindView(R.id.go_back_button) com.google.android.material.button.MaterialButton goBackButton;
    private Unbinder unbinder;
    private int multiVerseId;
    private int currentSeat;
    private int packNumber;
    private String cardUrl;
    private SingleCardDisplayFragment.OnSingleCardFragmentInteractionListener mListener;


    public SingleDeckCardFragment() {
        // Required empty public constructor
    }


    public static SingleDeckCardFragment newInstance() {
        return new SingleDeckCardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            multiVerseId = getArguments().getInt(AllMyConstants.CARD_ID);
            cardUrl = getArguments().getString(AllMyConstants.CARD_URL);
            currentSeat = getArguments().getInt(AllMyConstants.CURRENT_SEAT);
            packNumber = getArguments().getInt(AllMyConstants.CURRENT_PACK);

        }

        if (savedInstanceState != null) {
            multiVerseId = savedInstanceState.getInt(AllMyConstants.CARD_ID);
            cardUrl = savedInstanceState.getString(AllMyConstants.CARD_URL);
            currentSeat = savedInstanceState.getInt(AllMyConstants.CURRENT_SEAT);
            packNumber = savedInstanceState.getInt(AllMyConstants.CURRENT_PACK);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // just shows the card that was picked and bounces some info back to the last fragment
        // to prevent crashing
        View view;

        if (container != null) {
            container.removeAllViewsInLayout();
        }

        int orientation = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            // inflate portrait layout
            view =  inflater.inflate(R.layout.fragment_single_deck_card, container, false);
        } else {
            // inflate landscape layout
            view =  inflater.inflate(R.layout.fragment_single_deck_card, container, false);
        }

        unbinder = ButterKnife.bind(this, view);

        Picasso.get().load(cardUrl).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.mtg_card_back).into(mtgCardImage);

        goBackButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_singleDeckCardFragment_to_endGameFragment, null, null, null));

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSingleCardFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SingleCardDisplayFragment.OnSingleCardFragmentInteractionListener) {
            mListener = (SingleCardDisplayFragment.OnSingleCardFragmentInteractionListener) context;
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(AllMyConstants.CARD_ID, multiVerseId);
        outState.putString(AllMyConstants.CARD_URL, cardUrl);
        outState.putInt(AllMyConstants.CURRENT_SEAT, currentSeat);
        outState.putInt(AllMyConstants.CURRENT_PACK, packNumber);
    }

}
