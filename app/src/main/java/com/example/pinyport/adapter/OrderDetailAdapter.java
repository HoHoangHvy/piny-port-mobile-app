package com.example.pinyport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pinyport.R;
import com.example.pinyport.model.OrderDetail;
import com.example.pinyport.model.Topping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice, tvQuantity, tvToppings;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvToppings = itemView.findViewById(R.id.tvToppings);
        }

        public void bind(OrderDetail orderDetail) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            tvProductName.setText(orderDetail.getProductName() + " " + String.format("x%d", orderDetail.getQuantity()));
            tvProductPrice.setText(String.format("%s", formatter.format((orderDetail.getTotalPrice()))));

            // Load product image using Glide
            Glide.with(itemView.getContext())
                    .load(orderDetail.getProductImageUrl())// Add an error image
                    .into(ivProductImage);

            // Display toppings
            StringBuilder toppingsBuilder = new StringBuilder("Toppings: ");
            for (Topping topping : orderDetail.getToppings()) {
                toppingsBuilder.append(topping.getName()).append(", ");
            }
            if (orderDetail.getToppings().size() > 0) {
                toppingsBuilder.setLength(toppingsBuilder.length() - 2); // Remove the last comma
            }
            tvToppings.setText(toppingsBuilder.toString());
        }
    }
}