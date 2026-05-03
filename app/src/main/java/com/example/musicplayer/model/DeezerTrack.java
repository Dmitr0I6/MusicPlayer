package com.example.musicplayer.model;

public class DeezerTrack {

    private long id;
    private String title;
    private int duration;
    private DeezerArtist artist;
    private DeezerAlbum album;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getArtistName() {
        if (artist == null) {
            return "Неизвестный исполнитель";
        }

        return artist.getName();
    }

    public String getAlbumTitle() {
        if (album == null) {
            return "Неизвестный альбом";
        }

        return album.getTitle();
    }
}