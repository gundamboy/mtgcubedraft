package com.ragingclaw.mtgcubedraftsimulator.utils;

import android.app.Activity;
import com.ragingclaw.mtgcubedraftsimulator.database.MagicCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.magicthegathering.javasdk.resource.Card;
import io.magicthegathering.javasdk.resource.ForeignData;
import io.magicthegathering.javasdk.resource.Legality;
import io.magicthegathering.javasdk.resource.Ruling;
import timber.log.Timber;

public class MTGUtils {


    public static String randomIdentifier() {
        String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
        java.util.Random rand = new java.util.Random();
        final Set<String> identifiers = new HashSet<String>();

        StringBuilder builder = new StringBuilder();

        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }

    public static String loadJSONFromAsset(Activity activity, String fileName) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String parseJsonToString(Activity activity, String json) {

        String output = "";
        String justIds = "";
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(activity, json));
            JSONArray cardsArray = obj.getJSONArray("cards");

            for(int i = 0; i < cardsArray.length(); i++) {
                JSONObject cardObj = cardsArray.getJSONObject(i);
                int multiverseId = -1;

                // if there isn't a multiverseId skip it.
                if(cardObj.has("multiverseId")) {
                    String id = cardObj.optString("uuid");
                    String layout = cardObj.optString("layout");
                    String name = cardObj.optString("name");
                    String manaCost = cardObj.optString("manaCost");
                    double cmc = cardObj.optDouble("convertedManaCost");
                    String type = cardObj.optString("type");
                    String rarity = cardObj.optString("rarity");
                    String text = cardObj.optString("text");
                    String originalText = cardObj.optString("originalText");
                    String flavor = cardObj.optString("flavorText");
                    String artist = cardObj.optString("artist");
                    String number = cardObj.optString("number");
                    String power = cardObj.optString("power");
                    String toughness = cardObj.optString("toughness");
                    String loyalty = cardObj.optString("loyalty");
                    multiverseId = cardObj.optInt("multiverseId");
                    String border = cardObj.optString("border");
                    String set = obj.optString("mtgCode");
                    String setName = obj.optString("name");

                    String colors = "";
                    String colorIdentity = "]";
                    String supertypes = "";
                    String types = "";
                    String subtypes = "";


                    if(cardObj.has("colors")) {
                        for (int j = 0; j < cardObj.optJSONArray("colors").length(); j++) {
                            colors += "\"" + cardObj.optJSONArray("colors").get(j) + "\",";
                        }

                        colors = MTGUtils.removeLastCharacter(colors);
                   }

                    if(cardObj.has("colorIdentity")) {
                        for (int j = 0; j < cardObj.optJSONArray("colorIdentity").length(); j++) {
                            colorIdentity += "\"" + cardObj.optJSONArray("colorIdentity").get(j) + "\",";
                        }

                        colorIdentity = MTGUtils.removeLastCharacter(colorIdentity);
                    }

                    if(cardObj.has("superTypes")) {
                        for (int j = 0; j < cardObj.optJSONArray("superTypes").length(); j++) {
                            supertypes += "\"" + cardObj.optJSONArray("superTypes").get(j) + "\",";
                        }

                        supertypes = MTGUtils.removeLastCharacter(supertypes);
                    }

                    if(cardObj.has("types")) {
                        for (int j = 0; j < cardObj.optJSONArray("types").length(); j++) {
                            types += "\"" + cardObj.optJSONArray("types").get(j) + "\",";
                        }
                        types = MTGUtils.removeLastCharacter(types);
                    }

                    if(cardObj.has("subtypes")) {
                        for (int j = 0; j < cardObj.optJSONArray("subtypes").length(); j++) {
                            subtypes += "\"" + cardObj.optJSONArray("subtypes").get(j) + "\",";
                        }
                        subtypes = MTGUtils.removeLastCharacter(subtypes);
                    }

//                    justIds += "\"" + multiverseId + "\",";
                    justIds += multiverseId + ",";

                    output += "MagicCard card"+i+" = new MagicCard(\""+
                            id + "\", \"" +
                            layout + "\", " +
                            name + "\", " +
                            null + ", \"" +
                            manaCost + "\", " +
                            cmc + "\", [" +
                            colors + "], [" +
                            colorIdentity + "], \"" +
                            type + "\", [" +
                            supertypes + "], [" +
                            types + "], [" +
                            subtypes + "], \"" +
                            rarity + "\", \"" +
                            text + "\", \"" +
                            originalText + "\", \"" +
                            flavor + "\", \"" +
                            artist + "\", \"" +
                            number + "\", \"" +
                            power + "\", \"" +
                            toughness + "\", \"" +
                            loyalty + "\", \"" +
                            multiverseId + "\", \"" +
                            null + ", " +
                            null + ", " +
                            null + ", \"" +
                            border + "\", " +
                            false + ", " +
                            7 + ", " +
                            7 + ", " +
                            false + ", " +
                            null + ", " +
                            false + ", \"" +
                            set + "\", " +
                            setName + "\", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null + ", " +
                            null +");\n\n";
                }

                if(i % 100 == 0){
                    output += "\n\n****************************************************\n\n\n\n";
                }

            }

        } catch (JSONException e) {
            Timber.tag("fart").w(e);
            e.printStackTrace();
        }

        return justIds;
    }

    public static String removeLastCharacter(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static void cardObjectStuff() {

    }

}
