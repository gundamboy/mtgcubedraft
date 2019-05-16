package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;


@Entity(tableName = "cards")
@TypeConverters(StringTypeConverter.class)
public class DBCardsEntity {

    @NonNull
    @PrimaryKey
    private String id;

    private String layout;
    private String name;
    private String[] names;
    private String manaCost;
    private double cmc;
    private String[] colors;
    private String[] colorIdentity;
    private String type;
    private String[] supertypes;
    private String[] types;
    private String[] subtypes;
    private String rarity;
    private String text;
    private String originalText;
    private String flavor;
    private String artist;
    private String number;
    private String power;
    private String toughness;
    private String loyalty;
    private int multiverseid = -1;
    private String imageName;
    private String watermark;
    private String border;
    private boolean timeshifted;
    private int hand;
    private int life;
    private boolean reserved;
    private String releaseDate;
    private boolean starter;
    private String set;
    private String setName;
    private String imageUrl;

    public DBCardsEntity(@NonNull String id, String layout, String name, String[] names, String manaCost, double cmc, String[] colors, String[] colorIdentity, String type, String[] supertypes, String[] types, String[] subtypes, String rarity, String text, String originalText, String flavor, String artist, String number, String power, String toughness, String loyalty, int multiverseid, String imageName, String watermark, String border, boolean timeshifted, int hand, int life, boolean reserved, String releaseDate, boolean starter, String set, String setName, String imageUrl) {
        this.id = id;
        this.layout = layout;
        this.name = name;
        this.names = names;
        this.manaCost = manaCost;
        this.cmc = cmc;
        this.colors = colors;
        this.colorIdentity = colorIdentity;
        this.type = type;
        this.supertypes = supertypes;
        this.types = types;
        this.subtypes = subtypes;
        this.rarity = rarity;
        this.text = text;
        this.originalText = originalText;
        this.flavor = flavor;
        this.artist = artist;
        this.number = number;
        this.power = power;
        this.toughness = toughness;
        this.loyalty = loyalty;
        this.multiverseid = multiverseid;
        this.imageName = imageName;
        this.watermark = watermark;
        this.border = border;
        this.timeshifted = timeshifted;
        this.hand = hand;
        this.life = life;
        this.reserved = reserved;
        this.releaseDate = releaseDate;
        this.starter = starter;
        this.set = set;
        this.setName = setName;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public double getCmc() {
        return cmc;
    }

    public void setCmc(double cmc) {
        this.cmc = cmc;
    }

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public String[] getColorIdentity() {
        return colorIdentity;
    }

    public void setColorIdentity(String[] colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getSupertypes() {
        return supertypes;
    }

    public void setSupertypes(String[] supertypes) {
        this.supertypes = supertypes;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(String[] subtypes) {
        this.subtypes = subtypes;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public int getMultiverseid() {
        return multiverseid;
    }

    public void setMultiverseid(int multiverseid) {
        this.multiverseid = multiverseid;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public boolean isTimeshifted() {
        return timeshifted;
    }

    public void setTimeshifted(boolean timeshifted) {
        this.timeshifted = timeshifted;
    }

    public int getHand() {
        return hand;
    }

    public void setHand(int hand) {
        this.hand = hand;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isStarter() {
        return starter;
    }

    public void setStarter(boolean starter) {
        this.starter = starter;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
