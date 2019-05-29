package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyDraftsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.DraftViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyDraftsFragment.OnMyDraftsInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyDraftsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDraftsFragment extends Fragment {
    @BindView(R.id.no_drafts_found) androidx.constraintlayout.widget.ConstraintLayout no_drafts_found_layout;
    @BindView(R.id.my_drafts_layout) androidx.constraintlayout.widget.ConstraintLayout my_drafts_layout;
    @BindView(R.id.drafts_recyclerview) RecyclerView drafts_recyclerview;

    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DraftViewModel draftViewModel;
    private MyDraftsAdapter myDraftsAdapter;

    private OnMyDraftsInteractionListener mListener;

    public MyDraftsFragment() {
        // Required empty public constructor
    }

    public static MyDraftsFragment newInstance(String param1, String param2) {
        MyDraftsFragment fragment = new MyDraftsFragment();

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
        View view = inflater.inflate(R.layout.fragment_my_drafts, container, false);

        unbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        draftViewModel = ViewModelProviders.of(this).get(DraftViewModel.class);

        draftViewModel.getAllDrafts().observe(this, new Observer<List<Draft>>() {
            @Override
            public void onChanged(List<Draft> drafts) {
                if(drafts.size() > 0) {
                    my_drafts_layout.setVisibility(View.VISIBLE);
                    no_drafts_found_layout.setVisibility(View.GONE);

                    myDraftsAdapter = new MyDraftsAdapter();
                    drafts_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    drafts_recyclerview.setHasFixedSize(true);
                    myDraftsAdapter.setDrafts(drafts);

                    myDraftsAdapter.setOnClickListener(new MyDraftsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, int draftId, String draftName, int cubeId) {

                        }
                    });

                } else {
                    if (my_drafts_layout.getVisibility() == View.VISIBLE) {
                        my_drafts_layout.setVisibility(View.GONE);
                        no_drafts_found_layout.setVisibility(View.VISIBLE);
                    }
                }
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
        if (context instanceof OnMyDraftsInteractionListener) {
            mListener = (OnMyDraftsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyDraftsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMyDraftsInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
