package com.example.pinyport.ui.orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pinyport.R;
import com.example.pinyport.adapter.CustomerOptionAdapter;
import com.example.pinyport.adapter.ProductAdapter;
import com.example.pinyport.adapter.ToppingsAdapter;
import com.example.pinyport.databinding.FragmentCreateOrderBinding;
import com.example.pinyport.model.CustomerOption;
import com.example.pinyport.model.OrderDetail;
import com.example.pinyport.model.Product;
import com.example.pinyport.model.Topping;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private ProductAdapter productAdapter;
    private List<Product> filteredProducts;
    private LinearLayout layoutProducts;
    private final Map<String, Double> sizePrices = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        autoCompleteCustomer = binding.autoCompleteCustomer;
        layoutProducts = binding.layoutProducts;
        setupCustomerSearch();
        binding.btnAddProduct.setOnClickListener(v -> fetchProducts());
        binding.btnApplyVoucher.setOnClickListener(v -> applyVoucher());
        binding.btnCreate.setOnClickListener(v -> createOrder());

        sizePrices.put("S", 0.0);
        sizePrices.put("M", 5000.0);
        sizePrices.put("L", 10000.0);

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
                        autoCompleteCustomer.setOnItemClickListener((parent, view, position, id) -> {
                            CustomerOption selectedCustomer = customerAdapter.getItem(position);
                            if (selectedCustomer != null) {
                                autoCompleteCustomer.setText(selectedCustomer.getName());
                                selectedCustomerId = selectedCustomer.getId();
                                Toast.makeText(requireContext(), "Selected: " + selectedCustomer.getName(), Toast.LENGTH_SHORT).show();
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
    private List<Topping> parseToppings(JsonArray toppingsArray) {
        List<Topping> toppings = new ArrayList<>();
        for (JsonElement toppingElement : toppingsArray) {
            if (toppingElement.isJsonObject()) {
                JsonObject toppingObject = toppingElement.getAsJsonObject();
                String id = toppingObject.get("id").getAsString();
                String name = toppingObject.get("name").getAsString();
                double price = toppingObject.get("price").getAsDouble();
                toppings.add(new Topping(id, name, price));
            }
        }
        return toppings;
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
                                JsonArray toppingsArray = productObject.getAsJsonArray("toppings");
                                // Convert JsonArray to List<Topping>
                                List<Topping> toppings = parseToppings(toppingsArray);
                                products.add(new Product(
                                        productId,
                                        productName,
                                        productPrice,
                                        productObject.get("image_url").getAsString(),
                                        toppings
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
    public void showProductOptionsDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_search, null);

        Spinner spinnerSize = dialogView.findViewById(R.id.spinner_size);
        RecyclerView rvToppings = dialogView.findViewById(R.id.rv_toppings);
        EditText etNote = dialogView.findViewById(R.id.et_note);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        TextView tvTotalPrice = dialogView.findViewById(R.id.tv_total_price);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);

        // Setup quantity
        AtomicInteger initialQuantity = new AtomicInteger(1);
        tvQuantity.setText(String.valueOf(initialQuantity.get()));

        // Setup size Spinner
        List<String> sizes = Arrays.asList("S", "M", "L");
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);

        // Setup toppings RecyclerView
        List<Topping> toppings = product.getToppings();
        ToppingsAdapter toppingsAdapter = new ToppingsAdapter(toppings);
        rvToppings.setAdapter(toppingsAdapter);
        rvToppings.setLayoutManager(new LinearLayoutManager(requireContext()));
        toppingsAdapter.setOnToppingSelectedListener(selectedToppings -> {
            String selectedSize = spinnerSize.getSelectedItem().toString();
            updateTotalPriceProduct(product, selectedToppings, initialQuantity.get(), selectedSize, tvTotalPrice);
        });

        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSize = sizes.get(position);
                updateTotalPriceProduct(product, toppingsAdapter.getSelectedToppings(), initialQuantity.get(), selectedSize, tvTotalPrice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        // Setup quantity adjustment
        btnIncrease.setOnClickListener(v -> {
            initialQuantity.getAndIncrement();
            tvQuantity.setText(String.valueOf(initialQuantity.get()));
            String selectedSize = spinnerSize.getSelectedItem().toString();
            updateTotalPriceProduct(product, toppingsAdapter.getSelectedToppings(), initialQuantity.get(), selectedSize, tvTotalPrice);
        });

        btnDecrease.setOnClickListener(v -> {
            if (initialQuantity.get() > 1) {
                initialQuantity.getAndDecrement();
                tvQuantity.setText(String.valueOf(initialQuantity.get()));
                String selectedSize = spinnerSize.getSelectedItem().toString();
                updateTotalPriceProduct(product, toppingsAdapter.getSelectedToppings(), initialQuantity.get(), selectedSize, tvTotalPrice);
            }
        });

        updateTotalPriceProduct(product, toppingsAdapter.getSelectedToppings(), initialQuantity.get(), "S", tvTotalPrice);


        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Product Options")
                .create();
        // Setup confirm button
        btnConfirm.setOnClickListener(v -> {
            String selectedSize = spinnerSize.getSelectedItem().toString();
            List<Topping> selectedToppings = toppingsAdapter != null ? toppingsAdapter.getSelectedToppings() : new ArrayList<>();
            String note = etNote.getText().toString();
            int quantity = Integer.parseInt(tvQuantity.getText().toString());

            OrderDetail orderDetail = new OrderDetail(
                    product.getProductId(),
                    product.getName(),
                    getTotalPrice(product, selectedToppings, quantity, selectedSize),
                    quantity,
                    selectedToppings,
                    product.getImageUrl(),
                    selectedSize,
                    note
            );
            orderDetails.add(orderDetail);
            updateTotalPrice();
            // Display product in layoutProducts
            View productView = LayoutInflater.from(requireContext()).inflate(R.layout.item_selected_product, layoutProducts, false);
            ImageView ivProductImage = productView.findViewById(R.id.ivProductImage);
            TextView tvProductName = productView.findViewById(R.id.tvProductName);
            TextView tvToppings = productView.findViewById(R.id.tvToppings);
            TextView tvNotes = productView.findViewById(R.id.tvNotes);
            TextView tvProductPrice = productView.findViewById(R.id.tvProductPrice);
            Button btnRemove = productView.findViewById(R.id.btn_remove);

            Glide.with(requireContext()).load(orderDetail.getProductImageUrl()).into(ivProductImage);
            tvProductName.setText(orderDetail.getProductName() + " (" + orderDetail.getSize() + ") x" + orderDetail.getQuantity());
            // Display toppings
            StringBuilder toppingsBuilder = new StringBuilder("Toppings: ");
            for (Topping topping : orderDetail.getToppings()) {
                toppingsBuilder.append(topping.getName()).append(", ");
            }
            if (orderDetail.getToppings().size() > 0) {
                toppingsBuilder.setLength(toppingsBuilder.length() - 2); // Remove the last comma
            }
            tvToppings.setText(toppingsBuilder.toString());
            tvNotes.setText("Notes: " + orderDetail.getNotes());
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(format.format(orderDetail.getTotalPrice()));

            btnRemove.setOnClickListener(v1 -> {
                layoutProducts.removeView(productView);
                orderDetails.remove(orderDetail);
                updateTotalPrice();
                Toast.makeText(requireContext(), "Product removed: " + product.getName(), Toast.LENGTH_SHORT).show();
            });
            layoutProducts.addView(productView);

            Toast.makeText(requireContext(), "Product added: " + product.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Setup cancel button
        btnCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Product selection cancelled.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }
    private double getTotalPrice(Product product, List<Topping> selectedToppings, int quantity, String selectedSize) {
        double basePrice = product.getPrice();
        double sizePrice = sizePrices.getOrDefault(selectedSize, 0.0);
        double toppingsPrice = 0.0;
        for (Topping topping : selectedToppings) {
            toppingsPrice += topping.getPrice();
        }
        return (basePrice + sizePrice + toppingsPrice) * quantity;
    }
    private void updateTotalPriceProduct(Product product, List<Topping> selectedToppings, int quantity, String selectedSize, TextView tvTotalPrice) {
        double totalPrice = getTotalPrice(product, selectedToppings, quantity, selectedSize);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(formatter.format(totalPrice));
    }
    private void updateTotalPrice(Product product, List<Topping> selectedToppings, int quantity) {
        double basePrice = product.getPrice();
        double toppingsPrice = 0.0;
        for (Topping topping : selectedToppings) {
            toppingsPrice += topping.getPrice();
        }
        double totalPrice = (basePrice + toppingsPrice) * quantity;
    }
    private void showProductSelectionDialog(List<Product> products) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_options, null);

        SearchView searchView = dialogView.findViewById(R.id.search_view);
        RecyclerView rvProducts = dialogView.findViewById(R.id.rv_products);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Select a Product")
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                .create();

        filteredProducts = new ArrayList<>(products);
        productAdapter = new ProductAdapter(requireContext(), filteredProducts,this, product -> {
            dialog.dismiss();
            showProductOptionsDialog(product);
        });

        rvProducts.setAdapter(productAdapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, products);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, products);
                return true;
            }
        });

        dialog.show();
    }

    private void filterProducts(String query, List<Product> originalProducts) {
        filteredProducts.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredProducts.addAll(originalProducts);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : originalProducts) {
                if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredProducts.add(product);
                }
            }
        }
        productAdapter.setProducts(filteredProducts);
    }

