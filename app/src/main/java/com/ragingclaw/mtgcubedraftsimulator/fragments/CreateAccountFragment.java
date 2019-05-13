package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {
    @BindView(R.id.createAccountEmail) com.google.android.material.textfield.TextInputEditText mEmailText;
    @BindView(R.id.createAccountPassword) com.google.android.material.textfield.TextInputEditText mPasswordText;
    @BindView(R.id.createAccountButton) com.google.android.material.button.MaterialButton mCreateAccountButton;

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
        
        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
            
        });

        return view;
    }

    private void authenticateUser () {
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            View toastView = getLayoutInflater().inflate(R.layout.custom_toast_view, null);
            TextView createAccountError = toastView.findViewById(R.id.createAccountErrorToast);
            createAccountError.setVisibility(View.VISIBLE);
            Toast toast = new Toast(getContext());
            toast.setView(toastView);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 500);
            toast.show();
        } else {
           createUser(email, password);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void createUser(String email, String password) {
        if (mListener != null) {
            mListener.onCreateAccountFragmentInteraction(email, password);
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
        void onCreateAccountFragmentInteraction(String email, String password);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
