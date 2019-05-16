package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.IntegerIdTypeConverter;


import java.util.List;

@Entity(tableName = "cubes")
@TypeConverters(IntegerIdTypeConverter.class)
public class CubesEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int cubeId;

    private int userId;
    private String cube_name;
    private int total_cards;
    private List<Integer> card_ids = null;

    public CubesEntity(int cubeId, int userId, String cube_name, int total_cards, List<Integer> card_ids) {
        this.cubeId = cubeId;
        this.userId = userId;
        this.cube_name = cube_name;
        this.total_cards = total_cards;
        this.card_ids = card_ids;
    }

    public int getCubeId() {
        return cubeId;
    }

    public String getCube_name() {
        return cube_name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotal_cards() {
        return total_cards;
    }

    public List<Integer> getCard_ids() {
        return card_ids;
    }

    public void setCubeId(int cubeId) {
        this.cubeId = cubeId;
    }

    public void setCube_name(String cube_name) {
        this.cube_name = cube_name;
    }

    public void setTotal_cards(int total_cards) {
        this.total_cards = total_cards;
    }

    public void setCard_ids(List<Integer> card_ids) {
        this.card_ids = card_ids;
    }
}

