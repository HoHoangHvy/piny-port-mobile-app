package com.example.pinyport.ui.orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.example.pinyport.adapter.CustomerOptionAdapter;
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.databinding.FilterOrderDrawerBinding;
import com.example.pinyport.databinding.FragmentOrdersBinding;
import com.example.pinyport.model.CustomerOption;
import com.example.pinyport.model.Order;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.PermissionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.RangeSlider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredOrderList = new ArrayList<>();
    private OrderListAdapter adapter;

    private Set<String> selectedStatuses = new HashSet<>(); // Store selected statuses
    private String selectedDate = ""; // Default: No date filter
    private double minPrice = 0.0; // Default minimum price
    private double maxPrice = 1000000.0; // Default maximum price
    private String selectedCustomerId = ""; // Default: No customer filter
    private String selectedCustomerName = ""; // Default: No customer filter
    private Comparator<Order> currentComparator = null; // Store current sorting comparator

    private List<CustomerOption> customers = new ArrayList<>(); // List to store customer options
    private CustomerOptionAdapter customerAdapter; // Adapter for customer options

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        PermissionManager permissionManager = new PermissionManager(requireContext());

        // Initialize RecyclerView
        setupRecyclerView();

        // Initialize Filter UI Components
        initFilterUI(root);

        // Set up the SearchView
        setupSearchView();

        // Fetch orders from the API
        fetchOrders();

        // Handle create order button visibility based on permissions
        boolean canCreate = permissionManager.hasPermission("orders", "create", "123");
        if (canCreate) {
            binding.createOrderButton.setVisibility(View.VISIBLE);
        } else {
            binding.createOrderButton.setVisibility(View.GONE);
        }

        // Handle create order button click
        binding.createOrderButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_create_order);
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

    private void fetchOrders() {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.getOrders().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                        JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                        if (dataArray.size() > 0) {
                            orderList.clear();
                            // Define the input and output date formats
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                            for (JsonElement orderElement : dataArray) {
                                JsonObject orderObject = orderElement.getAsJsonObject();

                                try {
                                    // Parse the date string into a Date object
                                    Date date = inputDateFormat.parse(orderObject.get("created_at").getAsString());
                                    String formattedDate = outputDateFormat.format(date);
                                    String formattedTime = outputTimeFormat.format(date); // H
                                    Order order = new Order(
                                            orderObject.get("id").getAsString(),
                                            orderObject.get("order_number").getAsString(),
                                            formattedDate,
                                            formattedTime,
                                            orderObject.get("receiver_name").getAsString(),
                                            orderObject.get("order_status").getAsString(),
                                            orderObject.get("custom_fields").getAsJsonObject().get("count_product").getAsInt(),
                                            orderObject.get("order_total").getAsDouble(),
                                            orderObject.get("payment_status").getAsString(),
                                            orderObject.get("host_id").getAsString()
                                    );
                                    orderList.add(order);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            applyFilters(binding.searchView.getQuery().toString());
                        } else {
                            Toast.makeText(requireContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "No data found in the response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch orders: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        filteredOrderList.addAll(orderList); // Initialize with all orders
        adapter = new OrderListAdapter(filteredOrderList, this::onOrderClick);
        RecyclerView recyclerView = binding.orderList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void onOrderClick(Order order) {
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_ordersFragment_to_orderDetailFragment, args);
    }


    // Update the initFilterUI method to include the step Spinner
    private void initFilterUI(View root) {
        FilterOrderDrawerBinding filterOrderDrawerBinding = FilterOrderDrawerBinding.bind(binding.filterOrderDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterOrderDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterOrderDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterOrderDrawerBinding.clearAllButton;

        MaterialCardView waitForApprovalFilter = filterOrderDrawerBinding.waitForApprovalStatusFilter;
        MaterialCardView inProgressFilter = filterOrderDrawerBinding.inProgressStatusFilter;
        MaterialCardView deliveringFilter = filterOrderDrawerBinding.deliveringStatusFilter;
        MaterialCardView deliveredFilter = filterOrderDrawerBinding.deliveredStatusFilter;
        MaterialCardView cancelledFilter = filterOrderDrawerBinding.cancelledStatusFilter;
        MaterialCardView completedFilter = filterOrderDrawerBinding.completedStatusFilter;

        TextView waitForApprovalText = filterOrderDrawerBinding.waitForApprovalStatusText;
        TextView inProgressText = filterOrderDrawerBinding.inProgressStatusText;
        TextView deliveringText = filterOrderDrawerBinding.deliveringStatusText;
        TextView deliveredText = filterOrderDrawerBinding.deliveredStatusText;
        TextView cancelledText = filterOrderDrawerBinding.cancelledStatusText;
        TextView completedText = filterOrderDrawerBinding.completedStatusText;

        Button datePickerButton = filterOrderDrawerBinding.datePickerButton;
        TextView dateTextView = filterOrderDrawerBinding.dateTextView;

        RangeSlider priceRangeSlider = filterOrderDrawerBinding.priceRangeSlider;
        TextView priceRangeText = filterOrderDrawerBinding.priceRangeText;

        Button customerPickerButton = filterOrderDrawerBinding.customerPicker;
        TextView selectedCustomerTextView = filterOrderDrawerBinding.selectedCustomerTextView;

        // Step Spinner
        Spinner stepSpinner = filterOrderDrawerBinding.stepSpinner;
        ArrayAdapter<CharSequence> stepAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.step_values, android.R.layout.simple_spinner_item);
        stepAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stepSpinner.setAdapter(stepAdapter);

        // Set up step Spinner listener
        stepSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStep = parent.getItemAtPosition(position).toString();
                switch (selectedStep) {
                    case "10.000 đ":
                        currentStep = STEP_10K;
                        break;
                    case "100.000 đ":
                        currentStep = STEP_100K;
                        break;
                    case "1.000.000 đ":
                        currentStep = STEP_1M;
                        break;
                }
                updatePriceRangeSlider(priceRangeSlider, priceRangeText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

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
            clearAllFilters(waitForApprovalFilter, inProgressFilter, deliveringFilter, deliveredFilter, cancelledFilter, completedFilter, dateTextView, priceRangeSlider, priceRangeText, selectedCustomerTextView);
            applyFilters(binding.searchView.getQuery().toString());
        });

        // Set up status filter click listeners
        waitForApprovalFilter.setOnClickListener(v -> toggleStatusFilter(waitForApprovalFilter, "Wait for Approval", waitForApprovalText));
        inProgressFilter.setOnClickListener(v -> toggleStatusFilter(inProgressFilter, "In Progress", inProgressText));
        deliveringFilter.setOnClickListener(v -> toggleStatusFilter(deliveringFilter, "Delivering", deliveringText));
        deliveredFilter.setOnClickListener(v -> toggleStatusFilter(deliveredFilter, "Delivered", deliveredText));
        cancelledFilter.setOnClickListener(v -> toggleStatusFilter(cancelledFilter, "Cancelled", cancelledText));
        completedFilter.setOnClickListener(v -> toggleStatusFilter(completedFilter, "Completed", completedText));

        // Set up the date picker
        datePickerButton.setOnClickListener(v -> showDatePickerDialog(dateTextView));

        // Set up the price range slider
        updatePriceRangeSlider(priceRangeSlider, priceRangeText);

        // Set up the customer picker
        customerPickerButton.setOnClickListener(v -> showCustomerPickerDialog(selectedCustomerTextView));

        // Set up sort button
        binding.sortButton.setOnClickListener(v -> showSortDialog());
    }
    // Add these constants for step options
    private static final double STEP_10K = 10000;
    private static final double STEP_100K = 100000;
    private static final double STEP_1M = 1000000;
    private double currentStep = STEP_10K; // Default step size
    // Helper method to update the price range slider
    private void updatePriceRangeSlider(RangeSlider priceRangeSlider, TextView priceRangeText) {
        // Set the slider's valueFrom, valueTo, and stepSize based on the current step
        priceRangeSlider.setValueFrom(0f);
        priceRangeSlider.setValueTo((float) (currentStep * 10)); // Adjust the max value based on the step
        priceRangeSlider.setStepSize((float) currentStep);

        // Set initial values
        priceRangeSlider.setValues(0f, (float) (currentStep * 10));

        // Update the price range text
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            minPrice = slider.getValues().get(0);
            maxPrice = slider.getValues().get(1);
            priceRangeText.setText(String.format(Locale.getDefault(), "Selected Price Range: %s - %s",
                    formatCurrency(minPrice), formatCurrency(maxPrice)));
        });

        // Set initial text
        priceRangeText.setText(String.format(Locale.getDefault(), "Selected Price Range: %s - %s",
                formatCurrency(0), formatCurrency(currentStep * 10)));
    }

    // Helper method to format currency in VND
    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f VND", amount);
    }
    private void toggleStatusFilter(MaterialCardView card, String status, TextView textView) {
        if (!card.isChecked()) {
            selectedStatuses.add(status);
        } else {
            selectedStatuses.remove(status);
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
        filteredOrderList.clear();

        for (Order order : orderList) {
            boolean matchesStatus = selectedStatuses.isEmpty() || selectedStatuses.contains(order.getStatus());
            boolean matchesDate = selectedDate.isEmpty() || order.getDate().equals(selectedDate);
            boolean matchesPrice = order.getValue() >= minPrice && order.getValue() <= maxPrice;
            boolean matchesCustomer = selectedCustomerId.isEmpty() || order.getCustomerId().equals(selectedCustomerId);
            boolean matchesQuery = query.isEmpty() ||
                    order.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                    order.getOrderId().toLowerCase().contains(query.toLowerCase());

            if (matchesStatus && matchesDate && matchesPrice && matchesCustomer && matchesQuery) {
                filteredOrderList.add(order);
            }
        }

        // Apply sorting if needed
        if (currentComparator != null) {
            sortOrders(currentComparator);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void clearAllFilters(MaterialCardView waitForApprovalFilter, MaterialCardView inProgressFilter, MaterialCardView deliveringFilter,
                                 MaterialCardView deliveredFilter, MaterialCardView cancelledFilter, MaterialCardView completedFilter,
                                 TextView dateTextView, RangeSlider priceRangeSlider, TextView priceRangeText, TextView selectedCustomerTextView) {
        // Clear status filters
        waitForApprovalFilter.setChecked(false);
        inProgressFilter.setChecked(false);
        deliveringFilter.setChecked(false);
        deliveredFilter.setChecked(false);
        cancelledFilter.setChecked(false);
        completedFilter.setChecked(false);

        // Reset card background colors
        waitForApprovalFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        inProgressFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        deliveringFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        deliveredFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        cancelledFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        completedFilter.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));

        // Reset text colors to black
        TextView waitForApprovalText = binding.filterOrderDrawer.waitForApprovalStatusText;
        TextView inProgressText = binding.filterOrderDrawer.inProgressStatusText;
        TextView deliveringText = binding.filterOrderDrawer.deliveringStatusText;
        TextView deliveredText = binding.filterOrderDrawer.deliveredStatusText;
        TextView cancelledText = binding.filterOrderDrawer.cancelledStatusText;
        TextView completedText = binding.filterOrderDrawer.completedStatusText;

        waitForApprovalText.setTextColor(Color.parseColor("#006FFD"));
        inProgressText.setTextColor(Color.parseColor("#006FFD"));
        deliveringText.setTextColor(Color.parseColor("#006FFD"));
        deliveredText.setTextColor(Color.parseColor("#006FFD"));
        cancelledText.setTextColor(Color.parseColor("#006FFD"));
        completedText.setTextColor(Color.parseColor("#006FFD"));

        selectedStatuses.clear();

        // Clear date filter
        selectedDate = "";
        dateTextView.setText("Selected Date will appear here");

        // Reset price range slider and text
        priceRangeSlider.setValues(0f, (float) (currentStep * 10));
        priceRangeText.setText("Selected Price Range: " + formatCurrency(0) + " - " + formatCurrency(currentStep * 10));

        // Clear customer filter
        selectedCustomerId = "";
        selectedCustomerName = "";
        selectedCustomerTextView.setText("Selected Customer: None");

        // Reset filtered list to original list
        filteredOrderList.clear();
        filteredOrderList.addAll(orderList);

        // Reapply sorting if needed
        if (currentComparator != null) {
            sortOrders(currentComparator);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showDatePickerDialog(TextView dateTextView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            dateTextView.setText(selectedDate);
        }, 2023, 0, 1);

        datePickerDialog.show();
    }

    private void showCustomerPickerDialog(TextView selectedCustomerTextView) {
        // Fetch customer options from the API
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.getCustomerOptions().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    customers.clear();
                    if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                        JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                        for (JsonElement customerElement : dataArray) {
                            JsonObject customerObject = customerElement.getAsJsonObject();
                            CustomerOption customer = new CustomerOption(
                                    customerObject.get("id").getAsString(),
                                    customerObject.get("full_name").getAsString(),
                                    customerObject.get("phone_number").getAsString()
                            );
                            customers.add(customer);
                        }

                        // Show customer selection dialog
                        String[] customerNames = new String[customers.size()];
                        for (int i = 0; i < customers.size(); i++) {
                            customerNames[i] = customers.get(i).getName();
                        }

                        new AlertDialog.Builder(requireContext())
                                .setTitle("Select Customer")
                                .setItems(customerNames, (dialog, which) -> {
                                    CustomerOption selectedCustomer = customers.get(which);
                                    selectedCustomerId = selectedCustomer.getId();
                                    selectedCustomerName = selectedCustomer.getName();
                                    selectedCustomerTextView.setText("Selected Customer: " + selectedCustomerName);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load customers.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSortDialog() {
        String[] sortOptions = {"Sort by Date (Newest First)", "Sort by Date (Oldest First)", "Sort by Price (High to Low)", "Sort by Price (Low to High)"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Sort Orders")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sortOrders(Comparator.comparing(Order::getDate).reversed());
                            break;
                        case 1:
                            sortOrders(Comparator.comparing(Order::getDate));
                            break;
                        case 2:
                            sortOrders(Comparator.comparing(Order::getValue).reversed());
                            break;
                        case 3:
                            sortOrders(Comparator.comparing(Order::getValue));
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sortOrders(Comparator<Order> comparator) {
        currentComparator = comparator;
        Collections.sort(filteredOrderList, comparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}