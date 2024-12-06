package com.example.pinyport.service;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private SharedPreferences sharedPreferences;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
