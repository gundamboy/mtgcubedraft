package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PacksDao {
    // select every draft in the database
    @Query("SELECT * From packs")
    LiveData<List<PacksEntity>> getAllPacks();

    @Query("SELECT * From packs " +
            "INNER JOIN drafts ON packs.draftId " +
            "INNER JOIN cubes ON drafts.cubeId " +
            "Where cubes.userId = :userId")
    LiveData<List<PacksEntity>> getUserPacks(String userId);

    @Insert()
    void insertPack(PacksEntity... packsEntities);

    @Query("DELETE FROM packs")
    void deleteAll();

    @Query("DELETE FROM packs WHERE packs.packId = :packId")
    void deletePack(int packId);

    @Update
    void updatePack(PacksEntity... packsEntities);
}
