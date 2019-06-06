package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ApplicationRepository {

    private final MagicCardDao mMagicCardDao;
    private final CubeDao mCubesDoa;
    private final PackDao mPackDao;

    public ApplicationRepository(Application application) {
        ApplicationDatabase db = ApplicationDatabase.getDatabase(application);
        mMagicCardDao = db.mtgCardDao();
        mCubesDoa = db.cubesDao();
        mPackDao = db.packDoa();
    }

    // CARD *************************************************
    public void insertCard(MagicCard... magicCard) {
        new InsertCardAsyncTask(mMagicCardDao).execute(magicCard);
    }

    public void updateCard(MagicCard magicCard) {
        new UpdateCardAsyncTask(mMagicCardDao).execute(magicCard);
    }


    public void deleteCard(MagicCard magicCard) {
        new DeleteCardAsyncTask(mMagicCardDao).execute(magicCard);
    }

    public void deleteAllCards() {
        new DeleteAllCardsAsyncTask(mMagicCardDao).execute();
    }

    public LiveData<List<MagicCard>> mGetAllCards() {
        return mMagicCardDao.getAllCards();
    }

    public List<MagicCard> getAllCardsStatic() {
        return mMagicCardDao.getAllCardsStatic();
    }

    public MagicCard mGetSingleCard(int id) {
        return mMagicCardDao.getSingleCard(id);
    }


    // CUBE *************************************************
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

    public List<Cube> getAllCubesStatic() {
        return mCubesDoa.getAllCubesStatic();
    }

    public Long insertCubesWithReturn(Cube cube) {
        return mCubesDoa.insertCubeWithReturn(cube);
    }

    public LiveData<List<Cube>> getUserCubes(String userId) {
        return mCubesDoa.getUserCubes(userId);
    }

    public List<Cube> getUserCubesStatic(String userId) {
        return mCubesDoa.getUserCubesStatic(userId);
    }

    public Cube getUserCube(String userId, Integer cubeId) {
        return mCubesDoa.getUserCube(userId, cubeId);
    }



    // Packs *************************************************
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

    public LiveData<List<Pack>> getLivePlayerPacks(int seatNum) {
        return mPackDao.getLivePlayerPacks(seatNum);
    }

    public List<Pack> getAllPacksStatic() {return mPackDao.getAllPacksStatic();}

    public List<Pack> getPlayerPacks(int seatNum) {return mPackDao.getPlayerPacks(seatNum);}
    public Pack getPlayerPacksByNum(int seatNum, int boosterNum) {return mPackDao.getPlayerPacksByNum(seatNum, boosterNum);}

    // Card AsyncTasks *************************************************
    private static class InsertCardAsyncTask extends android.os.AsyncTask<MagicCard, Void, Void> {
        private final MagicCardDao mMagicCardDao;

        private InsertCardAsyncTask(MagicCardDao magicCardDao) {
            this.mMagicCardDao = magicCardDao;
        }

        @Override
        protected Void doInBackground(MagicCard... magicCardEntities) {
            mMagicCardDao.insertCard(magicCardEntities[0]);
            return null;
        }
    }

    private static class UpdateCardAsyncTask extends android.os.AsyncTask<MagicCard, Void, Void> {
        private final MagicCardDao mMagicCardDao;

        private UpdateCardAsyncTask(MagicCardDao magicCardDao) {
            this.mMagicCardDao = magicCardDao;
        }

        @Override
        protected Void doInBackground(MagicCard... magicCardEntities) {
            mMagicCardDao.updateCard(magicCardEntities[0]);
            return null;
        }
    }

    private static class DeleteCardAsyncTask extends android.os.AsyncTask<MagicCard, Void, Void> {
        private final MagicCardDao mMagicCardDao;

        private DeleteCardAsyncTask(MagicCardDao magicCardDao) {
            this.mMagicCardDao = magicCardDao;
        }

        @Override
        protected Void doInBackground(MagicCard... magicCardEntities) {
            mMagicCardDao.deleteCard(magicCardEntities[0]);
            return null;
        }
    }

    private static class DeleteAllCardsAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        final MagicCardDao magicCardDao;

        private DeleteAllCardsAsyncTask(MagicCardDao magicCardDao) {
            this.magicCardDao = magicCardDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            magicCardDao.deleteAllCards();
            return null;
        }
    }

    // Cube AsyncTasks *************************************************
    private static class InsertCubeAsyncTask extends android.os.AsyncTask<Cube, Void, Void> {
        private final CubeDao cubeDao;

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
        private final CubeDao cubeDao;

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

        private final CubeDao cubeDao;

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

        private final CubeDao cubeDao;

        private DeleteAllCubesAsyncTask(CubeDao cubeDao) {
            this.cubeDao = cubeDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cubeDao.deleteAllCubes();
            return null;
        }
    }

    // Packs AsyncTasks *************************************************
    private static class InsertPackAsyncTask extends AsyncTask<Pack, Void, Void> {
        private final PackDao packDao;

        private InsertPackAsyncTask(PackDao packDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Pack... packEntities) {
            packDao.insertPack(packEntities[0]);
            return null;
        }
    }

    private static class UpdatePackAsyncTask extends AsyncTask<Pack, Void, Void> {
        final PackDao packDao;

        private UpdatePackAsyncTask(PackDao packDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Pack... packs) {
            packDao.updatePack(packs[0]);
            return null;
        }
    }

    private static class DeletePackAsyncTask extends AsyncTask<Pack, Void, Void> {
        final PackDao packDao;

        private DeletePackAsyncTask(PackDao packDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Pack... packs) {
            packDao.deletePack(packs[0]);
            return null;
        }
    }

    private static class DeleteAllPacksAsyncTask extends AsyncTask<Void, Void, Void> {
        final PackDao packDao;

        private DeleteAllPacksAsyncTask(PackDao packDao) {
            this.packDao = packDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            packDao.deleteAllPacks();
            return null;
        }
    }

}