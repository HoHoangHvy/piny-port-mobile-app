package com.example.pinyport.ui.customers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerDetailsViewModel extends ViewModel {
    private MutableLiveData<String> levelLiveData;
    private final MutableLiveData<List<Order>> orderList = new MutableLiveData<>();

    // Khởi tạo LiveData nếu chưa được khởi tạo
    public LiveData<String> getLevel() {
        if (levelLiveData == null) {
            levelLiveData = new MutableLiveData<>();
            levelLiveData.setValue("Bronze"); // Khởi tạo giá trị level mặc định
        }
        return levelLiveData;
    }

    // Cập nhật giá trị của level
    public void setLevel(String level) {
        if (levelLiveData != null) {
            levelLiveData.setValue(level);
        }
    }

    public CustomerDetailsViewModel() {
        loadOrders();
    }

    public LiveData<List<Order>> getOrderList() {
        return orderList;
    }

    // Load danh sách các đơn hàng
    private void loadOrders() {
        List<Order> orders = new ArrayList<>();

        // Ví dụ thêm các đối tượng Order vào danh sách (giả sử class Order đã có cấu trúc cơ bản)
        orders.add(new Order("#MM0876", "12/06/2024", "11:30", "Success", "170.000 VND"));
        orders.add(new Order("#MM0870", "10/06/2024", "10:00", "Cancel", "170.000 VND"));

        orderList.setValue(orders);
    }
}
