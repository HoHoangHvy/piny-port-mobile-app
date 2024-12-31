package com.example.pinyport.network;

import android.content.Context;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private SharedPrefsManager sharedPrefsManager;

    public AuthInterceptor(Context context) {
        sharedPrefsManager = new SharedPrefsManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Get the token from SharedPreferences
        String token = sharedPrefsManager.getToken();

        if (token != null) {
            // Add the token to the request headers
            Request newRequest = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token) // Add "Bearer" prefix if required
                    .build();
            return chain.proceed(newRequest);
        }

        // If no token, proceed with the original request
        return chain.proceed(originalRequest);
    }
}