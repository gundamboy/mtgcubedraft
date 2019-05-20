package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PackDao {
    // select every draft in the database
    @Query("SELECT * From packs")
    LiveData<List<Pack>> getAllPacks();

    @Query("SELECT * From packs WHERE packs.packId = :packId")
    LiveData<Pack> getSinglePack(int packId);

    @Query("SELECT * From packs " +
            "INNER JOIN drafts ON packs.draftId " +
            "INNER JOIN cubes ON drafts.cubeId " +
            "Where cubes.userId = :userId")
    LiveData<List<Pack>> getUserPacks(String userId);

    @Query("SELECT * From cubes " +
            "INNER JOIN packs on packs.cubeId " +
            "WHERE cubes.cubeId = :cubeId")
    LiveData<Cube> getCubeFromPack(Integer cubeId);

    @Query("SELECT * From drafts " +
            "INNER JOIN packs on packs.cubeId " +
            "WHERE drafts.draftID = :draftId")
    LiveData<Draft> getDraftFromPack(Integer draftId);

    @Query("SELECT * From packs, drafts, cubes " +
            "INNER JOIN drafts on drafts.cubeId " +
            "WHERE packs.packId = :packId AND drafts.draftID = :draftId" )
    LiveData<List<Draft>> getDraftAndCubeFromPack(Integer packId, Integer draftId);


    @Insert()
    void insertPack(Pack... packsEntities);

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
