package com.example.pinyport.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

import com.example.pinyport.DTO.GenOtpRequest;
import com.example.pinyport.DTO.GenOtpResponse;
import com.example.pinyport.DTO.LoginOtpRequest;
import com.example.pinyport.DTO.LoginOtpResponse;
import com.example.pinyport.DTO.LoginRequest;
import com.example.pinyport.DTO.LoginResponse;
import com.example.pinyport.model.Customer;
import com.google.gson.JsonObject;

public interface ApiService {
    @GET("customers")
    Call<List<Customer>> getCustomers();

    @POST("customers")
    Call<Customer> createCustomer(@Body Customer customer);

    @POST("auth/login") // Replace with your Laravel API login endpoint
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/auth-otp") // Replace with your Laravel API login endpoint
    Call<JsonObject> verifyOtp(@Body LoginOtpRequest request);

    @POST("auth/gen-otp") // Replace with your Laravel API login endpoint
    Call<GenOtpResponse> genOtp(@Body GenOtpRequest request);
}
