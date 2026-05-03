package com.example.musicplayer.network;

import com.example.musicplayer.model.DeezerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeezerApi {

    @GET("search")
    Call<DeezerResponse> searchTracks(@Query("q") String query);
}