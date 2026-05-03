package com.example.musicplayer;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"login"}, unique = true)}
)
public class AppUser {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String login;
    public String email;
    public String password;
}