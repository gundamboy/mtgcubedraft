package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.NewCubeActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyCubesAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCubesFragment.OnMyCubesFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link MyCubesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCubesFragment extends Fragment {
    @BindView(R.id.no_cubes_found) androidx.constraintlayout.widget.ConstraintLayout no_cubes_found_layout;
    @BindView(R.id.my_cubes_layout) androidx.constraintlayout.widget.ConstraintLayout my_cubes_layout;
    @BindView(R.id.cubes_recyclerview) RecyclerView cubes_recyclerView;
    @BindView(R.id.create_cube_button) com.google.android.material.button.MaterialButton createCubeButton;
    private Unbinder unbinder;
    private CubeViewModel cubeViewModel;
    private MyCubesAdapter myCubesAdapter;

    private OnMyCubesFragmentInteraction mListener;

    public MyCubesFragment() {
        // Required empty public constructor
    }


    public static MyCubesFragment newInstance() {
        MyCubesFragment fragment = new MyCubesFragment();

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
        View view = inflater.inflate(R.layout.fragment_my_cubes, container, false);
        unbinder = ButterKnife.bind(this, view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);

        createCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewCubeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        cubeViewModel.getmAllUsersCubes(currentUser.getUid()).observe(this, new Observer<List<Cube>>() {
            @Override
            public void onChanged(List<Cube> cubesEntities) {
                // update stuff

                if(cubesEntities.size() > 0) {
                    my_cubes_layout.setVisibility(View.VISIBLE);
                    no_cubes_found_layout.setVisibility(View.GONE);

                    cubes_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    cubes_recyclerView.setHasFixedSize(true);
                    myCubesAdapter = new MyCubesAdapter();
                    cubes_recyclerView.setAdapter(myCubesAdapter);
                    myCubesAdapter.setCubes(cubesEntities);

                    myCubesAdapter.setOnClickListener(new MyCubesAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, int cubeId, String cubeName) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(AllMyConstants.NEW_CUBE, false);
                            bundle.putBoolean(AllMyConstants.CUBE_CARDS, false);
                            bundle.putString(AllMyConstants.CUBE_NAME, cubeName);
                            bundle.putInt(AllMyConstants.CUBE_ID, cubeId);

                            Navigation.findNavController(view).navigate(R.id.action_myCubesFragment_to_fragmentCubeReview, bundle, null);
                        }
                    });

                } else {
                    if (my_cubes_layout.getVisibility() == View.VISIBLE) {
                        my_cubes_layout.setVisibility(View.GONE);
                        no_cubes_found_layout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onMyCubesFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyCubesFragmentInteraction) {
            mListener = (OnMyCubesFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyCubesFragmentInteraction");
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


    public interface OnMyCubesFragmentInteraction {
        // TODO: Update argument type and name
        void onMyCubesFragmentInteraction(String string);
    }
}
