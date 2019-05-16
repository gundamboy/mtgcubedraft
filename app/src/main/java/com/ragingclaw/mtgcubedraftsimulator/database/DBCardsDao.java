package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.List;

@Dao
@TypeConverters(StringTypeConverter.class)
public interface DBCardsDao {

    @Query("SELECT * From cards")
    LiveData<List<DBCardsEntity>> getAllDBCards();

    @Query("SELECT * From cards WHERE cards.multiverseid = :multiverseid")
    LiveData<List<DBCardsEntity>> getDBCards(int multiverseid);

    @Insert()
    void insertCard(DBCardsEntity card);

    @Query("DELETE FROM cards")
    void deleteAllCards();

    @Query("DELETE FROM cards WHERE cards.multiverseid = :multiverseid")
    void deleteCard(int multiverseid);
}
