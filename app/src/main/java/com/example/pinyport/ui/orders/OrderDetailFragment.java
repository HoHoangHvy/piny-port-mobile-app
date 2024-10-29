package com.example.pinyport.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pinyport.R;
import com.example.pinyport.model.Order;

public class OrderDetailFragment extends Fragment {

    private Order order; // Declare an Order object

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the order object from arguments
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if order is not null before trying to use it
        if (order != null) {
            // Handle the case where the order is not null
            // You can use the order object here
        } else {
            // Handle the case where the order is null
            // You might want to show a message or handle it as needed
        }
    }
}
