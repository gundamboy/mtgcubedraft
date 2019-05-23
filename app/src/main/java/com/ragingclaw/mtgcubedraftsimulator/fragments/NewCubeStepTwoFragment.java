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

import org.jetbrains.annotations.NotNull;

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

        insertCardsIntoDatabase();

        return view;
    }

    private void insertCardsIntoDatabase() {
        /* MagicCard arguments
            MagicCard(@NonNull String id, String layout, String name, String[] names, String manaCost, double cmc, String[] colors, String[] colorIdentity, String type, String[] supertypes, String[] types, String[] subtypes, String rarity, String text, String originalText, String flavor, String artist, String number, String power, String toughness, String loyalty, int multiverseid, String[] variations, String imageName, String watermark, String border, int hand, int life, String releaseDate, String set, String setName, String[] printings, String imageUrl)
         */

        magicCardViewModel = ViewModelProviders.of(getActivity()).get(MagicCardViewModel.class);

        // the JDK / API requires processes to run on a new thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Card> allCards = CardAPI.getAllCards();
                Timber.tag("fart").i("********************************************* allCards size: %s", allCards.size());
                String cheating = "";
                for (Card c : allCards) {
                    String names = "";
                    String colors = "";
                    String colorIdentity = "";
                    String supertypes = "";
                    String types = "";
                    String subtypes = "";
                    String variations = "";
                    String printings = "";

                    String[] array_names = c.getNames();
                    String[] array_colors = c.getColors();
                    String[] array_colorIdentity = c.getColorIdentity();
                    String[] array_supertypes = c.getSupertypes();
                    String[] array_types = c.getTypes();
                    String[] array_subtypes = c.getSubtypes();
                    String[] array_variations = c.getVariations();
                    String[] array_printings = c.getPrintings();

                    if (array_names != null) {
                        for (String s : array_names) {
                            names += s + ",";
                        }
                        if(names.length() > 0) {
                            names = names.substring(0, names.length() - 1);
                        }
                    }

                    if (array_colors != null) {
                        for (String s : array_colors) {
                            colors += s + ",";
                        }
                        if(colors.length() > 0) {
                            colors = colors.substring(0, colors.length() - 1);
                        }
                    }

                    if (array_colorIdentity != null) {
                        for (String s : array_colorIdentity) {
                            colorIdentity += s + ",";
                        }
                        if(colorIdentity.length() > 0) {
                            colorIdentity = colorIdentity.substring(0, colorIdentity.length() - 1);
                        }
                    }

                    if (array_supertypes != null) {
                        for (String s : array_supertypes) {
                            supertypes += s + ",";
                        }
                        if(supertypes.length() > 0) {
                            supertypes = supertypes.substring(0, supertypes.length() - 1);
                        }
                    }

                    if (array_types != null) {
                        for (String s : array_types) {
                            types += s + ",";
                        }
                        if(types.length() > 0) {
                            types = types.substring(0, types.length() - 1);
                        }
                    }

                    if (array_subtypes != null) {
                        for (String s : array_subtypes) {
                            subtypes += s + ",";
                        }
                        if(subtypes.length() > 0) {
                            subtypes = subtypes.substring(0, subtypes.length() - 1);
                        }
                    }

                    if (array_variations != null) {
                        for (String s : array_variations) {
                            variations += s + ",";
                        }
                        if(variations.length() > 0) {
                            variations = variations.substring(0, variations.length() - 1);
                        }
                    }

                    if (array_printings != null) {
                        for (String s : array_printings) {
                            printings += s + ",";
                        }
                        if(printings.length() > 0) {
                            printings = printings.substring(0, printings.length() - 1);
                        }
                    }

                    cheating += "MagicCard card = new MagicCard(\"" + c.getId() + "\", \"" + c.getLayout() + "\", \"" + c.getName()  + "\", \"" + names + "\", \"" + c.getManaCost() + "\", " + c.getCmc() + ", \"" + colors + "\", \"" + colorIdentity + "\", \"" + c.getType() + "\", \"" + supertypes + "\", \"" + types + "\", \"" + subtypes + "\", \"" + c.getRarity() + "\", \"" + c.getText() + "\", \"" + c.getOriginalText() + "\", \"" + c.getFlavor() + "\", \"" + c.getArtist() + "\", \"" + c.getNumber() + "\", \"" + c.getPower() + "\", \"" + c.getToughness() + "\", \"" + c.getLoyalty() + "\", " + c.getMultiverseid() + ", \"" + variations + "\", \"" + c.getImageName() + "\", \"" + c.getWatermark() + "\", \"" + c.getBorder() + "\", " + c.getHand() + ", " + c.getLife() + ", \"" + c.getReleaseDate() + "\", \"" + c.getSet() + "\", \"" + c.getSetName() + "\", \"" + printings + "\", \"" + c.getImageUrl() + "\");";
                    //magicCardViewModel.insertCard(card);
//                    Timber.tag("fart").w("MagicCard card = new MagicCard(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %s, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %s, \"%s\", \"%s\", \"%s\", \"%s\", %s, %s, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");",
//                            c.getId(), c.getLayout(), names, c.getNames(), c.getManaCost(), c.getCmc(),
//                            colors, colorIdentity, c.getType(), supertypes, types,
//                            subtypes, c.getRarity(), c.getText(), c.getOriginalText(), c.getFlavor(), c.getArtist(),
//                            c.getNumber(), c.getPower(), c.getToughness(), c.getLoyalty(), c.getMultiverseid(), variations,
//                            c.getImageName(), c.getWatermark(), c.getBorder(), c.getHand(), c.getLife(), c.getReleaseDate(), c.getSet(),
//                            c.getSetName(), printings, c.getImageUrl());
//                }

                    Timber.tag("fart").w(cheating);
                }

                //LiveData<List<MagicCard>> cards = magicCardViewModel.getmAllCards();
                //Timber.tag("fart").i("cards from DB: %s", cards);

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
