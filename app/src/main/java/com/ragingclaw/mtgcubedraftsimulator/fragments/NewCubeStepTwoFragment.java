package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ragingclaw.mtgcubedraftsimulator.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.api.SetAPI;
import io.magicthegathering.javasdk.resource.Card;
import io.magicthegathering.javasdk.resource.MtgSet;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewCubeStepTwoFragment.OnFragmentInteractionListenerStepTwo} interface
 * to handle interaction events.
 * Use the {@link NewCubeStepTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCubeStepTwoFragment extends Fragment {
    @BindView (R.id.cube_name) TextView mCubeName;
    private Unbinder unbinder;

    private OnFragmentInteractionListenerStepTwo mListener;
    private List<String> mSetCodes = new ArrayList<>();
    private List<Card> mCubeCards = new ArrayList<>();

    public NewCubeStepTwoFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepTwoFragment newInstance(String param1, String param2) {
        NewCubeStepTwoFragment fragment = new NewCubeStepTwoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_cube_step_two, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mCubeName.setText(getArguments().getString("cubeName"));
        }

        generateCube();

        return view;
    }

    private void generateCube() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //List<MtgSet> sets = SetAPI.getAllSets();
                List<Card> allCards = CardAPI.getAllCards();

                //String code = set.getCode();
                //Timber.tag("fart").i("sets size: %s", sets.size());
                Timber.tag("fart").i("allCards size: %s", allCards.size());
//                for (int i = 0; i < 360; i++) {
//                    Random r = new Random();
//                    int randon_index = r.nextInt((allCards.size() - 1) + 1);
//                    mCubeCards.add(allCards.get(randon_index));
//                }
//
//                // this works
//                Timber.tag("fart").i("mCubeCards size: %s", mCubeCards.size());

            }
        }).start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendDataToActivity(String string) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepTwo(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerStepTwo) {
            mListener = (OnFragmentInteractionListenerStepTwo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerStepTwo");
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

    public interface OnFragmentInteractionListenerStepTwo {
        // TODO: Update argument type and name
        void onFragmentInteractionStepTwo(String string);
    }
}
