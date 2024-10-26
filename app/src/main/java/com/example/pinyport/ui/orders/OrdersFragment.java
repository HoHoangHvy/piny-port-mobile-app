package com.example.pinyport.ui.orders;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.databinding.FilterOrderDrawerBinding;
import com.example.pinyport.databinding.FragmentOrdersBinding;
import com.example.pinyport.model.Order;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private List<Order> orderList = new ArrayList<>();  // Sample data
    private ArrayList<String> filterStatus = new ArrayList<>(
            List.of("Wait", "Preparing", "Delivering", "Success", "Cancelled")
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

        // Inflate the layout using View Binding
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        FilterOrderDrawerBinding filterOrderDrawerBinding = FilterOrderDrawerBinding.bind(binding.filterOrderDrawer.getRoot()); // Access the drawer
        View root = binding.getRoot();

        // Initialize RecyclerView using View Binding
        RecyclerView recyclerView = binding.orderList;

        // Sample data - replace this with real data from your ViewModel
        orderList.add(new Order("MM0876", "12/06/2024", "11:30", "Le Thi Loan", "Wait", 2, "170000 VND"));
        orderList.add(new Order("MM0877","14/06/2024", "15:45", "Nguyen Van A", "Preparing", 3, "200000 VND"));
        orderList.add(new Order("MM088","14/06/2024", "15:45", "Nguyen Van B", "Delivering", 3, "200000 VND"));
        orderList.add(new Order("MM0879","14/06/2024", "15:45", "Nguyen Van C", "Success", 3, "200000 VND"));
        orderList.add(new Order("MM0880","14/06/2024", "15:45", "Nguyen Van D", "Cancelled", 3, "200000 VND"));

        // Set up the adapter
        OrderListAdapter adapter = new OrderListAdapter(orderList);
        recyclerView.setAdapter(adapter);

        // Set the LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterOrderDrawerBinding.applyFilterButton;
        Button cancelFilterBUtton = filterOrderDrawerBinding.cancelFilterButton;
        // Open the drawer when Filter button is clicked
        filterButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.END));
        cancelFilterBUtton.setOnClickListener(v -> binding.drawerLayout.closeDrawer(GravityCompat.END));
        // Apply the filter when Apply button is clicked
        applyFilterButton.setOnClickListener(v -> applyFilters(root, binding.drawerLayout));

        initStatusFilter(filterOrderDrawerBinding);

        return root;
    }
    private void initStatusFilter(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        setupCardClickListener(filterOrderDrawerBinding.waitStatusFilter,
                filterOrderDrawerBinding.waitStatusText);
        setupCardClickListener(filterOrderDrawerBinding.cancelledStatusFilter,
                filterOrderDrawerBinding.cancelledStatusText);
        setupCardClickListener(filterOrderDrawerBinding.successStatusFilter,
                filterOrderDrawerBinding.successStatusText);
        setupCardClickListener(filterOrderDrawerBinding.deliveringStatusFilter,
                filterOrderDrawerBinding.deliveringStatusText);
        setupCardClickListener(filterOrderDrawerBinding.preparingStatusFilter,
                filterOrderDrawerBinding.preparingStatusText);
    }

    private void setupCardClickListener(final MaterialCardView card, final TextView textView) {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the checked state
                card.setChecked(!card.isChecked());

                // Change text color based on the checked state
                if (card.isChecked()) {
                    card.setCardBackgroundColor(getResources().getColor(R.color.blue_500)); // Set a selected color
                    textView.setTextColor(Color.WHITE); // Change text color to white
                } else {
                    card.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background)); // Set default color
                    textView.setTextColor(Color.parseColor("#006FFD")); // Change text color back to original
                }
            }
        });
    }

    private void applyFilters(View root, DrawerLayout drawerLayout) {
        // Close the drawer after applying filters
        drawerLayout.closeDrawer(GravityCompat.END);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
