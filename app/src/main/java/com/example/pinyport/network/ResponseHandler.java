package com.example.pinyport.network;

import retrofit2.Response;

public class ResponseHandler {
    public static <T> T handleResponse(Response<T> response) throws Exception {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new Exception(response.errorBody().string());
        }
    }
}
