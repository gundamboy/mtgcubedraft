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

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment {
    @BindView(R.id.signInEmail) com.google.android.material.textfield.TextInputEditText emailText;
    @BindView(R.id.signInPassword) com.google.android.material.textfield.TextInputEditText passwordText;
    @BindView(R.id.emailSignInButton) com.google.android.material.button.MaterialButton signInButton;
    private Unbinder unbinder;
    private FirebaseAuth mAuth;


    private OnFragmentInteractionListener mListener;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }

    public static EmailPasswordFragment newInstance() {
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
        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

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
                    loginWithEmail(email, password);
                }
            }
        });

        return view;
    }

    private void loginWithEmail(String email, String password) {
        if (mListener != null) {
            mListener.onEmailFragmentInteraction(email, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


    public interface OnFragmentInteractionListener {
        void onEmailFragmentInteraction(String email, String password);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
