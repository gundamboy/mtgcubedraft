package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.ragingclaw.mtgcubedraftsimulator.converters.BigDecimalTypeConverter;
import com.ragingclaw.mtgcubedraftsimulator.converters.StringTypeConverter;

import java.util.List;

@Dao
@TypeConverters({StringTypeConverter.class, BigDecimalTypeConverter.class})
public interface MagicCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCard(MagicCard... magicCard);

    @Query("SELECT * FROM cards")
    LiveData<List<MagicCard>> getAllCards();

    @Query("SELECT * FROM cards")
    List<MagicCard> getAllCardsStatic();

    @Query("SELECT * FROM cards WHERE cards.multiverseid = :id")
    MagicCard getSingleCard(int id);

    @Update
    void updateCard(MagicCard magicCard);

    @Delete
    void deleteCard(MagicCard magicCard);

    @Query("DELETE FROM cards")
    void deleteAllCards();
}
