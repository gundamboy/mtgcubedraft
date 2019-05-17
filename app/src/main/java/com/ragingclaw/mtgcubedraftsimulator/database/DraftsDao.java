package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.ArrayList;

@TypeConverters(StringTypeConverter.class)
public interface DraftsDao {

    // select every draft in the database
    @Query("SELECT * From drafts")
    LiveData<ArrayList<DraftsEntity>> getAllDrafts();

    // select only the specific user drafts and their packs
    @Query("SELECT * From drafts, packs " +
            "INNER JOIN cubes ON drafts.cubeId " +
            "INNER JOIN packs ON packs.draftId " +
            "Where cubes.userId = :userId")
    LiveData<ArrayList<DraftsEntity>> getUserDrafts(String userId);

    @Insert()
    void insertDraft(DraftsEntity... draftsEntities);

    @Update
    void updateDraft(DraftsEntity... draftsEntities);

    @Query("DELETE FROM drafts")
    void deleteAllDrafts();

    @Query("DELETE FROM drafts WHERE drafts.draftID = :draftId")
    void deleteDraft(int draftId);
}
