package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

// TODO: get multiverse ID
public class AppRepository {
    private FirebaseAuth mAuth;
    private FirebaseUser userID;
    private UsersDao mUsersDao;
    private CubesDao mCubesDoa;
    private DBCardsDao mDBCardsDoa;

    private int mMultiverseId;


    private LiveData<ArrayList<CubesEntity>> mAllCubes;
    private LiveData<ArrayList<CubesEntity>> mUserCubes;
    private LiveData<ArrayList<CubesEntity>> mUserCubesAndDrafts;
    private LiveData<ArrayList<DBCardsEntity>> mAllDBCards;
    private LiveData<ArrayList<DBCardsEntity>> mDBCard;



    public AppRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser();
        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mUsersDao = db.usersDao();
        mCubesDoa = db.cubesDao();


        mAllCubes = mCubesDoa.getAllCubes();
        mUserCubes = mCubesDoa.getUserCubes(userID);
        mUserCubesAndDrafts = mCubesDoa.getAllUserCubeInfo(userID);

        mDBCardsDoa = db.dbCardsDao();
        mAllDBCards = mDBCardsDoa.getAllDBCards();
        mDBCard = mDBCardsDoa.getDBCard(mMultiverseId);
    }






    // Cubes
    public void insertCube(CubesEntity cubesEntity) {
        new InsertCubeAsyncTask(mCubesDoa).execute(cubesEntity);
    }

    public void updateCube(CubesEntity cubesEntity) {
        new UpdateCubeAsyncTask(mCubesDoa).execute(cubesEntity);
    }

    public void deleteAllCubes() {
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



    // DBCards
    public void insertCard() {
        new InsertCardAsyncTask(mDBCardsDoa).execute();
    }

    public LiveData<ArrayList<DBCardsEntity>> getAllCards() {
        return mAllDBCards;
    }

    public LiveData<ArrayList<DBCardsEntity>> getDBCard() {
        return mDBCard;
    }

    public void updateCard() {
        new UpdateCardAsyncTask(mDBCardsDoa).execute();
    }

    public void deleteAllCards() {
        new DeleteAllCardsAsyncTask(mDBCardsDoa).execute();
    }

    public void deleteCard() {
        new DeleteCardAsyncTask(mDBCardsDoa).execute();
    }



    // Users
    public void insertUser(UsersEntity usersEntity) {
        new InsertUserAsyncTask(mUsersDao).execute(usersEntity);
    }

    public void updateUser(UsersEntity usersEntity) {
        new UpdateUserAsyncTask(mUsersDao).execute(usersEntity);
    }

    public void deleteAllUsers() {
        new DeleteAllUserAsyncTask(mUsersDao).execute();
    }

    public void deleteUser(String id) {
        new DeleteUserAsyncTask(mUsersDao).execute(id);
    }








    // CUBES AsyncTasks
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



    //DBCARDS AsyncTasks
    private static class InsertCardAsyncTask extends AsyncTask<DBCardsEntity, Void, Void> {
        private DBCardsDao dbCardDao;

        private InsertCardAsyncTask(DBCardsDao dbCardDao) {
            this.dbCardDao = dbCardDao;
        }

        @Override
        protected Void doInBackground(DBCardsEntity... dbCardEntities) {
            dbCardDao.insertCard(dbCardEntities[0]);
            return null;
        }
    }

    private static class UpdateCardAsyncTask extends AsyncTask<DBCardsEntity, Void, Void> {
        private DBCardsDao dbCardDao;

        private UpdateCardAsyncTask(DBCardsDao dbCardDao) {
            this.dbCardDao = dbCardDao;
        }

        @Override
        protected Void doInBackground(DBCardsEntity... dbCardEntities) {
            dbCardDao.updateCard(dbCardEntities[0]);
            return null;
        }
    }

    private static class DeleteAllCardsAsyncTask extends AsyncTask<Void, Void, Void> {
        private DBCardsDao dbCardDao;

        private DeleteAllCardsAsyncTask(DBCardsDao dbCardDao) {
            this.dbCardDao = dbCardDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dbCardDao.deleteAllCards();
            return null;
        }
    }

    private static class DeleteCardAsyncTask extends AsyncTask<DBCardsEntity, Void, Void> {
        private DBCardsDao dbCardDao;

        private DeleteCardAsyncTask(DBCardsDao dbCardDao) {
            this.dbCardDao = dbCardDao;
        }

        @Override
        protected Void doInBackground(DBCardsEntity... dbCardEntities) {
            dbCardDao.deleteCard(dbCardEntities[0]);
            return null;
        }
    }



    // USERS AsyncTasks
    private static class InsertUserAsyncTask extends AsyncTask<UsersEntity, Void, Void> {
        private UsersDao usersDao;

        public InsertUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(UsersEntity... usersEntities) {
            usersDao.insertUser(usersEntities[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<UsersEntity, Void, Void> {
        private UsersDao usersDao;

        public UpdateUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(UsersEntity... usersEntities) {
            usersDao.updateUser(usersEntities[0]);
            return null;
        }
    }

    private static class DeleteAllUserAsyncTask extends AsyncTask<Void, Void, Void> {
        private UsersDao usersDao;

        public DeleteAllUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            usersDao.deleteAllUsers();
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<String, Void, Void> {
        private UsersDao usersDao;

        public DeleteUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            usersDao.deleteUser(strings[0]);
            return null;
        }
    }

}
