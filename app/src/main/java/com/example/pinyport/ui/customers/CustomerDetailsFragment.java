package com.example.pinyport.ui.customers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;

import java.util.List;

public class CustomerDetailsFragment extends Fragment {

    private TextView levelTextView;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private CustomerDetailsViewModel customerDetailsViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_details, container, false);

        // Initialize views
        levelTextView = view.findViewById(R.id.tvLevel);
        recyclerView = view.findViewById(R.id.orders_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ViewModel
        customerDetailsViewModel = new ViewModelProvider(this).get(CustomerDetailsViewModel.class);

        // Set up RecyclerView adapter
        orderAdapter = new OrderAdapter();
        recyclerView.setAdapter(orderAdapter);

        // Observe level data
        customerDetailsViewModel.getLevel().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String level) {
                updateLevelBackground(level);
            }
        });

        // Observe orders list and update RecyclerView adapter's data
        customerDetailsViewModel.getOrderList().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders); // Update data in the adapter
        });

        return view;
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
                levelTextView.setBackgroundResource(android.R.color.transparent);
                break;
        }
    }
}
