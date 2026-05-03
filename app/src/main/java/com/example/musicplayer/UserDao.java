package com.example.musicplayer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    AppUser getByLogin(String login);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    AppUser getByEmail(String email);

    @Insert
    void insert(AppUser user);
}