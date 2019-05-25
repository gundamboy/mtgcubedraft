package com.ragingclaw.mtgcubedraftsimulator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.magicthegathering.javasdk.resource.Card;

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
                    ApplicationDatabase.class, "mtgCubeSimDB").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
