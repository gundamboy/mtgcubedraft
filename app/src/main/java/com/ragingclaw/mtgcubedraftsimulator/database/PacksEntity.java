package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "packs",
        foreignKeys = @ForeignKey(entity = DraftsEntity.class, parentColumns = "id",
                childColumns = "packID",
                onDelete = ForeignKey.CASCADE))
public class PacksEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int packId;

    private int draftId;
    private int booster_num;
    private int seat_num;
    private int card01;
    private int card02;
    private int card03;
    private int card04;
    private int card05;
    private int card06;
    private int card07;
    private int card08;
    private int card09;
    private int card10;
    private int card11;
    private int card12;
    private int card13;
    private int card14;
    private int card15;

    public PacksEntity(int packId, int draftId, int booster_num, int seat_num, int card01, int card02, int card03, int card04, int card05, int card06, int card07, int card08, int card09, int card10, int card11, int card12, int card13, int card14, int card15) {
        this.packId = packId;
        this.draftId = draftId;
        this.booster_num = booster_num;
        this.seat_num = seat_num;
        this.card01 = card01;
        this.card02 = card02;
        this.card03 = card03;
        this.card04 = card04;
        this.card05 = card05;
        this.card06 = card06;
        this.card07 = card07;
        this.card08 = card08;
        this.card09 = card09;
        this.card10 = card10;
        this.card11 = card11;
        this.card12 = card12;
        this.card13 = card13;
        this.card14 = card14;
        this.card15 = card15;
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

    public int getCard01() {
        return card01;
    }

    public void setCard01(int card01) {
        this.card01 = card01;
    }

    public int getCard02() {
        return card02;
    }

    public void setCard02(int card02) {
        this.card02 = card02;
    }

    public int getCard03() {
        return card03;
    }

    public void setCard03(int card03) {
        this.card03 = card03;
    }

    public int getCard04() {
        return card04;
    }

    public void setCard04(int card04) {
        this.card04 = card04;
    }

    public int getCard05() {
        return card05;
    }

    public void setCard05(int card05) {
        this.card05 = card05;
    }

    public int getCard06() {
        return card06;
    }

    public void setCard06(int card06) {
        this.card06 = card06;
    }

    public int getCard07() {
        return card07;
    }

    public void setCard07(int card07) {
        this.card07 = card07;
    }

    public int getCard08() {
        return card08;
    }

    public void setCard08(int card08) {
        this.card08 = card08;
    }

    public int getCard09() {
        return card09;
    }

    public void setCard09(int card09) {
        this.card09 = card09;
    }

    public int getCard10() {
        return card10;
    }

    public void setCard10(int card10) {
        this.card10 = card10;
    }

    public int getCard11() {
        return card11;
    }

    public void setCard11(int card11) {
        this.card11 = card11;
    }

    public int getCard12() {
        return card12;
    }

    public void setCard12(int card12) {
        this.card12 = card12;
    }

    public int getCard13() {
        return card13;
    }

    public void setCard13(int card13) {
        this.card13 = card13;
    }

    public int getCard14() {
        return card14;
    }

    public void setCard14(int card14) {
        this.card14 = card14;
    }

    public int getCard15() {
        return card15;
    }

    public void setCard15(int card15) {
        this.card15 = card15;
    }
}
