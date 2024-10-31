package com.example.pinyport.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.model.Customer;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerViewHolder> {
    private List<Customer> customerList;
    private OnCustomerClickListener customerClickListener; // Declare the listener interface

    public CustomerListAdapter(List<Customer> customerList, OnCustomerClickListener customerClickListener) {
        this.customerList = customerList;
        this.customerClickListener = customerClickListener; // Initialize the listener
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        public TextView customerId, customerName, customerPhone, customerRank, customerDateRegistered;
        public MaterialCardView customerCard;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            customerId = itemView.findViewById(R.id.customer_id);
            customerName = itemView.findViewById(R.id.customer_name);
            customerRank = itemView.findViewById(R.id.customer_rank);
            customerPhone = itemView.findViewById(R.id.customer_phone_number);
            customerDateRegistered = itemView.findViewById(R.id.customer_date_registered);
            customerCard = itemView.findViewById(R.id.customer_card);
        }
    }

    public void updateCustomerList(List<Customer> newCustomerList) {
        this.customerList = newCustomerList;
        notifyDataSetChanged(); // Notify that the data has changed
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false); // Ensure this layout file exists
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.customerId.setText('#'+customer.getId()); // Use the customer's ID
        holder.customerName.setText(customer.getName()); // Use the customer's name
        holder.customerPhone.setText(customer.getPhone()); // Use the customer's email
        holder.customerRank.setText(customer.getRank()); // Use the customer's rank
        holder.customerDateRegistered.setText(customer.getDate_registered()); // Use the customer's date registered
        setRankColor(holder.customerRank, customer.getRank());


        // Set click listener for the entire item view
        holder.customerCard.setOnClickListener(v -> {
            Log.d("CustomerListAdapter", "Item clicked: " + customer.getId());
            customerClickListener.onCustomerClick(customer); // Trigger the listener
        });
    }
    private void setRankColor(TextView rankTextView, String rank) {
        int color;
        switch (rank) {
            case "Gold":
                color = ContextCompat.getColor(rankTextView.getContext(), R.color.status_preparing);
                break;
            case "Silver":
                color = ContextCompat.getColor(rankTextView.getContext(), R.color.silver);
                break;
            case "Bronze":
                color = ContextCompat.getColor(rankTextView.getContext(), R.color.status_wait);
                break;
            default:
                color = ContextCompat.getColor(rankTextView.getContext(), R.color.dark_gray);
                break;
        }

        // Apply the color tint to the background drawable
        Drawable background = ContextCompat.getDrawable(rankTextView.getContext(), R.drawable.status_background);
        if (background != null) {
            background.setTint(color);
            rankTextView.setBackground(background);
        }
    }
    @Override
    public int getItemCount() {
        return customerList.size();
    }

    // Define the listener interface
    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }
}
