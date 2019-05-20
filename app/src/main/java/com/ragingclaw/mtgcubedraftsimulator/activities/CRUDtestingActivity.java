package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.database.Draft;
import com.ragingclaw.mtgcubedraftsimulator.database.Pack;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.DraftViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.PackViewModel;
import com.ragingclaw.mtgcubedraftsimulator.models.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CRUDtestingActivity extends AppCompatActivity {
    @BindView(R.id.btn_add_cube) com.google.android.material.button.MaterialButton btn_addCube;
    @BindView(R.id.btn_delete_cube) com.google.android.material.button.MaterialButton btn_deleteCube;
    @BindView(R.id.btn_add_draft) com.google.android.material.button.MaterialButton btn_addDraft;
    @BindView(R.id.btn_delete_draft) com.google.android.material.button.MaterialButton btn_DeleteDraft;
    @BindView(R.id.output) TextView tv_output;
    @BindView(R.id.output_single) TextView tv_output_single;
    @BindView(R.id.output_draft_info) TextView tv_output_drafts_info;
    @BindView(R.id.output_single_draft) TextView tv_output_single_draft;

    // view models
    private CubeViewModel cubeViewModel;
    private UserViewModel userViewModel;
    private DraftViewModel draftViewModel;
    private PackViewModel packViewModel;

    // dummy data stuff
    private String cube_observe_info;
    private String single_cube_observer;
    private String all_drafts_observer;
    private String single_draft_observer;
    private String all_packs_observer;
    private String single_packs_observer;


    private int cubeId;
    private int draftId;
    private List<Integer> cube1Cards;
    private List<Integer> booster1;
    private List<Integer> booster2;
    private List<Integer> booster3;
    private List<Integer> draft_booster_choices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_db_test_layout);
        ButterKnife.bind(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String logged_in_userID = firebaseUser.getUid();


        all_drafts_observer = "DRAFTS INFO:\n ";
        single_draft_observer = "SINGLE DRAFTS INFO:\n ";
        all_packs_observer = "All DRAFTS INFO:\n ";
        single_packs_observer = "SINGLE PACKS INFO:\n ";
        tv_output.setText(cube_observe_info);
        tv_output_single.setText(single_cube_observer);
        tv_output_drafts_info.setText(all_drafts_observer);
        tv_output_single_draft.setText(single_draft_observer);



        /** OPERATIONS: CRUD **/
        // dummy data
        cubeId = 1;
        draftId = 1;
        cube1Cards = fauxCardIdList(360);
        booster1 = fauxCardIdList(15);
        booster2 = fauxCardIdList(15);
        booster3 = fauxCardIdList(15);
        draft_booster_choices = fauxCardIdList(45);
        Cube cube1 = new Cube(cubeId, logged_in_userID, "First Cube", cube1Cards.size(), cube1Cards);
        Draft draft1 = new Draft(1, 1, 8, draft_booster_choices, draft_booster_choices);
        Pack pack1 = new Pack(1, 1, 1, 1, 1, booster1);
        Pack pack2 = new Pack(2, 1, 1, 2, 1, booster2);
        Pack pack3 = new Pack(3, 1, 1, 3, 1, booster3);



        // get the cubes view model
        cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        draftViewModel = ViewModelProviders.of(this).get(DraftViewModel.class);
        packViewModel = ViewModelProviders.of(this).get(PackViewModel.class);



        // observers
        cubeViewModel.getmAllCubes().observe(this, new Observer<List<Cube>>() {
            @Override
            public void onChanged(List<Cube> cubesEntities) {
                // update stuff
                cube_observe_info = "CUBES INFO:\n ";
                try {
                    for (Cube c : cubesEntities) {
                        cube_observe_info += c.getCubeId() + ": " + c.getUserId() + ": " + c.getCube_name() + ", \n";
                    }
                } catch(Exception e) {
                    cube_observe_info = e.getMessage();
                }

                tv_output.setText(cube_observe_info);
            }
        });

        cubeViewModel.getmUserCube(logged_in_userID, cubeId).observe(this, new Observer <Cube>() {
            @Override
            public void onChanged(Cube cubesEntities) {
                single_cube_observer = "SINGLE CUBE INFO:\n ";
                // update stuff
                try {
                    single_cube_observer += cubesEntities.getCubeId() + ": " + cubesEntities.getUserId() + ": " + cubesEntities.getCube_name() + ", \n";
                    tv_output_single.setText(single_cube_observer);
                } catch(Exception e) {
                    single_cube_observer = e.getMessage();
                }
            }
        });

        draftViewModel.getAllDrafts().observe(this, new Observer<List<Draft>>() {
            @Override
            public void onChanged(List<Draft> draftEntries) {
                // update stuff
                all_drafts_observer = "DRAFTS INFO:\n ";
                try {
                    for (Draft c : draftEntries) {
                        all_drafts_observer += "draft id: " + c.getDraftID() + "\n";
                    }
                } catch(Exception e) {
                    all_drafts_observer = e.getMessage();
                }

                tv_output_drafts_info.setText(all_drafts_observer);
            }
        });

        draftViewModel.getSingleDraft(1).observe(this, new Observer <Draft>() {
            @Override
            public void onChanged(Draft draft) {
                // update stuff
                single_draft_observer = "SINGLE DRAFTS INFO:\n ";
                try {
                    single_draft_observer += "draft id: " + draft.getDraftID() + "\n";
                    single_draft_observer += "cube id: " + draft.getCubeId() + "\n";
                    single_draft_observer += "booster choices: " + draft.getBooster_choices() + "\n";
                } catch(Exception e) {
                    single_draft_observer = e.getMessage();
                }

                tv_output_single_draft.setText(single_draft_observer);
            }
        });



        btn_addCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert a cube
                try {
                    cubeViewModel.insertCube(cube1);
                } catch(Exception e) {
                    cube_observe_info = e.getMessage();
                }
            }
        });

        btn_deleteCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete a cube
                try {
                    cubeViewModel.deleteCube(cube1);
                } catch(Exception e) {
                    cube_observe_info = e.getMessage();
                }
            }
        });

        btn_addDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what happens?

                // get the cardIds from the current current cube. in the full app flow this will
                // be a chosen cube, but just use the cubeId for now.

                // To simplify this, just make 3 packs of 15. Screw the other players.
                // randomly pick a card from the ids, add it to booster1, remove it from the full list
                // randomly pick a card from the ids, add it to booster2, remove it from the full list
                // randomly pick a card from the ids, add it to booster3, remove it from the full list
                // repeat this until there are no cardIds in the full list.

                // TESTING
                // use preset boosters1, 2, and 3
                try {
                    draftViewModel.insertDraft(draft1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // insert each booster into the packs table
                // drafts observable should show some info
                // packs observable should show some info

                try {
                    packViewModel.insertPack(pack1);
                    packViewModel.insertPack(pack2);
                    packViewModel.insertPack(pack3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_DeleteDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    draftViewModel.deleteDraft(draft1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private List<Integer> fauxCardIdList(int count) {
        List<Integer> cardIds = new ArrayList<Integer>();

        for(int i = 0; i < count; i++) {
            Random r = new Random();
            cardIds.add(r.nextInt((1000 - 1) + 1));
        }

        return cardIds;
    }

}
