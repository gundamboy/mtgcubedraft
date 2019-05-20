package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PackRepository {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int mMultiverseId;

    private PackDao mPackDao;
    private LiveData<List<Pack>> mAllPacks;
    private LiveData<List<Pack>> mUserPacks;

    PackRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // TODO: get multiverse ID

        ApplicationDatabase db = ApplicationDatabase.getDatabase(application);
        mPackDao = db.packDoa();
        mAllPacks = mPackDao.getAllPacks();
        mUserPacks = mPackDao.getUserPacks(currentUser.getUid());
    }
}
