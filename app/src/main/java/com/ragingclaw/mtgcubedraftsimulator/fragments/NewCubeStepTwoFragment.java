package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.MTGUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.meta.When;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewCubeStepTwoFragment.OnFragmentInteractionListenerStepTwo} interface
 * to handle interaction events.
 * Use the {@link NewCubeStepTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCubeStepTwoFragment extends Fragment {
    @BindView (R.id.cube_name) TextView mCubeName;
    private Unbinder unbinder;

    private MagicCardViewModel magicCardViewModel;

    private OnFragmentInteractionListenerStepTwo mListener;
    private List<String> mSetCodes = new ArrayList<>();
    private List<Card> mCubeCards = new ArrayList<>();

    public NewCubeStepTwoFragment() {
        // Required empty public constructor
    }

    public static NewCubeStepTwoFragment newInstance(String param1, String param2) {
        NewCubeStepTwoFragment fragment = new NewCubeStepTwoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_cube_step_two, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mCubeName.setText(getArguments().getString("cubeName"));
        }



        //insertCardsIntoDatabase();
        letsGetDone();

        return view;
    }

    private void letsGetDone() {
        String[] list;
        String output = "";

        try {
            list = getActivity().getAssets().list("");

            if (list.length > 0) {
                for (String file : list) {
                    if (file.contains(".json")) {
                        output += MTGUtils.parseJsonToString(getActivity(), file);
                        if(output.length() > 0) {
                            String filename = file + "-ids.txt";
                            printToFile(output, filename);
                        }

                    }
                }

//                if(output.length() > 0) {
//                    printToFile(output);
//                }
            }

        } catch (IOException e) {
            Timber.tag("fart").w(e);
        }
    }

    private void printToFile(String str, String filename) {

        // the JDK / API requires processes to run on a new thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getContext().getFilesDir(),"mtgcubedraft");

                if(!file.exists()){
                    file.mkdir();
                }

                try {
                    File gpxfile = new File(file, filename);
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(str);
                    writer.flush();
                    writer.close();
                    Timber.tag("fart").w("Done... right?");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getRandomNum(int cubeSize, int max) {
        Random r = new Random();
        return r.nextInt((max - 1) + 1);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendDataToActivity(String string) {
        if (mListener != null) {
            mListener.onFragmentInteractionStepTwo(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerStepTwo) {
            mListener = (OnFragmentInteractionListenerStepTwo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListenerStepTwo");
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

    public interface OnFragmentInteractionListenerStepTwo {
        // TODO: Update argument type and name
        void onFragmentInteractionStepTwo(String string);
    }
}
