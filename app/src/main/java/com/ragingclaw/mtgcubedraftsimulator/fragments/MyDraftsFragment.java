package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.adapters.MyDraftsAdapter;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.DraftViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyDraftsFragment.OnMyDraftsInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyDraftsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDraftsFragment extends Fragment {
    @BindView(R.id.no_drafts_found) androidx.constraintlayout.widget.ConstraintLayout noDraftsFoundLayout;
    @BindView(R.id.my_drafts_layout) androidx.constraintlayout.widget.ConstraintLayout myDraftsLayout;
    @BindView(R.id.no_drafts_monster_text) TextView noDraftsMonsterText;
    @BindView(R.id.no_drafts_info) TextView noDraftsInfo;
    @BindView(R.id.go_to_cubes_button) com.google.android.material.button.MaterialButton goToCubesButton;
    @BindView(R.id.drafts_recyclerview) RecyclerView draftsRecyclerView;

    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DraftViewModel draftViewModel;
    private CubeViewModel cubeViewModel;
    private MyDraftsAdapter myDraftsAdapter;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();

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
        cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);

        cubeViewModel.getmAllUsersCubes(currentUserId).observe(this, new Observer<List<Cube>>() {
            @Override
            public void onChanged(List<Cube> cubes) {
                if(cubes.size() > 0) {
                    draftViewModel.getUserDrafts(currentUserId).observe(getActivity(), new Observer<List<Draft>>() {
                        @Override
                        public void onChanged(List<Draft> drafts) {
                            Timber.tag("fart").i("drafts size: %s", drafts.size());
                            if(drafts.size() > 0) {
                                myDraftsLayout.setVisibility(View.VISIBLE);
                                noDraftsFoundLayout.setVisibility(View.GONE);

                                myDraftsAdapter = new MyDraftsAdapter();
                                draftsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                draftsRecyclerView.setHasFixedSize(true);
                                myDraftsAdapter.setDrafts(drafts);

                                myDraftsAdapter.setOnClickListener(new MyDraftsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position, int draftId, String draftName, int cubeId) {

                                    }
                                });
                            } else {
                                Timber.tag("fart").i("there are no drafts, but there is a cube");
                                noDraftsInfo.setText(getResources().getString(R.string.my_drafts_no_drafts_instructions));
                                noDraftsMonsterText.setText(getResources().getString(R.string.my_drafts_monster_text));
                                myDraftsLayout.setVisibility(View.GONE);
                                noDraftsFoundLayout.setVisibility(View.VISIBLE);

                                goToCubesButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Navigation.findNavController(view).navigate(R.id.action_myDraftsFragment_to_myCubesFragment);

                                    }
                                });
                            }
                        }
                    });
                } else {
                    noDraftsInfo.setText(getResources().getString(R.string.my_drafts_no_cubes_instructions));
                    goToCubesButton.setText(getResources().getString(R.string.no_drafts_button_make_a_cube));
                    myDraftsLayout.setVisibility(View.GONE);
                    noDraftsFoundLayout.setVisibility(View.VISIBLE);

                    goToCubesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(getActivity(), NewCubeActivity.class);
//                            startActivity(intent);
                        }
                    });
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

    public void checkCubes() {
        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();

            }
        };

        t = new Thread(new CheckCubes(handler, currentUserId, cubeViewModel));
        t.start();
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

        void OnMyDraftStepOneFragmentInteractionListener(Uri uri);
    }

    public class CheckCubes implements Runnable {
        Handler handler;
        CubeViewModel cubeViewModel;
        String userId;
        boolean hasCubes = false;

        public CheckCubes(Handler handler, String userID, CubeViewModel cubeViewModel) {
            this.handler = handler;
            this.userId = userID;
            this.cubeViewModel = cubeViewModel;
        }

        @Override
        public void run() {
            LiveData<List<Cube>> userCubes = cubeViewModel.getmAllUsersCubes(userId);
            Timber.tag("fart").i("userCubes: %s", userCubes);
            Timber.tag("fart").i("userId: %s", userId);
            if (userCubes != null) {
                hasCubes = true;
                
                Timber.tag("fart").i("usercubes not null");

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putBoolean("hasCubes", hasCubes);
                m.setData(b);
                handler.sendMessage(m);
            } else {
                Timber.tag("fart").i("userCubes is null");
            }
        }
    }
}
