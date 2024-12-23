package com.example.pinyport.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SharedPrefsManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //Save user
    public void saveUser(String user) {
        editor.putString(KEY_USER, user);
        editor.apply();
    }

    //Get user (parse JSON string to User object)
    public JsonObject getUser() {
        String userJsonString = sharedPreferences.getString(KEY_USER, null);
        if (userJsonString != null) {
            return JsonParser.parseString(userJsonString).getAsJsonObject();
        }
        return null;
    }

    // Save user ID
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    // Save token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Save login status
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Get user ID
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Get token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Clear all data (logout)
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
