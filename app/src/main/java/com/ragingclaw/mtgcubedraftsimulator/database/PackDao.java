package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PackDao {
    // select every draft in the database
    @Query("SELECT * From packs")
    LiveData<List<Pack>> getAllPacks();

    @Query("SELECT * From packs")
    List<Pack> getAllPacksStatic();

    @Query("SELECT * From packs WHERE seat_num = :seatNum")
    List<Pack> getPlayerPacks(int seatNum);

    @Query("SELECT * From packs WHERE seat_num = :seatNum AND booster_num = :boosterNum")
    Pack getPlayerPacksByNum(int seatNum, int boosterNum);

    @Query("SELECT * From packs WHERE seat_num = :seatNum")
    LiveData<List<Pack>> getLivePlayerPacks(int seatNum);

    @Insert()
    long insertPack(Pack pack);

    // packs should actually delete with the draft ids
    @Query("DELETE FROM packs")
    void deleteAllPacks();

    // single packs do not actually delete
    @Delete
    void deletePack(Pack pack);

    // packs do not actually get updated.
    @Update
    void updatePack(Pack pack);
}
