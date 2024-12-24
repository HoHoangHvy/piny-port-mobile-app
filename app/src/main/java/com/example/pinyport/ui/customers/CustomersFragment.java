package com.example.pinyport.ui.customers;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.CustomerListAdapter;
import com.example.pinyport.databinding.FilterCustomerDrawerBinding;
import com.example.pinyport.databinding.FragmentCustomersBinding;
import com.example.pinyport.model.Customer;
import com.example.pinyport.model.Order;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;
    private List<Customer> filteredCustomerList;  // Store the filtered customer list
    private CustomerListAdapter adapter;
    private List<Customer> customerList = new ArrayList<>();  // Sample data

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(requireContext());

        CustomersViewModel customersViewModel = new ViewModelProvider(this).get(CustomersViewModel.class);
        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Customer List
        filteredCustomerList = new ArrayList<>(); // Start with the original list
        adapter = new CustomerListAdapter(filteredCustomerList, this::onCustomerClick);

        // Initialize RecyclerView
        setupRecyclerView();

        // Initialize Filter UI Components
        initFilterUI(root);

        Button createCustomerButton = binding.createCustomerButton;

        createCustomerButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_create_customer);
        });
        try {
            apiService.getCustomers().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject jsonResponse = response.body();
                        // Extract fields dynamically
                        boolean success = jsonResponse.get("success").getAsBoolean();
                        String message = jsonResponse.get("message").getAsString();

                        // Check if the "data" field exists and is not null
                        if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonResponse.getAsJsonArray("data");

                            // Check if the "data" array is empty
                            if (dataArray.size() > 0) {
                                // Clear the list before adding new data
                                filteredCustomerList.clear();

                                // Loop through the data array and add each customer to the list
                                for (JsonElement customerElement : dataArray) {
                                    JsonObject customerObject = customerElement.getAsJsonObject();
                                    Customer customer = new Customer(
                                            customerObject.get("customer_number").getAsString(), // Use "id" from the API response
                                            customerObject.get("full_name").getAsString(),
                                            customerObject.get("phone_number").getAsString(), // Fix the typo
                                            customerObject.get("rank").getAsString(), // Use "rank" from the API response
                                            customerObject.get("date_registered").getAsString()
                                    );
                                    filteredCustomerList.add(customer);
                                }

                                // Notify the adapter of data changes
                                adapter.notifyDataSetChanged();
                            } else {
                                // Handle the case where the "data" array is empty
                                Toast.makeText(requireContext(), "No customers found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle the case where the "data" field is missing or null
                            Toast.makeText(requireContext(), "No data found in the response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch customers", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(requireContext(), "Failed to fetch customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }
    private void setupRecyclerView() {
        adapter.updateCustomerList(filteredCustomerList); // Update the adapter's data
        RecyclerView recyclerView = binding.customerList;
        recyclerView.setAdapter(adapter); // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void onCustomerClick(Customer customer) {
        // Handle customer click, e.g., navigate to customer detail
        Bundle args = new Bundle();
        args.putSerializable("customer", customer); // Assuming Customer implements Serializable
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_customersFragment_to_customerDetailFragment, args);
    }


    private void initFilterUI(View root) {
        FilterCustomerDrawerBinding filterCustomerDrawerBinding = FilterCustomerDrawerBinding.bind(binding.filterCustomerDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterCustomerDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterCustomerDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterCustomerDrawerBinding.clearAllButton; // New clear filters button

        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        cancelFilterButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        applyFilterButton.setOnClickListener(v -> applyFilters(drawerLayout));
        clearAllButton.setOnClickListener(v -> clearAllFilters(filterCustomerDrawerBinding)); // New clear filters logic

        // Initialize any other UI components for filtering, sorting, etc. as necessary
    }

    private void applyFilters(DrawerLayout drawerLayout) {
        // Logic to filter customers based on selected criteria (e.g., status)
        // Close the drawer after applying filters
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    private void clearAllFilters(FilterCustomerDrawerBinding filterCustomerDrawerBinding) {
        // Logic to reset all filters to their default states
    }

    private void showSortDialog() {
        String[] sortOptions = {"Sort by Name", "Sort by Email", "Sort by Status"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortCustomers(which);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sortCustomers(int which) {
        switch (which) {
            case 0:
                filteredCustomerList.sort(Comparator.comparing(Customer::getName));
                break;
            case 1:
                filteredCustomerList.sort(Comparator.comparing(Customer::getPhone));
                break;
            case 2:
                filteredCustomerList.sort(Comparator.comparing(Customer::getRank));
                break;
        }
        adapter.notifyDataSetChanged(); // Notify adapter of data changes
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
