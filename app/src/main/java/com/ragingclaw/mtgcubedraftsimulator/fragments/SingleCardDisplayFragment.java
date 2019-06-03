package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleCardDisplayFragment.OnSingleCardFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleCardDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleCardDisplayFragment extends Fragment {
    @BindView(R.id.mtg_card) ImageView mtgCardImage;
    @BindView(R.id.draft_me_button) com.google.android.material.button.MaterialButton draftMeButton;
    @BindView(R.id.draft_me_button_go_back) com.google.android.material.button.MaterialButton goBackButton;
    private Unbinder unbinder;
    private int multiVerseId;
    private int currentSeat;
    private int packNumber;
    private String cardUrl;
    private OnSingleCardFragmentInteractionListener mListener;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public SingleCardDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleCardDisplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleCardDisplayFragment newInstance(String param1, String param2) {
        SingleCardDisplayFragment fragment = new SingleCardDisplayFragment();

        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_single_card_display, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Picasso.get().load(cardUrl).placeholder(R.color.colorAccent).into(mtgCardImage);

        mtgCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToDraftList(view);
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToDraftList(view);
            }
        });

        draftMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: store the id of the chosen card into shared preferences, or send it back in a bundle

                mEditor = mPreferences.edit();
                mEditor.putInt(AllMyConstants.CARD_ID, multiVerseId);
                mEditor.putInt(AllMyConstants.CURRENT_SEAT, currentSeat);
                mEditor.putInt(AllMyConstants.CURRENT_PACK, packNumber);
                mEditor.putBoolean(AllMyConstants.UPDATE_DRAFT, true);
                mEditor.putBoolean(AllMyConstants.START_DRAFT, false);
                mEditor.commit();

                Timber.tag("fart").i("the id of the drafted card: %s", multiVerseId);

                goBackToDraftList(view);
            }
        });

        return view;
    }

    private void goBackToDraftList(View view) {
        //FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(view, "mtgCardScale").build();
        Navigation.findNavController(view).navigate(R.id.action_singleCardDisplayFragment_to_draftingHappyFunTimeFragment, null, null, null);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSingleCardFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSingleCardFragmentInteractionListener) {
            mListener = (OnSingleCardFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSingleCardFragmentInteractionListener");
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

    public interface OnSingleCardFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSingleCardFragmentInteraction(Uri uri);
    }
}
