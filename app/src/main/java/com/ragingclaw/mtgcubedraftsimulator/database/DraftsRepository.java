package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Update;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DraftsRepository {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DraftsDao mDraftsDao;
    private LiveData<ArrayList<DraftsEntity>> mAllDrafts;
    private LiveData<ArrayList<DraftsEntity>> mUserDraftsAndPacks;

    DraftsRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mDraftsDao = db.draftsDao();
        mAllDrafts = mDraftsDao.getAllDrafts();
        mUserDraftsAndPacks = mDraftsDao.getUserDrafts(currentUser.getUid());
    }

    public void insertDraft(DraftsEntity draftsEntity) {
        new InsertDraftAsyncTask(mDraftsDao).execute(draftsEntity);
    }

    public void updateDraft(DraftsEntity draftsEntity) {
        new UpdateDraftAsyncTask(mDraftsDao).execute(draftsEntity);
    }

    public void deleteAllDrafts(DraftsEntity draftsEntity) {
        new DeleteAllDraftsAsyncTask(mDraftsDao).execute();
    }

    public void deleteDraft(Integer draftID) {
        new DeleteDraftAsyncTask(mDraftsDao).execute(draftID);
    }

    public LiveData<ArrayList<DraftsEntity>> getAllDrafts() {
        return mUserDraftsAndPacks;
    }

    public LiveData<ArrayList<DraftsEntity>> getUserDrafts() {
        return mAllDrafts;
    }



    private static class InsertDraftAsyncTask extends AsyncTask<DraftsEntity, Void, Void> {
        DraftsDao draftsDao;

        private InsertDraftAsyncTask(DraftsDao draftsDao) {
            this.draftsDao = draftsDao;
        }

        @Override
        protected Void doInBackground(DraftsEntity... draftsEntities) {
            draftsDao.insertDraft(draftsEntities[0]);
            return null;
        }
    }

    private static class UpdateDraftAsyncTask extends AsyncTask<DraftsEntity, Void, Void> {
        DraftsDao draftsDao;

        private UpdateDraftAsyncTask(DraftsDao draftsDao) {
            this.draftsDao = draftsDao;
        }

        @Override
        protected Void doInBackground(DraftsEntity... draftsEntities) {
            draftsDao.updateDraft(draftsEntities[0]);
            return null;
        }
    }

    private static class DeleteAllDraftsAsyncTask extends AsyncTask<Void, Void, Void> {
        DraftsDao draftsDao;

        private DeleteAllDraftsAsyncTask(DraftsDao draftsDao) {
            this.draftsDao = draftsDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            draftsDao.deleteAllDrafts();
            return null;
        }
    }

    private static class DeleteDraftAsyncTask extends AsyncTask<Integer, Void, Void> {
        DraftsDao draftsDao;

        private DeleteDraftAsyncTask(DraftsDao draftsDao) {
            this.draftsDao = draftsDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            draftsDao.deleteDraft(integers[0]);
            return null;
        }
    }
}
