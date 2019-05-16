package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.List;

@TypeConverters(StringTypeConverter.class)
public interface DraftsDao {

    // select every draft in the database
    @Query("SELECT * From drafts")
    LiveData<List<DraftsEntity>> getAllDrafts();

    // select only the specific user drafts and their packs
    @Query("SELECT * From drafts, packs " +
            "INNER JOIN cubes ON drafts.cubeId " +
            "INNER JOIN packs ON packs.draftId " +
            "Where cubes.userId = :userId")
    LiveData<List<DraftsEntity>> getUserDrafts(int userId);

    @Insert()
    void insertDraft(DraftsEntity draftsEntity);

    @Query("DELETE FROM drafts")
    void deleteAll();

    @Query("DELETE FROM drafts WHERE drafts.draftID = :draftId")
    void deleteCube(int draftId);
}
