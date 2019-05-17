package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.ArrayList;

@Dao
@TypeConverters(StringTypeConverter.class)
public interface DBCardsDao {

    @Query("SELECT * From cards")
    LiveData<ArrayList<DBCardsEntity>> getAllDBCards();

    @Query("SELECT * From cards WHERE cards.multiverseid = :multiverseid")
    LiveData<ArrayList<DBCardsEntity>> getDBCard(int multiverseid);

    @Insert()
    void insertCard(DBCardsEntity... dbCardsEntities);

    @Query("DELETE FROM cards")
    void deleteAllCards();

    @Query("DELETE FROM cards WHERE cards.multiverseid = :multiverseid")
    void deleteCard(DBCardsEntity multiverseid);

    @Update
    void updateCard(DBCardsEntity... dbCardsEntities);
}
