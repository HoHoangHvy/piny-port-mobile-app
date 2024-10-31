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

import java.util.ArrayList;
import java.util.List;

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
    private List<Order> orderList; // List to hold order data
    private CustomerDetailsViewModel customerDetailsViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_details, container, false);

        // Initialize TextViews
        levelTextView = view.findViewById(R.id.tvLevel);
        customerIdTextView = view.findViewById(R.id.tvCustomerId);
        customerNameTextView = view.findViewById(R.id.tvCustomerName);
        customerPhoneTextView = view.findViewById(R.id.tvCustomerPhone);
        customerBirthdayTextView = view.findViewById(R.id.tvCustomerBirthday);
        customerStartDateTextView = view.findViewById(R.id.tvCustomerStartDate);
        customerPointTextView = view.findViewById(R.id.tvCustomerPoint);
        customerTotalSpendingTextView = view.findViewById(R.id.tvTotalSpending);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Get arguments passed from CustomerFragment
        Bundle args = getArguments();
        if (args != null) {
            Customer customer = (Customer) args.getSerializable("customer");

            // Set TextViews with received data
            customerIdTextView.setText(customer.getId());
            customerNameTextView.setText(customer.getName());
            customerPhoneTextView.setText(customer.getPhone());
//            customerBirthdayTextView.setText(customer.getBirthday());
//            customerStartDateTextView.setText(customer.getStartDate());
            levelTextView.setText(customer.getRank());
//            customerPointTextView.setText(customer.getPoint());
//            customerTotalSpendingTextView.setText(customer.getTotalSpending());
//            customer.loadOrderHistory();
            orderList = customer.getOrderHistory();
            setupRecyclerView(orderList);
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

    private void setupRecyclerView(List<Order> orders) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderListAdapter(orders, this::onOrderClick);
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
