package com.ragingclaw.mtgcubedraftsimulator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.google.firebase.firestore.auth.User;

@Database(entities = {MagicCard.class, Cube.class, Pack.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
    private static ApplicationDatabase INSTANCE;
    public abstract MagicCardDao mtgCardDao();
    public abstract CubeDao cubesDao();
    public abstract PackDao packDoa();

    public static ApplicationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ApplicationDatabase.class, "mtgCubeSimDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
