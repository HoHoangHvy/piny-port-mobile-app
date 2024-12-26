package com.example.pinyport.ui.customers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.model.Customer;
import com.example.pinyport.model.Order;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CustomerDetailFragment extends Fragment {

    private TextView levelTextView;
    private TextView customerIdTextView;
    private TextView customerNameTextView;
    private TextView customerPhoneTextView;
    private TextView customerBirthdayTextView;
    private TextView customerStartDateTextView;
    private TextView customerPointTextView;
    private TextView customerTotalSpendingTextView;
    private RecyclerView recyclerView;
    private OrderListAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>(); // Initialize the list
    private CustomerDetailsViewModel customerDetailsViewModel;

    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_details, container, false);

        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        // Initialize TextViews
        levelTextView = view.findViewById(R.id.tvLevel);
        customerIdTextView = view.findViewById(R.id.tvCustomerId);
        customerNameTextView = view.findViewById(R.id.tvCustomerName);
        customerPhoneTextView = view.findViewById(R.id.tvCustomerPhone);
        customerStartDateTextView = view.findViewById(R.id.tvCustomerStartDate);
        customerPointTextView = view.findViewById(R.id.tvCustomerPoint);
        customerTotalSpendingTextView = view.findViewById(R.id.tvTotalSpending);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up RecyclerView
        setupRecyclerView();

        // Get arguments passed from CustomerFragment
        Bundle args = getArguments();
        if (args != null) {
            Customer customer = (Customer) args.getSerializable("customer");

            if (customer == null) {
                return view;
            }
            customerIdTextView.setText(customer.getCustomerNumber());
            customerNameTextView.setText(customer.getName());
            customerPhoneTextView.setText(customer.getPhone());
            customerStartDateTextView.setText(customer.getDateRegistered());
            levelTextView.setText(customer.getRank());
            customerPointTextView.setText(customer.getPointText());
            customerTotalSpendingTextView.setText(customer.getTotalSpendingText());
            this.getOrderHistory(customer.getId());
        }

        // Initialize ViewModel
        customerDetailsViewModel = new ViewModelProvider(this).get(CustomerDetailsViewModel.class);

        // Observe LiveData from ViewModel
        customerDetailsViewModel.getLevel().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String level) {
                // Update background based on level change
                updateLevelBackground(level);
            }
        });

        return view;
    }

    private void getOrderHistory(String customerId) {
        apiService.getOrderHistory(customerId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("data");

                    // Clear the existing list
                    orderList.clear();

                    // Define the input and output date formats
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject orderObject = jsonArray.get(i).getAsJsonObject();
                        try {
                            // Parse the date string into a Date object
                            Date date = inputDateFormat.parse(orderObject.get("date_created").getAsString());

                            // Format the date into the desired output format
                            String formattedDate = outputDateFormat.format(date);
                            String formattedTime = outputTimeFormat.format(date); // HH:mm:ss

                            // Create an Order object with the formatted date
                            Order order = new Order(
                                    orderObject.get("order_number").getAsString(),
                                    formattedDate, // Use the formatted date for display
                                    formattedTime, // Convert timestamp to string
                                    orderObject.get("receiver_name").getAsString(),
                                    orderObject.get("status").getAsString(),
                                    orderObject.get("count_product").getAsInt(),
                                    orderObject.get("total_price").getAsString()
                            );
                            orderList.add(order);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Handle the exception (e.g., log the error or skip this order)
                        }
                    }
                    // Notify the adapter of data changes
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderListAdapter(orderList, this::onOrderClick);
        recyclerView.setAdapter(orderAdapter);
    }

    public void onOrderClick(Order order) {
        Bundle args = new Bundle();
        args.putSerializable("order", order);

        NavController navController = Navigation.findNavController(requireView()); // Use requireView() instead of requireActivity()
        navController.navigate(R.id.action_customerDetailFragment_to_orderDetailFragment, args);
    }

    public void updateLevelBackground(String level) {
        switch (level) {
            case "Bronze":
                levelTextView.setBackgroundResource(R.drawable.bronze_background);
                break;
            case "Silver":
                levelTextView.setBackgroundResource(R.drawable.silver_background);
                break;
            case "Gold":
                levelTextView.setBackgroundResource(R.drawable.gold_background);
                break;
            case "Diamond":
                levelTextView.setBackgroundResource(R.drawable.diamond_background);
                break;
            default:
                levelTextView.setBackgroundResource(android.R.color.transparent); // Default if level doesn't match
                break;
        }
    }
}