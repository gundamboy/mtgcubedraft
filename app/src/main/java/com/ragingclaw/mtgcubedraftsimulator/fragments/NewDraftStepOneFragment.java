package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;
import com.ragingclaw.mtgcubedraftsimulator.utils.MTGUtils;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewDraftStepOneFragment.OnMyDraftStepOneFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewDraftStepOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewDraftStepOneFragment extends Fragment {
    @BindView(R.id.draft_name_edit_text) TextView draftName;
    @BindView(R.id.btn_generate_draft) com.google.android.material.button.MaterialButton draftButton;
    private Unbinder unbinder;
    int cubeId = -1;

    private OnMyDraftStepOneFragmentInteractionListener mListener;


    public NewDraftStepOneFragment() {
        // Required empty public constructor
    }


    public static NewDraftStepOneFragment newInstance() {
        NewDraftStepOneFragment fragment = new NewDraftStepOneFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cubeId = getActivity().getIntent().getExtras().getInt(AllMyConstants.CUBE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_draft_step_one, container, false);
        unbinder = ButterKnife.bind(this, view);

        // testing. i dont want to type shit. random name.
        draftName.setText(MTGUtils.randomIdentifier());

        draftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(draftName.getText().toString())) {
                    String name = draftName.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString(AllMyConstants.DRAFT_NAME, name);
                    bundle.putInt(AllMyConstants.CUBE_ID, cubeId);
                    Navigation.findNavController(view).navigate(R.id.action_newDraftStepOneFragment_to_newDraftBuildDraftScreen, bundle);
                } else {
                    Toast.makeText(getContext(), "You must give your draft a name to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnMyDraftStepOneFragmentInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyDraftStepOneFragmentInteractionListener) {
            mListener = (OnMyDraftStepOneFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyDraftStepOneFragmentInteractionListener");
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


    public interface OnMyDraftStepOneFragmentInteractionListener {
        void OnMyDraftStepOneFragmentInteractionListener(Uri uri);
    }
}
