package com.example.musicplayer;

import android.util.Patterns;

public class Validation {

    public static boolean isLoginValid(String login) {
        return login != null && login.trim().length() >= 3;
    }

    public static boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}