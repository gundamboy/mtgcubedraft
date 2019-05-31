package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

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
    private Unbinder unbinder;
    private int multiVerseId;
    private String cardUrl;
    private OnSingleCardFragmentInteractionListener mListener;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_single_card_display, container, false);
        unbinder = ButterKnife.bind(this, view);

        Picasso.get().load(cardUrl).placeholder(R.color.colorAccent).into(mtgCardImage, new com.squareup.picasso.Callback(){

            @Override
            public void onSuccess() {
                Timber.tag("fart").i("picasso was successful");
            }

            @Override
            public void onError(Exception e) {
                Timber.tag("fart").i("picasso failed: %s", e.getMessage());
            }
        });

        mtgCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(v, "mtgCardScale").build();
                Navigation.findNavController(view).navigate(R.id.action_singleCardDisplayFragment_to_draftingHappyFunTimeFragment, null, null, extras);
            }
        });

        return view;
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

    public interface OnSingleCardFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSingleCardFragmentInteraction(Uri uri);
    }
}
