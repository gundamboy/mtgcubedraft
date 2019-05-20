package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {

    private CubeDao mCubesDoa;
    private UserDao mUserDao;
    private DraftDao mDraftDao;
    private PackDao mPackDao;

    public ApplicationRepository(Application application) {
        ApplicationDatabase db = ApplicationDatabase.getDatabase(application);
        mCubesDoa = db.cubesDao();
        mUserDao = db.userDao();
        mDraftDao = db.draftDoa();
        mPackDao = db.packDoa();
    }


    // CUBE
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





    // USER
    public LiveData<List<User>> getAllUsers() {
        return mUserDao.getAllUsers();
    }

    public User getUserId(String email, String password) {
        return mUserDao.getUserId(email, password);
    }

    public void insertUser(User user) {
        new InsertUserAsyncTask(mUserDao).execute(user);
    }

    public void deletetUser(User user) {
        new DeleteUserAsyncTask(mUserDao).execute(user);
    }

    public void updateUser(User user) {
        new UpdateUserAsyncTask(mUserDao).execute(user);
    }

    public void deleteAllUsers() {
        new DeleteAllUserAsyncTask(mUserDao).execute();
    }

    public void deleteUser(User user) {
        new DeleteUserAsyncTask(mUserDao).execute(user);
    }




    // DRAFT
    public void insertDraft(Draft draft) {
        new InsertDraftAsyncTask(mDraftDao).execute(draft);
    }

    public void updateDraft(Draft draft) {
        new UpdateDraftAsyncTask(mDraftDao).execute(draft);
    }

    public void deleteAllDrafts() {
        new DeleteAllDraftsAsyncTask(mDraftDao).execute();
    }

    public void deleteDraft(Draft draft) {
        new DeleteDraftAsyncTask(mDraftDao).execute(draft);
    }

    public LiveData<List<Draft>> getAllDrafts() {
        return mDraftDao.getAllDrafts();
    }

    public LiveData<Draft> getSingleDraft(int draftID) {
        return mDraftDao.getSingleDraft(draftID);
    }

    public LiveData<List<Draft>> getUserDrafts(String userId) {
        return mDraftDao.getUserDrafts(userId);
    }




    // Packs
    public void insertPack(Pack pack) {
        new InsertPackAsyncTask(mPackDao).execute(pack);
    }

    public void updatePack(Pack pack) {
        new UpdatePackAsyncTask(mPackDao).execute(pack);
    }

    public void deletePack(Pack pack) {
        new DeletePackAsyncTask(mPackDao).execute(pack);
    }

    public void deleteAllPacks() {
        new DeleteAllPacksAsyncTask(mPackDao).execute();
    }

    public LiveData<List<Pack>> getAllPacks() {
        return mPackDao.getAllPacks();
    }





    // Cube AsyncTasks
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


    // User AsyncTasks
    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... usersEntities) {
            userDao.insertUser(usersEntities[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... usersEntities) {
            userDao.updateUser(usersEntities[0]);
            return null;
        }
    }

    private static class DeleteAllUserAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;

        public DeleteAllUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.deleteUser(users[0]);
            return null;
        }
    }


    // Draft AsyncTasks
    private static class InsertDraftAsyncTask extends AsyncTask<Draft, Void, Void> {
        DraftDao draftDao;

        private InsertDraftAsyncTask(DraftDao draftDao) {
            this.draftDao = draftDao;
        }

        @Override
        protected Void doInBackground(Draft... drafts) {
            draftDao.insertDraft(drafts[0]);
            return null;
        }
    }

    private static class UpdateDraftAsyncTask extends AsyncTask<Draft, Void, Void> {
        DraftDao draftDao;

        private UpdateDraftAsyncTask(DraftDao draftDao) {
            this.draftDao = draftDao;
        }

        @Override
        protected Void doInBackground(Draft... drafts) {
            draftDao.updateDraft(drafts[0]);
            return null;
        }
    }

    private static class DeleteAllDraftsAsyncTask extends AsyncTask<Void, Void, Void> {
        DraftDao draftDao;

        private DeleteAllDraftsAsyncTask(DraftDao draftDao) {
            this.draftDao = draftDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            draftDao.deleteAllDrafts();
            return null;
        }
    }

    private static class DeleteDraftAsyncTask extends AsyncTask<Draft, Void, Void> {
        DraftDao draftDao;

        private DeleteDraftAsyncTask(DraftDao draftDao) {
            this.draftDao = draftDao;
        }

        @Override
        protected Void doInBackground(Draft... drafts) {
            draftDao.deleteDraft(drafts[0]);
            return null;
        }
    }



    // Packs AsyncTasks
    private static class InsertPackAsyncTask extends AsyncTask<Pack, Void, Void> {
        private PackDao packDao;

        private InsertPackAsyncTask(PackDao draftDao) {
            this.packDao = draftDao;
        }

        @Override
        protected Void doInBackground(Pack... packEntities) {
            packDao.insertPack(packEntities[0]);
            return null;
        }
    }

    private static class UpdatePackAsyncTask extends AsyncTask<Pack, Void, Void> {
        PackDao packDao;

        private UpdatePackAsyncTask(PackDao draftDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Pack... packs) {
            packDao.updatePack(packs[0]);
            return null;
        }
    }

    private static class DeletePackAsyncTask extends AsyncTask<Pack, Void, Void> {
        PackDao packDao;

        private DeletePackAsyncTask(PackDao draftDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Pack... packs) {
            packDao.deletePack(packs[0]);
            return null;
        }
    }

    private static class DeleteAllPacksAsyncTask extends AsyncTask<Void, Void, Void> {
        PackDao packDao;

        private DeleteAllPacksAsyncTask(PackDao draftDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            packDao.deleteAllPacks();
            return null;
        }
    }

}
