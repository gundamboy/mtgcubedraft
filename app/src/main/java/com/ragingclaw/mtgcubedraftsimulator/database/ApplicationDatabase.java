package com.ragingclaw.mtgcubedraftsimulator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ragingclaw.mtgcubedraftsimulator.sqlAsset.AssetSQLiteOpenHelperFactory;

@Database(entities = {MagicCard.class, Cube.class, Pack.class}, version = 1, exportSchema = false)
public abstract class ApplicationDatabase extends RoomDatabase {
    private static ApplicationDatabase INSTANCE;
    public abstract MagicCardDao mtgCardDao();
    public abstract CubeDao cubesDao();
    public abstract PackDao packDoa();

    public static ApplicationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            // this copies the pre-populated database over. if not, id need
            // 300mb of json and it takes a literal 3.5 minutes to load.
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ApplicationDatabase.class,
                    "mtgCubeSimDB.db")
                    .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
