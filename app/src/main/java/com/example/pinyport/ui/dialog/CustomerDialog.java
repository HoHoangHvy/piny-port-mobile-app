package com.example.pinyport.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.CustomerAdapter;
import com.example.pinyport.model.Customer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CustomerDialog {

    public static void showDialog(FragmentActivity activity, List<Customer> customers, TextView selectedCustomerTextView) {
        // Inflate the dialog layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customer_dialog, null);

        // Setup RecyclerView
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // Create the dialog
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity)
                .setView(dialogView)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();

        // Create and pass the dialog instance
        CustomerAdapter customerAdapter = new CustomerAdapter(customers, selectedCustomerTextView, dialog);
        recyclerView.setAdapter(customerAdapter);

        // Show the dialog after the adapter is set
        dialog.show();
    }
}