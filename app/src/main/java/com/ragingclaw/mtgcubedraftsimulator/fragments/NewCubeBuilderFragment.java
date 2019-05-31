package com.ragingclaw.mtgcubedraftsimulator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;
import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.magicthegathering.javasdk.resource.Card;

public class NewCubeBuilderFragment extends Fragment {
    @BindView(R.id.percentage_built) TextView completePercent;
    private Unbinder unbinder;
    private Thread t;
    private Handler handler;
    private Bundle handlerBundle = new Bundle();
    private MagicCardViewModel magicCardViewModel;
    private OnFragmentInteractionListenerStepTwo mListener;
    private String cubeName;

    public NewCubeBuilderFragment() {
        // Required empty public constructor
    }

    public static NewCubeBuilderFragment newInstance(String param1, String param2) {
        NewCubeBuilderFragment fragment = new NewCubeBuilderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creating_cube_or_draft_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            if(mListener != null) {
                cubeName = getArguments().getString(AllMyConstants.CUBE_NAME);
                mListener.onFragmentInteractionStepTwo(cubeName);
            }
        }

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);
        magicCardViewModel.getmAllCards().observe(this, new Observer<List<MagicCard>>() {
            // make a list of the ids to send off for draft building
            List<Integer> ids = new ArrayList<>();
            List<MagicCard> cards = new ArrayList<>();

            @Override
            public void onChanged(List<MagicCard> magicCards) {
                try {
                    for (MagicCard c : magicCards) {
                        ids.add(c.getMultiverseid());
                        cards.add(c);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(ids.size() > 0) {
                    // there are ids, WOOHOO! lets build a cube!
                    buildCube(cards, view);
                }
            }
        });
        return view;
    }

    private void buildCube(List<MagicCard> cards, View view) {
        // separate threads cannot communicate with the UI thread directly. This lets them communicate.
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                handlerBundle = msg.getData();
                String percent = handlerBundle.getString("percent") + "%";
                completePercent.setText(percent);
            }
        };

        t = new Thread(new BuildCube(handler, cards, view));
        t.start();

    }

    private int getRandomNum(int max) {
        Random r = new Random();
        return r.nextInt((max - 1) + 1);
    }

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

        void onFragmentInteractionStepTwo(String string);
    }

    public class BuildCube implements Runnable {
        Handler handler;
        List<MagicCard> cards;
        View view;

        public BuildCube(Handler handler, List<MagicCard> cards, View view) {
            this.handler = handler;
            this.cards = cards;
            this.view = view;
        }

        @Override
        public void run() {
            List<MagicCard> allCards = cards;
            List<MagicCard> cubeCards = new ArrayList<>();

            // a cube is 360 cards.
            int cubeSize = 360;

            for (int i = 0; i < cubeSize; i++) {
                int r = getRandomNum(allCards.size());
                cubeCards.add(allCards.get(r));
                allCards.remove(r);

                // uhg, math.
                String percent = String.valueOf((i * 100) / cubeSize);

                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString("percent", percent);
                m.setData(b);
                handler.sendMessage(m);

            }

            if (cubeCards.size() == cubeSize) {
                Message m = Message.obtain();
                Bundle b = new Bundle();

                b.putString("percent", "100");
                m.setData(b);
                handler.sendMessage(m);

                Bundle bundle = new Bundle();
                bundle.putBoolean(AllMyConstants.NEW_CUBE, true);
                bundle.putParcelable(AllMyConstants.CUBE_CARDS, Parcels.wrap(cubeCards));
                bundle.putString(AllMyConstants.CUBE_NAME, cubeName);

                NavOptions.Builder navBuilder = new NavOptions.Builder();
                NavOptions navOptions = navBuilder.setPopUpTo(R.id.newCubeStepOneFragment, true).build();
                Navigation.findNavController(view).navigate(R.id.action_newCubeBuilderFragment_to_cubeCardsReview, bundle, navOptions);
            }
        }
    }
}
