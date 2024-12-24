package com.example.pinyport.ui.customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pinyport.DTO.LoginOtpRequest;
import com.example.pinyport.LoginActivity;
import com.example.pinyport.databinding.FragmentCreateCustomerBinding;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCustomerFragment extends Fragment {
    private FragmentCreateCustomerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateCustomerBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
