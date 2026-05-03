package com.example.musicplayer.repository;

import android.content.Context;

import com.example.musicplayer.R;
import com.example.musicplayer.model.DeezerResponse;
import com.example.musicplayer.model.DeezerTrack;
import com.example.musicplayer.model.Track;
import com.example.musicplayer.network.NetworkClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicRepository {

    private final Context context;

    public MusicRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public interface TracksCallback {
        void onSuccess(ArrayList<Track> tracks);

        void onError(String message, ArrayList<Track> fallbackTracks);
    }

    public void searchTracks(String query, TracksCallback callback) {
        NetworkClient.getApi().searchTracks(query).enqueue(new Callback<DeezerResponse>() {
            @Override
            public void onResponse(Call<DeezerResponse> call, Response<DeezerResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getData() == null) {
                    callback.onError("Ошибка ответа сервера", loadLocalTracks());
                    return;
                }

                ArrayList<Track> result = new ArrayList<>();

                for (DeezerTrack deezerTrack : response.body().getData()) {
                    Track track = new Track(
                            deezerTrack.getId(),
                            deezerTrack.getTitle(),
                            deezerTrack.getArtistName(),
                            deezerTrack.getAlbumTitle(),
                            formatDuration(deezerTrack.getDuration())
                    );

                    result.add(track);
                }

                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Call<DeezerResponse> call, Throwable t) {
                callback.onError("Ошибка сети. Загружены локальные данные", loadLocalTracks());
            }
        });
    }

    public ArrayList<Track> loadLocalTracks() {
        ArrayList<Track> result = new ArrayList<>();

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.tracks);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject root = new JSONObject(json);
            JSONArray jsonTracks = root.getJSONArray("tracks");

            for (int i = 0; i < jsonTracks.length(); i++) {
                JSONObject item = jsonTracks.getJSONObject(i);

                Track track = new Track(
                        item.getLong("id"),
                        item.getString("title"),
                        item.getString("artist"),
                        item.getString("album"),
                        item.getString("duration")
                );

                result.add(track);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (remainingSeconds < 10) {
            return minutes + ":0" + remainingSeconds;
        }

        return minutes + ":" + remainingSeconds;
    }
}