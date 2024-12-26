package com.example.pinyport.ui.customers;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.pinyport.databinding.FilterOrderDrawerBinding;
import com.example.pinyport.databinding.FragmentCustomersBinding;
import com.example.pinyport.model.Customer;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;
    private List<Customer> customerList = new ArrayList<>();  // Original customer list
    private List<Customer> filteredCustomerList = new ArrayList<>();  // Filtered customer list
    private CustomerListAdapter adapter;

    private Set<String> selectedRanks = new HashSet<>(); // Store selected ranks
    private String selectedDate = ""; // Default: No date filter

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(requireContext());

        CustomersViewModel customersViewModel = new ViewModelProvider(this).get(CustomersViewModel.class);
        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        setupRecyclerView();

        // Initialize Filter UI Components
        initFilterUI(root);

        // Set up the SearchView
        setupSearchView();

        // Fetch customers from the API
        fetchCustomers(apiService);

        // Handle create customer button click
        FloatingActionButton createCustomerButton = binding.createCustomerButton;
        createCustomerButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_create_customer);
        });

        return root;
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText);
                return true;
            }
        });
    }
    private void fetchCustomers(ApiService apiService) {
        try {
            apiService.getCustomers().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject jsonResponse = response.body();
                        if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                            if (dataArray.size() > 0) {
                                customerList.clear();
                                for (JsonElement customerElement : dataArray) {
                                    JsonObject customerObject = customerElement.getAsJsonObject();
                                    Customer customer = new Customer(
                                            customerObject.get("id").getAsString(),
                                            customerObject.get("customer_number").getAsString(),
                                            customerObject.get("full_name").getAsString(),
                                            customerObject.get("phone_number").getAsString(),
                                            customerObject.get("rank").getAsString(),
                                            formatDate(customerObject.get("date_registered").getAsString()),
                                            customerObject.get("total_point").getAsInt(),
                                            customerObject.get("total_spent").getAsFloat()
                                    );
                                    customerList.add(customer);
                                }
                                applyFilters(binding.searchView.getQuery().toString());
                            } else {
                                Toast.makeText(requireContext(), "No customers found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
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
    }

    private void setupRecyclerView() {
        adapter = new CustomerListAdapter(filteredCustomerList, this::onCustomerClick);
        RecyclerView recyclerView = binding.customerList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void onCustomerClick(Customer customer) {
        Bundle args = new Bundle();
        args.putSerializable("customer", customer);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_customersFragment_to_customerDetailFragment, args);
    }

    private void initFilterUI(View root) {
        FilterCustomerDrawerBinding filterCustomerDrawerBinding = FilterCustomerDrawerBinding.bind(binding.filterCustomerDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterCustomerDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterCustomerDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterCustomerDrawerBinding.clearAllButton;

        MaterialCardView goldFilter = filterCustomerDrawerBinding.goldFilter;
        MaterialCardView silverFilter = filterCustomerDrawerBinding.silverFilter;
        MaterialCardView bronzeFilter = filterCustomerDrawerBinding.bronzeFilter;
        MaterialCardView diamondFilter = filterCustomerDrawerBinding.diamondFilter;

        TextView goldText= filterCustomerDrawerBinding.goldText;
        TextView silverText= filterCustomerDrawerBinding.silverText;
        TextView bronzeText= filterCustomerDrawerBinding.bronzeText;
        TextView diamondText= filterCustomerDrawerBinding.diamondText;


        Button datePickerButton = filterCustomerDrawerBinding.datePickerButton;
        TextView dateTextView = filterCustomerDrawerBinding.dateTextView;

        // Open the filter drawer
        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Close the filter drawer
        cancelFilterButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        // Apply filters
        applyFilterButton.setOnClickListener(v -> {
            applyFilters(binding.searchView.getQuery().toString());
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        // Clear all filters
        clearAllButton.setOnClickListener(v -> {
            clearAllFilters(goldFilter, silverFilter, bronzeFilter, diamondFilter, dateTextView);
            applyFilters(binding.searchView.getQuery().toString());
        });

        // Set up rank filter click listeners
        goldFilter.setOnClickListener(v -> toggleRankFilter(goldFilter, "Gold", goldText));
        silverFilter.setOnClickListener(v -> toggleRankFilter(silverFilter, "Silver", silverText));
        bronzeFilter.setOnClickListener(v -> toggleRankFilter(bronzeFilter, "Bronze", bronzeText));
        diamondFilter.setOnClickListener(v -> toggleRankFilter(diamondFilter, "Diamond", diamondText));

        // Set up the date picker
        datePickerButton.setOnClickListener(v -> showDatePickerDialog(dateTextView));
    }

    private void toggleRankFilter(MaterialCardView card, String rank, TextView textView) {
        if (!card.isChecked()) {
            selectedRanks.add(rank);
        } else {
            selectedRanks.remove(rank);
        }
        card.setChecked(!card.isChecked());
        if (card.isChecked()) {
            card.setCardBackgroundColor(getResources().getColor(R.color.blue_500));
            textView.setTextColor(Color.WHITE);
        } else {
            card.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
            textView.setTextColor(Color.parseColor("#006FFD"));
        }
    }

    private void applyFilters(String query) {
        filteredCustomerList.clear();

        if (query.isEmpty() && selectedRanks.isEmpty() && selectedDate.isEmpty()) {
            filteredCustomerList.addAll(customerList);
        } else {
            for (Customer customer : customerList) {
                boolean matchesRank = selectedRanks.isEmpty() || selectedRanks.contains(customer.getRank());
                boolean matchesDate = selectedDate.isEmpty() || customer.getDateRegistered().equals(selectedDate);

                if ((customer.getName().toLowerCase().contains(query.toLowerCase()) ||
                        customer.getPhone().toLowerCase().contains(query.toLowerCase()) ||
                        customer.getRank().toLowerCase().contains(query.toLowerCase()))
                        && matchesRank && matchesDate) {
                    filteredCustomerList.add(customer);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    private void clearAllFilters(MaterialCardView goldFilter, MaterialCardView silverFilter, MaterialCardView bronzeFilter, MaterialCardView diamondFilter, TextView dateTextView) {
        // Clear rank filters
        goldFilter.setChecked(false);
        silverFilter.setChecked(false);
        bronzeFilter.setChecked(false);
        diamondFilter.setChecked(false);
        selectedRanks.clear();

        // Clear date filter
        selectedDate = "";
        dateTextView.setText("Selected Date will appear here");
    }

    private void showDatePickerDialog(TextView dateTextView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            dateTextView.setText(selectedDate);
        }, 2023, 0, 1);

        datePickerDialog.show();
    }

    public String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}