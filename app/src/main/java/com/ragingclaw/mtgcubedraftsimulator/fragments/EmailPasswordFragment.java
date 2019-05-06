package com.ragingclaw.mtgcubedraftsimulator.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment {
    @BindView(R.id.fieldEmail) com.google.android.material.textfield.TextInputEditText emailText;
    @BindView(R.id.fieldPassword) com.google.android.material.textfield.TextInputEditText passwordText;
    @BindView(R.id.signInButton) com.google.android.material.button.MaterialButton signInButton;
    @BindView(R.id.emailCreateAccountButton) com.google.android.material.button.MaterialButton createAccountButton;
    @BindView(R.id.signOutButton) com.google.android.material.button.MaterialButton signOutButton;
    @BindView(R.id.verifyEmailButton) com.google.android.material.button.MaterialButton verifyEmailButton;
    private Unbinder unbinder;


    private OnFragmentInteractionListener mListener;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.login_email_password_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
