package com.example.pinyport.ui.orders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.databinding.FilterOrderDrawerBinding;
import com.example.pinyport.databinding.FragmentOrdersBinding;
import com.example.pinyport.model.Order;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.PermissionManager;
import com.google.android.material.card.MaterialCardView;
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
        boolean canCreate = permissionManager.hasPermission("orders", "create", "123");
        if(canCreate) {
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
                                            orderObject.get("payment_status").getAsString()
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
    private void setupRecyclerView() {
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

    private void initFilterUI(View root) {
        FilterOrderDrawerBinding filterOrderDrawerBinding = FilterOrderDrawerBinding.bind(binding.filterOrderDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterOrderDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterOrderDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterOrderDrawerBinding.clearAllButton;

        MaterialCardView waitFilter = filterOrderDrawerBinding.waitStatusFilter;
        MaterialCardView preparingFilter = filterOrderDrawerBinding.preparingStatusFilter;
        MaterialCardView deliveringFilter = filterOrderDrawerBinding.deliveringStatusFilter;
        MaterialCardView successFilter = filterOrderDrawerBinding.successStatusFilter;
        MaterialCardView cancelledFilter = filterOrderDrawerBinding.cancelledStatusFilter;

        TextView waitText = filterOrderDrawerBinding.waitStatusText;
        TextView preparingText = filterOrderDrawerBinding.preparingStatusText;
        TextView deliveringText = filterOrderDrawerBinding.deliveringStatusText;
        TextView successText = filterOrderDrawerBinding.successStatusText;
        TextView cancelledText = filterOrderDrawerBinding.cancelledStatusText;

        Button datePickerButton = filterOrderDrawerBinding.datePickerButton;
        TextView dateTextView = filterOrderDrawerBinding.dateTextView;

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
            clearAllFilters(waitFilter, preparingFilter, deliveringFilter, successFilter, cancelledFilter, dateTextView);
            applyFilters(binding.searchView.getQuery().toString());
        });

        // Set up status filter click listeners
        waitFilter.setOnClickListener(v -> toggleStatusFilter(waitFilter, "Wait", waitText));
        preparingFilter.setOnClickListener(v -> toggleStatusFilter(preparingFilter, "Preparing", preparingText));
        deliveringFilter.setOnClickListener(v -> toggleStatusFilter(deliveringFilter, "Delivering", deliveringText));
        successFilter.setOnClickListener(v -> toggleStatusFilter(successFilter, "Success", successText));
        cancelledFilter.setOnClickListener(v -> toggleStatusFilter(cancelledFilter, "Cancelled", cancelledText));

        // Set up the date picker
        datePickerButton.setOnClickListener(v -> showDatePickerDialog(dateTextView));

        // Set up sort button
        binding.sortButton.setOnClickListener(v -> showSortDialog());
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

        if (query.isEmpty() && selectedStatuses.isEmpty() && selectedDate.isEmpty()) {
            filteredOrderList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                boolean matchesStatus = selectedStatuses.isEmpty() || selectedStatuses.contains(order.getStatus());
                boolean matchesDate = selectedDate.isEmpty() || order.getDate().equals(selectedDate);

                if ((order.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                        order.getOrderId().toLowerCase().contains(query.toLowerCase()))
                        && matchesStatus && matchesDate) {
                    filteredOrderList.add(order);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void clearAllFilters(MaterialCardView waitFilter, MaterialCardView preparingFilter, MaterialCardView deliveringFilter,
                                 MaterialCardView successFilter, MaterialCardView cancelledFilter, TextView dateTextView) {
        // Clear status filters
        waitFilter.setChecked(false);
        preparingFilter.setChecked(false);
        deliveringFilter.setChecked(false);
        successFilter.setChecked(false);
        cancelledFilter.setChecked(false);
        selectedStatuses.clear();

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
        Collections.sort(filteredOrderList, comparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}