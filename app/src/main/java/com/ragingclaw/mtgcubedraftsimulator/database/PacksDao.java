package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PacksDao {
    // select every draft in the database
    @Query("SELECT * From packs")
    LiveData<List<PacksEntity>> getAllDrafts();

    @Insert()
    void insertPack(PacksEntity packsEntity);

    @Query("DELETE FROM packs")
    void deleteAll();

    @Query("DELETE FROM packs WHERE packs.packId = :packId")
    void deletePack(int packId);
}
