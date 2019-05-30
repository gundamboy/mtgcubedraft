package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.converters.IntegerIdTypeConverter;

import java.util.List;

@Dao
@TypeConverters(IntegerIdTypeConverter.class)
public interface DraftDao {

    // select every draft in the database
    @Query("SELECT * From drafts")
    LiveData<List<Draft>> getAllDrafts();

    // select only the specific user drafts and their packs
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * From drafts " +
            "INNER JOIN cubes ON drafts.cubeId " +
            "Where cubes.userId = :userId")
    LiveData<List<Draft>> getUserDrafts(String userId);

    @Query("SELECT * From drafts " +
            "Where drafts.draftID = :draftId")
    LiveData<Draft> getSingleDraft(Integer draftId);

    @Insert()
    void insertDraft(Draft... draftsEntities);

    @Update
    void updateDraft(Draft... draftsEntities);

    @Query("DELETE FROM drafts")
    void deleteAllDrafts();

    @Delete
    void deleteDraft(Draft draft);
}
