package com.example.pinyport.ui.customers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pinyport.R;

public class CustomerDetailsFragment extends Fragment {

    private TextView levelTextView;
    private CustomerDetailsViewModel customerDetailsViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_details, container, false);
        levelTextView = view.findViewById(R.id.tvLevel);

        // Sử dụng ViewModel
        customerDetailsViewModel = new ViewModelProvider(this).get(CustomerDetailsViewModel.class);

        // Quan sát dữ liệu LiveData từ ViewModel
        customerDetailsViewModel.getLevel().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String level) {
                // Cập nhật background dựa trên level thay đổi
                updateLevelBackground(level);
            }
        });

        return view;
    }

    public void updateLevelBackground(String level) {
        switch (level) {
            case "Bronze":
                levelTextView.setBackgroundResource(R.drawable.bronze_background);
                break;
            case "Silver":
                levelTextView.setBackgroundResource(R.drawable.silver_background);
                break;
            case "Gold":
                levelTextView.setBackgroundResource(R.drawable.gold_background);
                break;
            case "Diamond":
                levelTextView.setBackgroundResource(R.drawable.diamond_background);
                break;
            default:
                levelTextView.setBackgroundResource(android.R.color.transparent); // Mặc định nếu level không khớp
                break;
        }
    }
}
