package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.LoginActivity;
import com.ragingclaw.mtgcubedraftsimulator.activities.MainActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyCubesAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private FirebaseAuth mAuth;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
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

        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        // view model for db stuff
        cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);

        createCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_myCubesFragment_to_newCubeStepOneFragment);
            }
        });

        // livedata observer for displaying data
        cubeViewModel.getmAllUsersCubes(currentUser).observe(this, new Observer<List<Cube>>() {
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

                            Navigation.findNavController(view).navigate(R.id.action_myCubesFragment_to_cubeCardsReview, bundle, null);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.logout_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            case R.id.logout:
                mAuth.signOut();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    public interface OnMyCubesFragmentInteraction {
        void onMyCubesFragmentInteraction(String string);
    }
}