//    private void addProductToOrder(Product product) {
//        OrderDetail orderDetail = new OrderDetail(
//                product.getProductId(),
//                product.getName(),
//                product.getPrice(),
//                1,
//                new ArrayList<>(),
//                product.getImageUrl(),
//                "M",
//                ""
//        );
//        orderDetails.add(orderDetail);
//        updateTotalPrice();
//
//        // Create a new TextView for the product
//        View productView = LayoutInflater.from(requireContext()).inflate(R.layout.item_selected_product, layoutProducts, false);
//
//        TextView tvProductName = productView.findViewById(R.id.tv_product_name);
//        TextView tvProductPrice = productView.findViewById(R.id.tv_product_price);
//        Button btnRemove = productView.findViewById(R.id.btn_remove);
//
//        tvProductName.setText(product.getName());
//        tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
//
//        btnRemove.setOnClickListener(v -> {
//            layoutProducts.removeView(productView);
//            orderDetails.remove(orderDetail);
//            updateTotalPrice();
//            Toast.makeText(requireContext(), "Product removed: " + product.getName(), Toast.LENGTH_SHORT).show();
//        });
//
//        layoutProducts.addView(productView);
//
//        Toast.makeText(requireContext(), "Product added: " + product.getName(), Toast.LENGTH_SHORT).show();
//    }

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
            detailObject.addProperty("total_price", orderDetail.getTotalPrice());

            JsonArray toppingsArray = new JsonArray();
            for (Topping topping : orderDetail.getToppings()) {
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
    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            totalPrice += orderDetail.getTotalPrice();
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.tvTotalPrice.setText(formatter.format(totalPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
