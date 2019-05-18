package com.ragingclaw.mtgcubedraftsimulator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.R;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;
import com.ragingclaw.mtgcubedraftsimulator.models.CubeViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCubesActivity extends AppCompatActivity {
    @BindView(R.id.btn_add_cube) com.google.android.material.button.MaterialButton btn_addCube;
    @BindView(R.id.btn_update_cube) com.google.android.material.button.MaterialButton btn_updateCube;
    @BindView(R.id.btn_delete_cube) com.google.android.material.button.MaterialButton btn_deleteCube;
    @BindView(R.id.btn_delete_all_cubes) com.google.android.material.button.MaterialButton btn_deleteAllCubes;
    @BindView(R.id.btn_get_all_cubes) com.google.android.material.button.MaterialButton btn_getAllCubes;
    @BindView(R.id.btn_get_all_users_cubes) com.google.android.material.button.MaterialButton btn_getAllUsersCubes;
    @BindView(R.id.btn_get_single_cube) com.google.android.material.button.MaterialButton btn_getSingleCube;
    @BindView(R.id.output) TextView tv_output;
    @BindView(R.id.output_single) TextView tv_output_single;

    private CubeViewModel cubeViewModel;
    private String names;
    private String names_singleUser;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_cubes);
        setContentView(R.layout.room_db_test_layout);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        user = firebaseUser.getUid();

        names = "ALL USER CUBES:\n ";
        names_singleUser = "SINGLE USER CUBES:\n ";
        tv_output.setText(names);
        tv_output_single.setText(names_singleUser);


        /*
        OPERATIONS:
        Create - works
        Read: All - works
        Read: Single User - works
        Update: Single - works
        Update: Single User - works
        Delete: Single - Works
         */

        // dummy data
        List<Integer> cardIds = new ArrayList<Integer>();
        cardIds.add(335);
        cardIds.add(324);
        cardIds.add(658);
        cardIds.add(125);
        cardIds.add(3425);

        Cube cube1 = new Cube(1, "KLNBh4t-------",
                "First Cube", 640, cardIds);

        Cube cube2 = new Cube(2, "KLNBh4t53dPVdC6",
                "Second Cube", 360, cardIds);

        Cube cube3 = new Cube(3, user,
                "Third Cube", 360, cardIds);

        Cube cube4 = new Cube(4, user,
                "Fourth Cube", 360, cardIds);




        // get the cubes view model
        cubeViewModel = ViewModelProviders.of(this).get(CubeViewModel.class);

        // observers
        cubeViewModel.getmAllCubes().observe(this, new Observer<List<Cube>>() {
            @Override
            public void onChanged(List<Cube> cubesEntities) {
                // update stuff
                try {
                    for (Cube c : cubesEntities) {
                        names += c.getCubeId() + ": " + c.getUserId() + ": " + c.getCube_name() + ", \n";
                    }
                } catch(Exception e) {
                    names = e.getMessage();
                }

                tv_output.setText(names);
            }
        });


        cubeViewModel.getmUserCube(user, 4).observe(this, new Observer <Cube>() {
            @Override
            public void onChanged(Cube cubesEntities) {
                // update stuff
                try {
                    names_singleUser += cubesEntities.getCubeId() + ": " + cubesEntities.getUserId() + ": " + cubesEntities.getCube_name() + ", \n";
                    tv_output_single.setText(names_singleUser);
                } catch(Exception e) {
                    names_singleUser = e.getMessage();
                }
            }
        });





        // button events
        btn_addCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert a cube
                try {
                    cubeViewModel.insertCube(cube3);
                } catch(Exception e) {
                    names = e.getMessage();
                }
            }
        });

        btn_updateCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert a cube
                cubeViewModel.updateCube(cube3);
            }
        });

        btn_deleteCube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete a cube
                cubeViewModel.deleteCube(cube1);
            }
        });

        btn_getAllUsersCubes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                names = "";
                cubeViewModel.getmAllUsersCubes(user.toString());
            }
        });

        btn_deleteAllCubes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert a cube
                cubeViewModel.deleteAllCubes();
            }
        });


    }
}
