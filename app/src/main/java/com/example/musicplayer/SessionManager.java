package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "music_player_session";
    private static final String KEY_SESSION = "session";
    private static final String KEY_CURRENT_USER = "current_user";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_SESSION, false);
    }

    public void saveSession(String login) {
        preferences.edit()
                .putBoolean(KEY_SESSION, true)
                .putString(KEY_CURRENT_USER, login)
                .apply();
    }

    public String getCurrentUser() {
        return preferences.getString(KEY_CURRENT_USER, "");
    }

    public void logout() {
        preferences.edit()
                .putBoolean(KEY_SESSION, false)
                .remove(KEY_CURRENT_USER)
                .apply();
    }
}