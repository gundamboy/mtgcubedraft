package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.google.firebase.auth.FirebaseUser;
import com.ragingclaw.mtgcubedraftsimulator.interfaces.IntegerIdTypeConverter;

import java.util.ArrayList;

@Dao
@TypeConverters(IntegerIdTypeConverter.class)
public interface CubesDao {

    // select every cube in the database
    @Query("SELECT * From cubes")
    LiveData<ArrayList<CubesEntity>> getAllCubes();

    // select only the current users cubes
    @Query("SELECT * From cubes Where cubes.userId = :userId")
    LiveData<ArrayList<CubesEntity>> getUserCubes(FirebaseUser userId);

    // select only the users cubes, drafts, and packs all in 1 go
    @Query("SELECT * From cubes, drafts, packs " +
            "INNER JOIN drafts ON cubes.cubeId = drafts.cubeId " +
            "INNER JOIN packs ON packs.draftId = drafts.draftID " +
            "Where cubes.userId = :userId")
    LiveData<ArrayList<CubesEntity>> getAllUserCubeInfo(FirebaseUser userId);

    @Insert()
    void insertCube(CubesEntity... cubesEntities);

    @Query("DELETE FROM cubes")
    void deleteAllCubes();

    @Query("DELETE FROM cubes WHERE cubes.cubeId = :cubeId")
    void deleteCube(int cubeId);

    @Update
    void updateCube(CubesEntity... cubesEntities);
}
