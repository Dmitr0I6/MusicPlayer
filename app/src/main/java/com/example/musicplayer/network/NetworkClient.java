package com.example.musicplayer.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    private static final String BASE_URL = "https://api.deezer.com/";

    private static Retrofit retrofit;

    public static DeezerApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(DeezerApi.class);
    }
}