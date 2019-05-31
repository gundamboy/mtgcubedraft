package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListenerStepOne} interface
 * to handle interaction events.
 * Use the {@link NewCubeStepOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCubeStepOneFragment extends Fragment {
    @BindView(R.id.btn_generate_cube) com.google.android.material.button.MaterialButton generateCubeButton;
    @BindView(R.id.cube_name_edit_text) TextInputEditText cubeName;
    private Unbinder unbinder;

    private OnFragmentInteractionListenerStepOne mListener;

    public NewCubeStepOneFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepOneFragment newInstance() {
        NewCubeStepOneFragment fragment = new NewCubeStepOneFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_cube_step_one, container, false);
        unbinder = ButterKnife.bind(this, view);

        generateCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate name

                if(!TextUtils.isEmpty(cubeName.getText().toString())) {
                    String name = cubeName.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString(AllMyConstants.CUBE_NAME, name);
                    bundle.putInt(AllMyConstants.CUBE_ID, 0);
                    Navigation.findNavController(view).navigate(R.id.action_newCubeStepOneFragment_to_newCubeBuilderFragment, bundle);
                } else {
                    // show toast because i dont have time for fancy shit.
                    Toast.makeText(getContext(), "You must give your cube a name to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendDataToActivity(String title) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepOne(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerStepOne) {
            mListener = (OnFragmentInteractionListenerStepOne) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerStepOne");
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

    public interface OnFragmentInteractionListenerStepOne {
        // TODO: Update argument type and name
        void onFragmentInteractionStepOne(String title);
    }
}
