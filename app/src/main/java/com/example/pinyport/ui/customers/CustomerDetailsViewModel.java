package com.example.pinyport.ui.customers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomerDetailsViewModel extends ViewModel {
    private MutableLiveData<String> levelLiveData;

    // Khởi tạo LiveData nếu chưa được khởi tạo
    public LiveData<String> getLevel() {
        if (levelLiveData == null) {
            levelLiveData = new MutableLiveData<>();
            levelLiveData.setValue("Bronze"); // Khởi tạo giá trị level mặc định (có thể thay đổi tùy theo logic của bạn)
        }
        return levelLiveData;
    }

    // Cập nhật giá trị của level
    public void setLevel(String level) {
        if (levelLiveData != null) {
            levelLiveData.setValue(level);
        }
    }
}
