package com.ragingclaw.mtgcubedraftsimulator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CubesEntity.class, DBCardsEntity.class, DraftsEntity.class, PacksEntity.class, UsersEntity.class}, version = 1)
public abstract class CubeSimDatabase extends RoomDatabase {
    private static CubeSimDatabase INSTANCE;
    public abstract CubesDao cubesDao();
    public abstract DBCardsDao dbCardsDao();
    public abstract DraftsDao draftsDao();
    public abstract PacksDao packsDao();
    public abstract UsersDao usersDao();

    public static CubeSimDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CubeSimDatabase.class, "cube_sim_db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
