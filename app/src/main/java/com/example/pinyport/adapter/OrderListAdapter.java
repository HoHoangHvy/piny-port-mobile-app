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
import com.example.pinyport.model.Order;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OnOrderClickListener orderClickListener; // Declare the listener interface

    public OrderListAdapter(List<Order> orderList, OnOrderClickListener orderClickListener) {
        this.orderList = orderList;
        this.orderClickListener = orderClickListener; // Initialize the listener
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderId, orderDate, orderTime, orderStatus, orderItemsCount, orderValue, orderCustomer;
        public MaterialCardView orderCard;
        public OrderViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderItemsCount = itemView.findViewById(R.id.order_items_count);
            orderValue = itemView.findViewById(R.id.order_value);
            orderCustomer = itemView.findViewById(R.id.order_customer);
            orderCard = itemView.findViewById(R.id.order_card);
        }
    }

    public void updateOrderList(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged(); // Notify that the data has changed
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("#" + order.getOrderId());
        holder.orderDate.setText(order.getDate());
        holder.orderTime.setText(order.getTime());
        holder.orderStatus.setText(order.getStatus());
        setStatusColor(holder.orderStatus, order.getStatus());
        holder.orderItemsCount.setText(order.getItemsCount() + " items");
        holder.orderValue.setText(order.getValue());
        holder.orderCustomer.setText(order.getCustomerName());

        // Set click listener for the entire item view
        holder.orderCard.setOnClickListener(v -> {
            Log.d("OrderListAdapter", "Item clicked: " + order.getOrderId());
            orderClickListener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void setStatusColor(TextView statusTextView, String status) {
        int color;
        switch (status) {
            case "Wait":
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.status_wait);
                break;
            case "Preparing":
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.status_preparing);
                break;
            case "Delivering":
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.status_delivering);
                break;
            case "Success":
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.status_success);
                break;
            case "Cancelled":
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.status_cancel);
                break;
            default:
                color = ContextCompat.getColor(statusTextView.getContext(), R.color.dark_gray);
                break;
        }

        // Apply the color tint to the background drawable
        Drawable background = ContextCompat.getDrawable(statusTextView.getContext(), R.drawable.status_background);
        if (background != null) {
            background.setTint(color);
            statusTextView.setBackground(background);
        }
    }

    // Define the listener interface
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}
