package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.converters.IntegerIdTypeConverter;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

@Entity(tableName = "packs",
        indices = {@Index(name = "idx_pack", value = {"packId"})})
@TypeConverters(IntegerIdTypeConverter.class)
@Parcel(Parcel.Serialization.BEAN)
public class Pack {

    @PrimaryKey(autoGenerate = true)
    private int packId;

    private int booster_num;
    private int seat_num;
    private int cubeId;
    private List<Integer> cardIDs;

    @ParcelConstructor
    public Pack(int packId, int booster_num, int seat_num, int cubeId, List<Integer> cardIDs) {
        this.packId = packId;
        this.booster_num = booster_num;
        this.seat_num = seat_num;
        this.cubeId = cubeId;
        this.cardIDs = cardIDs;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public int getBooster_num() {
        return booster_num;
    }

    public void setBooster_num(int booster_num) {
        this.booster_num = booster_num;
    }

    public int getSeat_num() {
        return seat_num;
    }

    public void setSeat_num(int seat_num) {
        this.seat_num = seat_num;
    }

    public int getCubeId() {
        return cubeId;
    }

    public void setCubeId(int cubeId) {
        this.cubeId = cubeId;
    }

    public List<Integer> getCardIDs() {
        return cardIDs;
    }

    public void setCardIDs(List<Integer> cardIDs) {
        this.cardIDs = cardIDs;
    }
}
