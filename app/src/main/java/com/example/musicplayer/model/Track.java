package com.example.musicplayer.model;

public class Track {

    private long id;
    private String title;
    private String artist;
    private String album;
    private String duration;

    public Track(long id, String title, String artist, String album, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }
}