package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.activities.LoginActivity;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyCubesAdapter;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    private MyCubesAdapter myCubesAdapter;
    private FirebaseAuth mAuth;
    private LinearLayoutManager linearLayoutManager;
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    private OnMyCubesFragmentInteraction mListener;
    private boolean savePosition = false;
    private int cubeId;
    private String cubeName;
    private Boolean isSaved;
    private Boolean isSingle;
    private  View view;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public MyCubesFragment() {
        // Required empty public constructor
    }

    public static MyCubesFragment newInstance() {

        return new MyCubesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_cubes, container, false);
        unbinder = ButterKnife.bind(this, view);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAuth = FirebaseAuth.getInstance();
        String currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        cubeId = mPreferences.getInt(AllMyConstants.CUBE_ID, 0);
        cubeName = mPreferences.getString(AllMyConstants.CUBE_NAME, null);
        isSaved = mPreferences.getBoolean(AllMyConstants.IS_SAVED, false);
        isSingle = mPreferences.getBoolean(AllMyConstants.IS_SINGLE, false);

        // view model for db stuff
        CubeViewModel cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);

        createCubeButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_myCubesFragment_to_newCubeStepOneFragment));

        // livedata observer for displaying data
        cubeViewModel.getmAllUsersCubes(currentUser).observe(this, cubesEntities -> {
            // update stuff
            if(cubesEntities.size() > 0) {
                savePosition = true;
                my_cubes_layout.setVisibility(View.VISIBLE);
                no_cubes_found_layout.setVisibility(View.GONE);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                cubes_recyclerView.setLayoutManager(linearLayoutManager);
                cubes_recyclerView.setHasFixedSize(true);
                myCubesAdapter = new MyCubesAdapter();
                cubes_recyclerView.setAdapter(myCubesAdapter);
                myCubesAdapter.setCubes(cubesEntities);

                myCubesAdapter.setOnClickListener((theCubeId, theCubeName) -> {
                    cubeName = theCubeName;
                    cubeId = theCubeId;
                    isSaved = false;
                    isSingle = true;

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AllMyConstants.NEW_CUBE, false);
                    bundle.putBoolean(AllMyConstants.CUBE_CARDS, false);
                    bundle.putString(AllMyConstants.CUBE_NAME, cubeName);
                    bundle.putInt(AllMyConstants.CUBE_ID, cubeId);

                    mEditor = mPreferences.edit();
                    mEditor.putInt(AllMyConstants.CUBE_ID, cubeId);
                    mEditor.putString(AllMyConstants.CUBE_NAME, cubeName);
                    mEditor.putString(AllMyConstants.TOAST_MESSAGE, null);
                    mEditor.putBoolean(AllMyConstants.IS_SAVED, isSaved);
                    mEditor.putBoolean(AllMyConstants.IS_SINGLE, isSingle);
                    mEditor.apply();

                    Navigation.findNavController(view).navigate(R.id.action_myCubesFragment_to_cubeCardsReview, bundle, null);
                });

            } else {
                if (my_cubes_layout.getVisibility() == View.VISIBLE) {
                    my_cubes_layout.setVisibility(View.GONE);
                    no_cubes_found_layout.setVisibility(View.VISIBLE);
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
                    + getActivity().getString(R.string.fragment_interaction_error_end_text));
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
        inflater.inflate(R.menu.cube_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(savePosition) {
            mBundleRecyclerViewState = new Bundle();
            mListState = Objects.requireNonNull(cubes_recyclerView.getLayoutManager()).onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(AllMyConstants.RECYCLER_RESTORE, mListState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savePosition) {
            if (mBundleRecyclerViewState != null) {
                new Handler().postDelayed(() -> {
                    mListState = mBundleRecyclerViewState.getParcelable(AllMyConstants.RECYCLER_RESTORE);
                    Objects.requireNonNull(cubes_recyclerView.getLayoutManager()).onRestoreInstanceState(mListState);

                }, 50);
            }
        }
    }


    public interface OnMyCubesFragmentInteraction {
        void onMyCubesFragmentInteraction(String string);
    }
}
