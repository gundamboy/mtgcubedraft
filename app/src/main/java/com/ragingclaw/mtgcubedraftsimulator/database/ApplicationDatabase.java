package com.ragingclaw.mtgcubedraftsimulator.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.Executors;

import io.magicthegathering.javasdk.resource.Card;
import timber.log.Timber;

@Database(entities = {MagicCard.class, Cube.class, User.class, Draft.class, Pack.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
    private static ApplicationDatabase INSTANCE;
    public abstract MagicCardDao mtgCardDao();
    public abstract CubeDao cubesDao();
    public abstract UserDao userDao();
    public abstract DraftDao draftDoa();
    public abstract PackDao packDoa();

    public static ApplicationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ApplicationDatabase.class, "mtgCubeSimDB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new insertData(INSTANCE).execute();
        }
    };

    private static class insertData extends AsyncTask<Void, Void, Void> {

        private MagicCardDao magicCardDao;

        public insertData(ApplicationDatabase db) {
            magicCardDao = db.mtgCardDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.tag("fart").i("should now be installing data........");
            String json = null;
            try {
                Timber.tag("fart").i("inside try block........");

                String filename = "cardIds.json";

                // this is coming back null but the file exists. this works in other files so wtf
                InputStream is = Objects.requireNonNull(this.getClass().getClassLoader()).getResourceAsStream(filename);


                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                JSONObject obj = new JSONObject(json);
                JSONArray idArray = obj.getJSONArray("ids");

                for(int i = 0; i < idArray.length(); i++) {
                    int id = idArray.getInt(i);

                    if(i > 0 && i < 10) {
                        Timber.tag("fart").i("id is: %s", id);
                    }

                    MagicCard card = new MagicCard(
                            null,
                            null,
                            null,
                            null,
                            null,
                            0.0,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            id,
                            null,
                            null,
                            null,
                            null,
                            false,
                            0,
                            0,
                            false,
                            null,
                            false,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );

                    magicCardDao.insertCard(card);
                }

            } catch (JSONException e) {
                Timber.tag("fart").w(e);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
