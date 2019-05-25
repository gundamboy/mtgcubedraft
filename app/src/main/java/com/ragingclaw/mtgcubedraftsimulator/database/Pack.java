package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.converters.IntegerIdTypeConverter;

import java.util.List;

@Entity(tableName = "packs",
        foreignKeys = @ForeignKey(
                entity = Draft.class,
                parentColumns = "draftID",
                childColumns = "draftId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(name = "idx_pack", value = {"draftId"})}
        )
@TypeConverters(IntegerIdTypeConverter.class)
public class Pack {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int packId;
    private int draftId;
    private int booster_num;
    private int seat_num;
    private int cubeId;
    private List<Integer> cardIDs;

    public Pack(int packId, int draftId, int cubeId, int booster_num, int seat_num, List<Integer> cardIDs) {
        this.packId = packId;
        this.draftId = draftId;
        this.cubeId = cubeId;
        this.booster_num = booster_num;
        this.seat_num = seat_num;
        this.cardIDs = cardIDs;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public int getDraftId() {
        return draftId;
    }

    public void setDraftId(int draftId) {
        this.draftId = draftId;
    }

    public int getCubeId() {
        return cubeId;
    }

    public void setCubeId(int cubeId) {
        this.cubeId = cubeId;
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

    public List<Integer> getCardIDs() {
        return cardIDs;
    }

    public void setCardIDs(List<Integer> cardIDs) {
        this.cardIDs = cardIDs;
    }
}
