package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PacksRepository {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int mMultiverseId;

    private PacksDao mPacksDao;
    private LiveData<List<PacksEntity>> mAllPacks;
    private LiveData<List<PacksEntity>> mUserPacks;

    PacksRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // TODO: get multiverse ID

        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mPacksDao = db.packsDao();
        mAllPacks = mPacksDao.getAllPacks();
        mUserPacks = mPacksDao.getUserPacks(currentUser.getUid());
    }
}
