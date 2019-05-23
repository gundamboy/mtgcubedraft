package com.ragingclaw.mtgcubedraftsimulator.Network;

import com.ragingclaw.mtgcubedraftsimulator.utils.AllMyConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(AllMyConstants.MTG_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
