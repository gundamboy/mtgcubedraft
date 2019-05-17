package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DBCardsRepository {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int mMultiverseId;

    private DBCardsDao mDBCardsDoa;
    private LiveData<ArrayList<DBCardsEntity>> mAllDBCards;
    private LiveData<ArrayList<DBCardsEntity>> mDBCard;

    DBCardsRepository(Application application) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // TODO: get multiverse ID

        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mDBCardsDoa = db.dbCardsDao();
        mAllDBCards = mDBCardsDoa.getAllDBCards();
        mDBCard = mDBCardsDoa.getDBCard(mMultiverseId);
    }


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

}
