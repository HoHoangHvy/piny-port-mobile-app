package com.example.pinyport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.model.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private List<Customer> customers; // List of customers
    private TextView selectedCustomerTextView; // TextView to update selected customer
    private AlertDialog dialog;

    // Constructor
    public CustomerAdapter(List<Customer> customers, TextView selectedCustomerTextView, AlertDialog dialog) {
        this.customers = customers;
        this.selectedCustomerTextView = selectedCustomerTextView;
        this.dialog = dialog; // Store the dialog reference
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false); // Inflate customer item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.customerNameTextView.setText(customer.getName());
        holder.customerEmailTextView.setText(customer.getEmail());
        holder.customerRankTextView.setText(customer.getRank());

        // Set onClickListener to update the selected customer and dismiss the dialog
        holder.itemView.setOnClickListener(v -> {
            selectedCustomerTextView.setText("Customer: " + customer.getName());
            dialog.dismiss(); // Dismiss the dialog
        });
    }

    @Override
    public int getItemCount() {
        return customers.size(); // Return the size of the customer list
    }

    // ViewHolder class to hold customer views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView customerEmailTextView;
        TextView customerRankTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            customerEmailTextView = itemView.findViewById(R.id.customerEmailTextView);
            customerRankTextView = itemView.findViewById(R.id.customerRankTextView);
        }
    }
}
