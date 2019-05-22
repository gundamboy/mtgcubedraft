package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ragingclaw.mtgcubedraftsimulator.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewCubeStepTwoFragment.OnFragmentInteractionListenerStepTwo} interface
 * to handle interaction events.
 * Use the {@link NewCubeStepTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCubeStepTwoFragment extends Fragment {
    private OnFragmentInteractionListenerStepTwo mListener;

    public NewCubeStepTwoFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepTwoFragment newInstance(String param1, String param2) {
        NewCubeStepTwoFragment fragment = new NewCubeStepTwoFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_cube_step_two, container, false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepTwo(uri);
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

    public interface OnFragmentInteractionListenerStepTwo {
        // TODO: Update argument type and name
        void onFragmentInteractionStepTwo(Uri uri);
    }
}
