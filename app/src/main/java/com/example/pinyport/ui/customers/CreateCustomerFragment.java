package com.example.pinyport.ui.customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pinyport.DTO.CustomerRequest;
import com.example.pinyport.databinding.FragmentCreateCustomerBinding;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCustomerFragment extends Fragment {
    private FragmentCreateCustomerBinding binding;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateCustomerBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        setupGenderSpinner();

        // Set click listener for the create customer button
        binding.buttonCreateCustomer.setOnClickListener(v -> createCustomer());

        return binding.getRoot();
    }

    private void createCustomer() {
        // Collect data from the UI
        String name = binding.nameInput.getText().toString().trim();
        String phone = binding.phoneNumberInput.getText().toString().trim();
        String gender = binding.genderSpinner.getSelectedItem().toString();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a CustomerRequest object
        CustomerRequest customerRequest = new CustomerRequest(name, phone, gender);

        // Make the API call
        Call<JsonObject> call = apiService.createCustomer(customerRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Customer created successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    // Handle validation errors or other errors
                    if (response.code() == 422) {
                        // Handle validation errors
                        Toast.makeText(getContext(), "Validation error: " + "The phone number is already existed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to create customer: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupGenderSpinner() {
        // Define gender options
        String[] genders = {"Male", "Female"};

        // Create an ArrayAdapter using the gender array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        binding.genderSpinner.setAdapter(adapter);
    }

    private void clearForm() {
        // Clear the input fields after successful creation
        binding.nameInput.setText("");
        binding.phoneNumberInput.setText("");
        binding.genderSpinner.setSelection(0); // Reset the Spinner to the first item
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}