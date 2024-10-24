package com.example.pinyport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.model.Order;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderId, orderDate, orderTime, orderStatus, orderItemsCount, orderValue, orderCustomer;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderItemsCount = itemView.findViewById(R.id.order_items_count);
            orderValue = itemView.findViewById(R.id.order_value);
            orderCustomer = itemView.findViewById(R.id.order_customer);
        }
    }

    public OrderListAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("#" + order.getOrderId());
        holder.orderDate.setText(order.getDate());
        holder.orderTime.setText(order.getTime());
        holder.orderStatus.setText(order.getStatus());
        holder.orderItemsCount.setText(order.getItemsCount() + " items");
        holder.orderValue.setText(order.getValue());
        holder.orderCustomer.setText(order.getCustomerName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}
