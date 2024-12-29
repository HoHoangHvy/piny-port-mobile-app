package com.example.pinyport.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.pinyport.DTO.CreateOrderRequest;
import com.example.pinyport.DTO.CustomerRequest;
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
    Call<JsonObject> getCustomers();

    @GET("orders")
    Call<JsonObject> getOrders();
    @POST("orders")
    Call<Customer> createOrders(@Body CreateOrderRequest request);
    @GET("loadOrderDetail/{orderId}")
    Call<JsonObject> getOrderDetails(@Path("orderId") String orderId);
    @GET("loadCustomerOrdersHistory")
    Call<JsonObject> getOrderHistory(@Query("customerId") String customerId);
    @POST("customers")
    Call<Customer> createCustomer(@Body Customer customer);

    @POST("auth/login") // Replace with your Laravel API login endpoint
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/auth-otp") // Replace with your Laravel API login endpoint
    Call<JsonObject> verifyOtp(@Body LoginOtpRequest request);

    @POST("auth/gen-otp") // Replace with your Laravel API login endpoint
    Call<GenOtpResponse> genOtp(@Body GenOtpRequest request);

    @POST("customers") // Replace with your actual endpoint
    Call<JsonObject> createCustomer(@Body CustomerRequest customerRequest);
}
