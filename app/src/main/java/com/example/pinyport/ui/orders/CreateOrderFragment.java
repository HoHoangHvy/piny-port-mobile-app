package com.example.pinyport.ui.orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.CustomerOptionAdapter;
import com.example.pinyport.adapter.ProductAdapter;
import com.example.pinyport.databinding.FragmentCreateOrderBinding;
import com.example.pinyport.model.CustomerOption;
import com.example.pinyport.model.OrderDetail;
import com.example.pinyport.model.Product;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderFragment extends Fragment {
    private FragmentCreateOrderBinding binding;
    private final List<OrderDetail> orderDetails = new ArrayList<>();
    private ApiService apiService;
    private double totalPrice = 0.0;
    private final List<CustomerOption> customers = new ArrayList<>();
    private AutoCompleteTextView autoCompleteCustomer;
    private CustomerOptionAdapter customerAdapter;
    private String selectedCustomerId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        autoCompleteCustomer = binding.autoCompleteCustomer;

        setupCustomerSearch();
        binding.btnAddProduct.setOnClickListener(v -> fetchProducts());
        binding.btnApplyVoucher.setOnClickListener(v -> applyVoucher());
        binding.btnCreate.setOnClickListener(v -> createOrder());

        return binding.getRoot();
    }

    private void setupCustomerSearch() {
        apiService.getCustomerOptions().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    customers.clear();
                    if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                        JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                        for (JsonElement customerElement : dataArray) {
                            JsonObject customerObject = customerElement.getAsJsonObject();
                            CustomerOption customer = new CustomerOption(
                                    customerObject.get("id").getAsString(),
                                    customerObject.get("full_name").getAsString(),
                                    customerObject.get("phone_number").getAsString()
                            );
                            customers.add(customer);
                        }
                        customerAdapter = new CustomerOptionAdapter(requireContext(), customers);
                        autoCompleteCustomer.setAdapter(customerAdapter);
                        autoCompleteCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // Get the selected customer
                                CustomerOption selectedCustomer = customerAdapter.getItem(position);

                                if (selectedCustomer != null) {
                                    // Update the AutoCompleteTextView with the customer's name
                                    autoCompleteCustomer.setText(selectedCustomer.getName());
                                    selectedCustomerId = selectedCustomer.getId();
                                    // Optional: Display a toast or handle additional logic
                                    Toast.makeText(requireContext(), "Selected: " + selectedCustomer.getName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load customers.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProducts() {
        apiService.getProductOptions().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("data") && jsonResponse.get("data").isJsonArray()) {
                        JsonArray productsArray = jsonResponse.getAsJsonArray("data");
                        List<Product> products = new ArrayList<>();
                        for (JsonElement productElement : productsArray) {
                            if (productElement.isJsonObject()) {
                                JsonObject productObject = productElement.getAsJsonObject();
                                String productId = productObject.get("id").getAsString();
                                String productName = productObject.get("name").getAsString();
                                double productPrice = productObject.get("price").getAsDouble();
                                products.add(new Product(
                                        productId,
                                        productName,
                                        productPrice,
                                        productObject.get("image_url").getAsString(),
                                        new ArrayList<>()
                                ));
                            }
                        }
                        showProductSelectionDialog(products);
                    } else {
                        Toast.makeText(requireContext(), "No products available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading products: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showProductSelectionDialog(List<Product> products) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_options, null);
        RecyclerView rvProducts = dialogView.findViewById(R.id.rv_products);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Select a Product")
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                .create();

        ProductAdapter adapter = new ProductAdapter(requireContext(), products, product -> {
            dialog.dismiss();
            addProductToOrder(product);
        });

        rvProducts.setAdapter(adapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        dialog.show();
    }

    private void addProductToOrder(Product product) {
        OrderDetail orderDetail = new OrderDetail(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                1,
                new ArrayList<>(),
                product.getImageUrl(),
                "M"
                );
        orderDetails.add(orderDetail);
        updateTotalPrice();
        Toast.makeText(requireContext(), "Product added: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    private void applyVoucher() {
        String voucherCode = binding.etVoucher.getText().toString();
        if (voucherCode.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a voucher code.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Add API call to validate the voucher here
        Toast.makeText(requireContext(), "Voucher applied: " + voucherCode, Toast.LENGTH_SHORT).show();
    }

    private void createOrder() {
        if (selectedCustomerId == null) {
            Toast.makeText(requireContext(), "Please select a customer.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderDetails.isEmpty()) {
            Toast.makeText(requireContext(), "No products in the order.", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = binding.spinnerPaymentMethod.getSelectedItem().toString();
        String voucherCode = binding.etVoucher.getText().toString();

        JsonArray orderDetailsArray = new JsonArray();
        for (OrderDetail orderDetail : orderDetails) {
            JsonObject detailObject = new JsonObject();
            detailObject.addProperty("product_id", orderDetail.getProductId());
            detailObject.addProperty("size", orderDetail.getSize());
            detailObject.addProperty("quantity", orderDetail.getQuantity());
            detailObject.addProperty("total_price", calculateTotalPriceOfProduct(orderDetail));

            JsonArray toppingsArray = new JsonArray();
            for (OrderDetail.Topping topping : orderDetail.getToppings()) {
                JsonObject toppingObject = new JsonObject();
                toppingObject.addProperty("product_id", topping.getId());
                toppingsArray.add(toppingObject);
            }
            detailObject.add("toppings", toppingsArray);
            orderDetailsArray.add(detailObject);
        }

        JsonObject orderRequest = new JsonObject();
        orderRequest.addProperty("payment_method", paymentMethod);
        orderRequest.addProperty("customer_id", selectedCustomerId);
        orderRequest.add("order_details", orderDetailsArray);
        orderRequest.addProperty("order_total", totalPrice);
        orderRequest.addProperty("voucher_code", voucherCode);

        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.createOrder(orderRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Order created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to create order. Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateTotalPriceOfProduct(OrderDetail orderDetail) {
        double productPrice = orderDetail.getProductPrice();
        int quantity = orderDetail.getQuantity();
        double toppingsPrice = 0.0;
        for (OrderDetail.Topping topping : orderDetail.getToppings()) {
            toppingsPrice += topping.getPrice();
        }
        return productPrice * quantity + toppingsPrice;
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            totalPrice += calculateTotalPriceOfProduct(orderDetail);
        }
        binding.tvTotalPrice.setText(String.format("$%.2f", totalPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
