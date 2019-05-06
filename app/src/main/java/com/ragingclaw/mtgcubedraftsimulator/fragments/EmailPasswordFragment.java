package com.ragingclaw.mtgcubedraftsimulator.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment {
    @BindView(R.id.fieldEmail) com.google.android.material.textfield.TextInputEditText emailText;
    @BindView(R.id.fieldPassword) com.google.android.material.textfield.TextInputEditText passwordText;
    @BindView(R.id.emailSignInButton) com.google.android.material.button.MaterialButton signInButton;
    private Unbinder unbinder;


    private OnFragmentInteractionListener mListener;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }

    public static EmailPasswordFragment newInstance(String param1, String param2) {
        EmailPasswordFragment fragment = new EmailPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_email_password_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        Timber.tag("fart").i("we made it!");

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag("fart").i("sign in with email pressed");
            }
        });

        return view;
    }

    private void loginWithEmail(View view, Bundle bundle) {
        if (mListener != null) {
            mListener.onEmailFragmentInteraction(view, bundle);
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
        void onEmailFragmentInteraction(View view, Bundle bundle);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
