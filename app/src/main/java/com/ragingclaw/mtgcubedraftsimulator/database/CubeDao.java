package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.converters.ListTypeConverter;

import java.util.List;

@Dao
@TypeConverters(ListTypeConverter.class)
public interface CubeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCube(Cube cube);

    // get every cube in the database
    @Query("SELECT * From cubes")
    LiveData<List<Cube>> getAllCubes();

    // get only the current users cubes
    @Query("SELECT * From cubes Where cubes.userId = :userId")
    LiveData<List<Cube>> getUserCubes(String userId);

    // get only a single users cube
    @Query("SELECT * From cubes Where cubes.userId = :userId AND cubes.cubeId = :cubeId")
    Cube getUserCube(String userId, Integer cubeId);

    @Update
    void updateCube(Cube cube);

    @Query("DELETE FROM cubes")
    void deleteAllCubes();

    @Delete
    void deleteCube(Cube cube);
}
