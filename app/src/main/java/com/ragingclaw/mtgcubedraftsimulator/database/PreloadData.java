package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonObject;
import com.ragingclaw.mtgcubedraftsimulator.models.MagicCardViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class PreloadData {
    // String json = PreloadData.loadFile("cardIds.json");

    public static String loadFile(InputStream is) {
        String json = null;
        try {

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
}
