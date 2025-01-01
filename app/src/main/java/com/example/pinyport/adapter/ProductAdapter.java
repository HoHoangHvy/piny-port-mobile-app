package com.example.pinyport.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pinyport.R;
import com.example.pinyport.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> originalProducts;
    private List<Product> filteredProducts;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.originalProducts = new ArrayList<>(products);
        this.filteredProducts = new ArrayList<>(products);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }
    public void setProducts(List<Product> products) {
        this.filteredProducts = products;
        notifyDataSetChanged();
    }
    // Method to filter products based on search query
    public void filter(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredProducts = new ArrayList<>(originalProducts);
        } else {
            filteredProducts = new ArrayList<>();
            for (Product product : originalProducts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        public void bind(Product product) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .into(productImage);

            productName.setText(product.getName());

            // Format price to Vietnamese Dong
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(format.format(product.getPrice()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
