package com.example.musicplayer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Track;
import com.example.musicplayer.repository.MusicRepository;

import java.util.ArrayList;

public class MusicViewModel extends AndroidViewModel {

    private final MusicRepository repository;

    private final MutableLiveData<ArrayList<Track>> tracksLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Track> selectedTrackLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> playButtonTextLiveData = new MutableLiveData<>("Play");

    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    public MusicViewModel(@NonNull Application application) {
        super(application);
        repository = new MusicRepository(application);
    }

    public LiveData<ArrayList<Track>> getTracksLiveData() {
        return tracksLiveData;
    }

    public LiveData<Track> getSelectedTrackLiveData() {
        return selectedTrackLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public LiveData<String> getPlayButtonTextLiveData() {
        return playButtonTextLiveData;
    }

    public void loadTracks(String query) {
        loadingLiveData.setValue(true);

        repository.searchTracks(query, new MusicRepository.TracksCallback() {
            @Override
            public void onSuccess(ArrayList<Track> tracks) {
                loadingLiveData.setValue(false);
                tracksLiveData.setValue(tracks);

                if (!tracks.isEmpty()) {
                    selectTrack(0);
                } else {
                    messageLiveData.setValue("Треки не найдены");
                }
            }

            @Override
            public void onError(String message, ArrayList<Track> fallbackTracks) {
                loadingLiveData.setValue(false);
                messageLiveData.setValue(message);
                tracksLiveData.setValue(fallbackTracks);

                if (!fallbackTracks.isEmpty()) {
                    selectTrack(0);
                }
            }
        });
    }

    public void selectTrack(int index) {
        ArrayList<Track> tracks = tracksLiveData.getValue();

        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        if (index < 0 || index >= tracks.size()) {
            return;
        }

        currentTrackIndex = index;
        selectedTrackLiveData.setValue(tracks.get(index));
        messageLiveData.setValue("Выбран трек: " + tracks.get(index).getTitle());
    }

    public void nextTrack() {
        ArrayList<Track> tracks = tracksLiveData.getValue();

        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        int newIndex = currentTrackIndex + 1;

        if (newIndex >= tracks.size()) {
            newIndex = 0;
        }

        selectTrack(newIndex);
    }

    public void previousTrack() {
        ArrayList<Track> tracks = tracksLiveData.getValue();

        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        int newIndex = currentTrackIndex - 1;

        if (newIndex < 0) {
            newIndex = tracks.size() - 1;
        }

        selectTrack(newIndex);
    }

    public void togglePlay() {
        isPlaying = !isPlaying;

        if (isPlaying) {
            playButtonTextLiveData.setValue("Pause");
            messageLiveData.setValue("Воспроизведение");
        } else {
            playButtonTextLiveData.setValue("Play");
            messageLiveData.setValue("Пауза");
        }
    }
}