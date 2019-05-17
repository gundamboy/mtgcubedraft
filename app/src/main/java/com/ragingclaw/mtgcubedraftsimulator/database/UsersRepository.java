package com.ragingclaw.mtgcubedraftsimulator.database;

import android.app.Application;
import android.os.AsyncTask;

public class UsersRepository {
    private UsersDao mUsersDao;

    public UsersRepository(Application application) {
        CubeSimDatabase db = CubeSimDatabase.getDatabase(application);
        mUsersDao = db.usersDao();
    }

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

    // async tasks
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
