package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.converters.BigDecimalTypeConverter;
import com.ragingclaw.mtgcubedraftsimulator.converters.StringTypeConverter;

import java.math.BigDecimal;

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
    private String[] variations;
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
    private String[] printings;
    private String imageUrl;
    private String[] legalities;
    private BigDecimal priceHigh;
    private BigDecimal priceMid;
    private BigDecimal priceLow;
    private BigDecimal onlinePriceHigh;
    private BigDecimal onlinePriceMid;
    private BigDecimal onlinePriceLow;
    private String[] rulings;
    private String[] foreignNames;

    public MagicCard(String id, String layout, String name, String[] names, String manaCost, double cmc, String[] colors, String[] colorIdentity, String type, String[] supertypes, String[] types, String[] subtypes, String rarity, String text, String originalText, String flavor, String artist, String number, String power, String toughness, String loyalty, int multiverseid, String[] variations, String imageName, String watermark, String border, boolean timeshifted, int hand, int life, boolean reserved, String releaseDate, boolean starter, String set, String setName, String[] printings, String imageUrl, String[] legalities, BigDecimal priceHigh, BigDecimal priceMid, BigDecimal priceLow, BigDecimal onlinePriceHigh, BigDecimal onlinePriceMid, BigDecimal onlinePriceLow, String[] rulings, String[] foreignNames) {
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
        this.variations = variations;
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
        this.printings = printings;
        this.imageUrl = imageUrl;
        this.legalities = legalities;
        this.priceHigh = priceHigh;
        this.priceMid = priceMid;
        this.priceLow = priceLow;
        this.onlinePriceHigh = onlinePriceHigh;
        this.onlinePriceMid = onlinePriceMid;
        this.onlinePriceLow = onlinePriceLow;
        this.rulings = rulings;
        this.foreignNames = foreignNames;
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

    public String[] getVariations() {
        return variations;
    }

    public void setVariations(String[] variations) {
        this.variations = variations;
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

    /**
     * dirty compare to in order to start testing. Just comparing the
     * MultiverseId which should be unique.
     *
     * @param toCompare A {@link Card} object hopefully
     * @return true if the same set, false if different.
     */
    @Override
    public boolean equals(Object toCompare) {
        if (toCompare instanceof Card) {
            Card cardCompare = (Card) toCompare;
            return getMultiverseid() == cardCompare.getMultiverseid()
                    && getName().equals(cardCompare.getName())
                    && getCmc() == cardCompare.getCmc();
        } else {
            return false;
        }
    }

    /**
     * Prints the Card name and multiverseId which should give enough info for
     * debug testing.
     *
     * @return The cards name and Id
     */
    @Override
    public String toString() {
        return "\nCard Name: " + getName() +
                "\nMultiverse Id: " + getMultiverseid() +
                "\nMana Cost: " + getManaCost();
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

    public String[] getPrintings() {
        return printings;
    }

    public void setPrintings(String[] printings) {
        this.printings = printings;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String[] getLegalities() {
        return legalities;
    }

    public void setLegalities(String[] legalities) {
        this.legalities = legalities;
    }

    public BigDecimal getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }

    public BigDecimal getPriceMid() {
        return priceMid;
    }

    public void setPriceMid(BigDecimal priceMid) {
        this.priceMid = priceMid;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getOnlinePriceHigh() {
        return onlinePriceHigh;
    }

    public void setOnlinePriceHigh(BigDecimal onlinePriceHigh) {
        this.onlinePriceHigh = onlinePriceHigh;
    }

    public BigDecimal getOnlinePriceMid() {
        return onlinePriceMid;
    }

    public void setOnlinePriceMid(BigDecimal onlinePriceMid) {
        this.onlinePriceMid = onlinePriceMid;
    }

    public BigDecimal getOnlinePriceLow() {
        return onlinePriceLow;
    }

    public void setOnlinePriceLow(BigDecimal onlinePriceLow) {
        this.onlinePriceLow = onlinePriceLow;
    }

    public String[] getRulings() {
        return rulings;
    }

    public void setRulings(String[] rulings) {
        this.rulings = rulings;
    }

    public String[] getForeignNames() {
        return foreignNames;
    }

    public void setForeignNames(String[] foreignNames) {
        this.foreignNames = foreignNames;
    }
}