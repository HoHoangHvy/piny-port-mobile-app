package com.example.pinyport.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pinyport.adapter.CustomerAdapter;
import com.example.pinyport.adapter.CustomerOptionAdapter;
import com.example.pinyport.databinding.FragmentCreateOrderBinding;
import com.example.pinyport.model.Customer;
import com.example.pinyport.model.CustomerOption;
import com.example.pinyport.model.OrderDetail;
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
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private ApiService apiService; // Initialize this using your Retrofit instance
    private double totalPrice = 0.0;
    private List<CustomerOption> customers = new ArrayList<>();
    private AutoCompleteTextView autoCompleteCustomer;
    private CustomerOptionAdapter customerAdapter;
    private String selectedCustomerId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        // Initialize AutoCompleteTextView
        autoCompleteCustomer = binding.autoCompleteCustomer;

        setupCustomerSearch();
        binding.btnAddProduct.setOnClickListener(v -> addNewProduct());
        binding.btnApplyVoucher.setOnClickListener(v -> applyVoucher());
        binding.btnCreate.setOnClickListener(v -> createOrder());

        return binding.getRoot();
    }

    private void setupCustomerSearch() {
        // Fetch customers
        apiService.getCustomerOptions().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                        JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                        customers.clear();
                        for (JsonElement customerElement : dataArray) {
                            if (customerElement.isJsonObject()) {
                                JsonObject customerObject = customerElement.getAsJsonObject();
                                String customerId = customerObject.get("id").getAsString();
                                String customerName = customerObject.get("name").getAsString();
                                String customerPhone = customerObject.get("phone").getAsString();
                                CustomerOption customer = new CustomerOption(customerId, customerName, customerPhone);
                                customers.add(customer);
                            }
                        }
                        // Set up adapter
                        customerAdapter = new CustomerOptionAdapter(requireContext(), customers);
                        autoCompleteCustomer.setAdapter(customerAdapter);

                        // Handle customer selection
                        autoCompleteCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                CustomerOption selectedCustomer = customers.get(position);
                                selectedCustomerId = selectedCustomer.getId();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Update createOrder() method
    private void createOrder() {
        if (selectedCustomerId == null) {
            Toast.makeText(requireContext(), "Please select a customer.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected payment method
        String paymentMethod = binding.spinnerPaymentMethod.getSelectedItem().toString();

        // Get voucher code
        String voucherCode = binding.etVoucher.getText().toString();

        // Build order details JSON array
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

        // Build main JSON object
        JsonObject orderRequest = new JsonObject();
        orderRequest.addProperty("payment_method", paymentMethod);
        orderRequest.addProperty("customer_id", selectedCustomerId);
        orderRequest.add("order_details", orderDetailsArray);
        orderRequest.addProperty("order_total", totalPrice);
        orderRequest.addProperty("voucher_code", voucherCode);

        // Send POST request
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
        //Return product price * quantity + toppings price
        double productPrice = orderDetail.getProductPrice();
        int quantity = orderDetail.getQuantity();
        double toppingsPrice = 0.0;
        for (OrderDetail.Topping topping : orderDetail.getToppings()) {
            toppingsPrice += topping.getPrice();
        }
        return productPrice * quantity + toppingsPrice;
    }
    private void addNewProduct() {
        // Logic to add a new product with toppings
        OrderDetail newProduct = new OrderDetail("productId123", "Large", 1, 10.0);
        newProduct.addTopping(new OrderDetail.Topping("toppingId456", "Small", 1, 2.0));
        orderDetails.add(newProduct);

        // Update total price
        totalPrice += newProduct.getTotalPrice();
        updateTotalPrice();
    }

    private void applyVoucher() {
        String voucherCode = binding.etVoucher.getText().toString();
        if (voucherCode.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a voucher code.", Toast.LENGTH_SHORT).show();
        } else {
            // Apply voucher logic
            totalPrice -= 5.0; // Example discount
            updateTotalPrice();
            Toast.makeText(requireContext(), "Voucher applied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalPrice() {
        binding.tvTotalPrice.setText(String.format("$%.2f", totalPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}