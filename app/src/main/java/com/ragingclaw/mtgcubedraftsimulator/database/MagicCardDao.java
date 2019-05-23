package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.List;

@Dao
@TypeConverters(StringTypeConverter.class)
public interface MagicCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCard(MagicCard magicCard);

    @Query("SELECT * FROM cards")
    LiveData<List<MagicCard>> getAllCards();

    @Query("SELECT * FROM cards WHERE cards.id = :id")
    LiveData<MagicCard> getSingleCard(int id);

    @Update
    void updateCard(MagicCard magicCard);

    @Delete
    void deleteCard(MagicCard magicCard);

    @Query("DELETE FROM cards")
    void deleteAllCards();
}
