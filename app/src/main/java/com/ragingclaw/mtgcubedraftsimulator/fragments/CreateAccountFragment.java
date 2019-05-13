package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {
    @BindView(R.id.fieldEmail) com.google.android.material.textfield.TextInputEditText emailText;
    @BindView(R.id.fieldPassword) com.google.android.material.textfield.TextInputEditText passwordText;
    @BindView(R.id.newAccountButton) com.google.android.material.button.MaterialButton createAccountButton;

    private Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    private PasswordEncryptionWarningFragment passwordEncryptionWarningFragment;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_create_account_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        passwordEncryptionWarningFragment = new PasswordEncryptionWarningFragment();
        passwordEncryptionWarningFragment.show(getFragmentManager(), "passwordEncryptionWarningFragment");


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag("fart").i("sign in with email pressed");
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view, Bundle bundle) {
        if (mListener != null) {
            mListener.onCreateAccountFragmentInteraction(view, bundle);
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
        void onCreateAccountFragmentInteraction(View view, Bundle bundle);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
