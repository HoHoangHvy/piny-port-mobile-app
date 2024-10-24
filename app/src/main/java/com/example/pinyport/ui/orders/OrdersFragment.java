package com.example.pinyport.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.databinding.FragmentOrdersBinding;
import com.example.pinyport.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private List<Order> orderList = new ArrayList<>();  // Sample data

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

        // Inflate the layout using View Binding
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView using View Binding
        RecyclerView recyclerView = binding.orderList;

        // Sample data - replace this with real data from your ViewModel
        orderList.add(new Order("MM0876", "12/06/2024", "11:30", "Le Thi Loan", "Success", 2, "170000 VND"));
        orderList.add(new Order("MM0877","14/06/2024", "15:45", "Nguyen Van A", "Pending", 3, "200000 VND"));

        // Set up the adapter
        OrderListAdapter adapter = new OrderListAdapter(orderList);
        recyclerView.setAdapter(adapter);

        // Set the LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
