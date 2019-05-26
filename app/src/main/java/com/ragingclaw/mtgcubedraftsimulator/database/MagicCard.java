package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.converters.BigDecimalTypeConverter;
import com.ragingclaw.mtgcubedraftsimulator.converters.StringTypeConverter;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.magicthegathering.javasdk.resource.Card;
import io.magicthegathering.javasdk.resource.ForeignData;
import io.magicthegathering.javasdk.resource.Ruling;

@Entity(tableName = "cards")
@TypeConverters({StringTypeConverter.class, BigDecimalTypeConverter.class})
public class MagicCard {
    @PrimaryKey
    @NonNull
    private int multiverseid = -1;

    private String id;
    private String layout;
    private String name;
    private ArrayList<String> names;
    private String manaCost;
    private double cmc;
    private ArrayList<String> colors;
    private ArrayList<String> colorIdentity;
    private String type;
    private ArrayList<String> supertypes;
    private ArrayList<String> types;
    private ArrayList<String> subtypes;
    private String rarity;
    private String text;
    private String originalText;
    private String flavor;
    private String artist;
    private String number;
    private String power;
    private String toughness;
    private String loyalty;
    private String border;
    private String releaseDate;
    private String set;
    private String setName;
    private String imageUrl;

    public MagicCard(int multiverseid, String id, String layout, String name, ArrayList<String> names, String manaCost, double cmc, ArrayList<String> colors, ArrayList<String> colorIdentity, String type, ArrayList<String> supertypes, ArrayList<String> types, ArrayList<String> subtypes, String rarity, String text, String originalText, String flavor, String artist, String number, String power, String toughness, String loyalty, String border, String releaseDate, String set, String setName, String imageUrl) {
        this.multiverseid = multiverseid;
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
        this.border = border;
        this.releaseDate = releaseDate;
        this.set = set;
        this.setName = setName;
        this.imageUrl = imageUrl;
    }

    public int getMultiverseid() {
        return multiverseid;
    }

    public void setMultiverseid(int multiverseid) {
        this.multiverseid = multiverseid;
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

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
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

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ArrayList<String> getColorIdentity() {
        return colorIdentity;
    }

    public void setColorIdentity(ArrayList<String> colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getSupertypes() {
        return supertypes;
    }

    public void setSupertypes(ArrayList<String> supertypes) {
        this.supertypes = supertypes;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public ArrayList<String> getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(ArrayList<String> subtypes) {
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

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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