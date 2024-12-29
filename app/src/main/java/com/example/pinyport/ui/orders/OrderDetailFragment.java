package com.example.pinyport.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.OrderDetailAdapter;
import com.example.pinyport.model.Order;
import com.example.pinyport.model.OrderDetail;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {

    private Order order;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerLevel;
    private TextView tvOrderId, tvOrderDateTime, tvOrderFromName, tvOrderFromAddress, tvOrderToName, tvOrderToAddress;
    private TextView tvTotalItems, tvShippingFee, tvDiscount, tvTotalAmount, tvPaymentMethod;
    private TextView tvFeedbackTime, tvFeedbackRating, tvFeedbackContent;
    private RecyclerView rvOrderDetails;
    private OrderDetailAdapter orderDetailAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvCustomerName = view.findViewById(R.id.customer_name);
        tvCustomerPhone = view.findViewById(R.id.customer_phone);
        tvCustomerLevel = view.findViewById(R.id.customer_level);

        tvOrderId = view.findViewById(R.id.order_id);
        tvOrderDateTime = view.findViewById(R.id.order_date_time);
        tvOrderFromName = view.findViewById(R.id.order_from_name);
        tvOrderFromAddress = view.findViewById(R.id.order_from_address);
        tvOrderToName = view.findViewById(R.id.order_to_name);
        tvOrderToAddress = view.findViewById(R.id.order_to_address);

        tvTotalItems = view.findViewById(R.id.tvTotalItems);
        tvShippingFee = view.findViewById(R.id.tvShippingFee);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);

        tvFeedbackTime = view.findViewById(R.id.feedback_time);
        tvFeedbackRating = view.findViewById(R.id.feedback_rating);
        tvFeedbackContent = view.findViewById(R.id.feedback_content);

        rvOrderDetails = view.findViewById(R.id.rvOrderDetails);
        rvOrderDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        orderDetailAdapter = new OrderDetailAdapter(new ArrayList<>());
        rvOrderDetails.setAdapter(orderDetailAdapter);

        // Fetch order details
        if (order != null) {
            fetchOrderDetails(order.getOrderId());
        } else {
            Toast.makeText(requireContext(), "Order not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOrderDetails(String orderId) {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.getOrderDetails(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("data") && !jsonResponse.get("data").isJsonNull()) {
                        JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                        if (dataArray.size() > 0) {
                            JsonObject orderObject = dataArray.get(0).getAsJsonObject();
                            bindOrderDetails(orderObject);
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch order details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindOrderDetails(JsonObject orderObject) {
        // Bind customer details
        tvCustomerName.setText("Name: " + orderObject.get("customer_name").getAsString());
        tvCustomerPhone.setText("Phone: " + orderObject.get("customer_phone").getAsString());
        tvCustomerLevel.setText("Level: " + orderObject.get("customer_level").getAsString());

        // Bind order details
        tvOrderId.setText("Order ID: " + orderObject.get("order_number").getAsString());
        tvOrderDateTime.setText(formatDateTime(orderObject.get("date_created").getAsString()));
        tvOrderFromName.setText(orderObject.get("from_name").getAsString());
        tvOrderFromAddress.setText(orderObject.get("from_address").getAsString());
        tvOrderToName.setText(orderObject.get("to_name").getAsString());
        tvOrderToAddress.setText(orderObject.get("to_address").getAsString());

        // Bind payment details
        tvTotalItems.setText("Total (" + orderObject.get("count_product").getAsString() + " items)");
        tvShippingFee.setText(orderObject.get("shipping_fee").getAsString() + " VND");
        tvDiscount.setText("-" + orderObject.get("discount").getAsString() + " VND");
        tvTotalAmount.setText(orderObject.get("total_price").getAsString() + " VND");
        tvPaymentMethod.setText(orderObject.get("payment_method").getAsString());

        // Bind feedback details
        if (orderObject.has("feedback") && !orderObject.get("feedback").isJsonNull()) {
            JsonObject feedbackObject = orderObject.getAsJsonObject("feedback");
            tvFeedbackTime.setText(formatDateTime(feedbackObject.get("feedback_time").getAsString()));
            tvFeedbackRating.setText("Rating: " + feedbackObject.get("rating").getAsString());
            tvFeedbackContent.setText("Content: " + feedbackObject.get("content").getAsString());
        }

        // Bind order items
        JsonArray orderDetailsArray = orderObject.getAsJsonArray("order_detail");
        List<OrderDetail> orderDetails = parseOrderDetails(orderDetailsArray);
        orderDetailAdapter.updateOrderDetails(orderDetails);
    }

    private List<OrderDetail> parseOrderDetails(JsonArray orderDetailsArray) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (JsonElement element : orderDetailsArray) {
            JsonObject detailObject = element.getAsJsonObject();
            OrderDetail orderDetail = new OrderDetail(
                    detailObject.get("product_name").getAsString(),
                    detailObject.get("product_price").getAsString(),
                    detailObject.get("quantity").getAsInt(),
                    parseToppings(detailObject.getAsJsonArray("toppings"))
            );
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    private List<OrderDetail.Topping> parseToppings(JsonArray toppingsArray) {
        List<OrderDetail.Topping> toppings = new ArrayList<>();
        for (JsonElement element : toppingsArray) {
            JsonObject toppingObject = element.getAsJsonObject();
            OrderDetail.Topping topping = new OrderDetail.Topping(
                    toppingObject.get("name").getAsString(),
                    toppingObject.get("price").getAsString()
            );
            toppings.add(topping);
        }
        return toppings;
    }

    private String formatDateTime(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy | HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
}