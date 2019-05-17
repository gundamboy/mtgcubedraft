package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CubesRepository {

    private FirebaseAuth mAuth;
    private FirebaseUser userID;
    private CubesDao mCubesDoa;
    private LiveData<ArrayList<CubesEntity>> mAllCubes;
    private LiveData<ArrayList<CubesEntity>> mUserCubes;
    private LiveData<ArrayList<CubesEntity>> mUserCubesAndDrafts;

    CubesRepository(Application application) {
        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser();
        mCubesDoa = db.cubesDao();
        mAllCubes = mCubesDoa.getAllCubes();
        mUserCubes = mCubesDoa.getUserCubes(userID);
        mUserCubesAndDrafts = mCubesDoa.getAllUserCubeInfo(userID);
    }

    public void insertCube(CubesEntity cubesEntity) {
        new InsertCubeAsyncTask(mCubesDoa).execute(cubesEntity);
    }

    public void updateCube(CubesEntity cubesEntity) {
        new UpdateCubeAsyncTask(mCubesDoa).execute(cubesEntity);
    }

    public void deleteAllCubes(CubesEntity cubesEntity) {
        new DeleteAllCubesAsyncTask(mCubesDoa).execute();
    }

    public void deleteCube(Integer cubeId) {
        new DeleteCubeAsyncTask(mCubesDoa).execute(cubeId);
    }

    public LiveData<ArrayList<CubesEntity>> getmAllCubes() {
        return mAllCubes;
    }

    public LiveData<ArrayList<CubesEntity>> getmUserCubes() {
        return mUserCubes;
    }

    public LiveData<ArrayList<CubesEntity>> getmUserCubesAndDrafts() {
        return mUserCubesAndDrafts;
    }

    // async tasks
    private static class InsertCubeAsyncTask extends AsyncTask<CubesEntity, Void, Void> {
        private CubesDao cubesDao;

        private InsertCubeAsyncTask(CubesDao cubesDao) {
            this.cubesDao = cubesDao;
        }

        @Override
        protected Void doInBackground(CubesEntity... cubesEntities) {
            cubesDao.insertCube(cubesEntities[0]);
            return null;
        }
    }

    private static class UpdateCubeAsyncTask extends AsyncTask<CubesEntity, Void, Void> {
        private CubesDao cubesDao;

        private UpdateCubeAsyncTask(CubesDao cubesDao) {
            this.cubesDao = cubesDao;
        }

        @Override
        protected Void doInBackground(CubesEntity... cubesEntities) {
            cubesDao.updateCube(cubesEntities[0]);
            return null;
        }
    }

    private static class DeleteCubeAsyncTask extends AsyncTask<Integer, Void, Void> {

        private CubesDao cubesDao;

        private DeleteCubeAsyncTask(CubesDao cubesDao) {
            this.cubesDao = cubesDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int currentInt = integers[0];
            cubesDao.deleteCube(currentInt);
            return null;
        }
    }

    private static class DeleteAllCubesAsyncTask extends AsyncTask<Void, Void, Void> {

        private CubesDao cubesDao;

        private DeleteAllCubesAsyncTask(CubesDao cubesDao) {
            this.cubesDao = cubesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cubesDao.deleteAllCubes();
            return null;
        }
    }
}
