package com.example.pinyport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.model.OrderDetail;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<OrderDetail> orderDetails;

    public OrderDetailAdapter(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetails.get(position);
        holder.bind(orderDetail);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public void updateOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
        notifyDataSetChanged();
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName, tvProductPrice, tvQuantity, tvToppings;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvToppings = itemView.findViewById(R.id.tvToppings);
        }

        public void bind(OrderDetail orderDetail) {
            tvProductName.setText(orderDetail.getProductName());
            tvProductPrice.setText(String.format("Price: %s VND", orderDetail.getProductPrice()));
            tvQuantity.setText(String.format("Quantity: %d", orderDetail.getQuantity()));

            // Display toppings
            StringBuilder toppingsBuilder = new StringBuilder("Toppings: ");
            for (OrderDetail.Topping topping : orderDetail.getToppings()) {
                toppingsBuilder.append(topping.getName()).append(", ");
            }
            if (orderDetail.getToppings().size() > 0) {
                toppingsBuilder.setLength(toppingsBuilder.length() - 2); // Remove the last comma
            }
            tvToppings.setText(toppingsBuilder.toString());
        }
    }
}