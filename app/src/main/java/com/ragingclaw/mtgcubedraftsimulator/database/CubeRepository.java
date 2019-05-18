package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CubeRepository {

    private CubeDao mCubesDoa;

    public CubeRepository(Application application) {
        ApplicationDatabase db = ApplicationDatabase.getDatabase(application);
        mCubesDoa = db.cubesDao();
    }

    public void insertCube(Cube cube) {
        new InsertCubeAsyncTask(mCubesDoa).execute(cube);
    }

    public void updateCube(Cube cube) {
        new UpdateCubeAsyncTask(mCubesDoa).execute(cube);
    }

    public void deleteAllCubes() {
        new DeleteAllCubesAsyncTask(mCubesDoa).execute();
    }

    public void deleteCube(Cube cube) {
        new DeleteCubeAsyncTask(mCubesDoa).execute(cube);
    }

    public LiveData<List<Cube>> mGetAllCubes() {
        return mCubesDoa.getAllCubes();
    }

    public LiveData<List<Cube>> getUserCubes(String userId) {
        return mCubesDoa.getUserCubes(userId);
    }

    public LiveData<Cube> getUserCube(String userId, Integer cubeId) {
        return mCubesDoa.getUserCube(userId, cubeId);
    }

    // async tasks
    private static class InsertCubeAsyncTask extends android.os.AsyncTask<Cube, Void, Void> {
        private CubeDao cubeDao;

        private InsertCubeAsyncTask(CubeDao cubeDao) {
            this.cubeDao = cubeDao;
        }

        @Override
        protected Void doInBackground(Cube... cubesEntities) {
            cubeDao.insertCube(cubesEntities[0]);
            return null;
        }
    }

    private static class UpdateCubeAsyncTask extends android.os.AsyncTask<Cube, Void, Void> {
        private CubeDao cubeDao;

        private UpdateCubeAsyncTask(CubeDao cubeDao) {
            this.cubeDao = cubeDao;
        }

        @Override
        protected Void doInBackground(Cube... cubesEntities) {
            cubeDao.updateCube(cubesEntities[0]);
            return null;
        }
    }

    private static class DeleteCubeAsyncTask extends android.os.AsyncTask<Cube, Void, Void> {

        private CubeDao cubeDao;

        private DeleteCubeAsyncTask(CubeDao cubeDao) {
            this.cubeDao = cubeDao;
        }

        @Override
        protected Void doInBackground(Cube... cubesEntities) {
            cubeDao.deleteCube(cubesEntities[0]);
            return null;
        }
    }

    private static class DeleteAllCubesAsyncTask extends AsyncTask<Void, Void, Void> {

        private CubeDao cubeDao;

        private DeleteAllCubesAsyncTask(CubeDao cubeDao) {
            this.cubeDao = cubeDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cubeDao.deleteAllCubes();
            return null;
        }
    }
}
