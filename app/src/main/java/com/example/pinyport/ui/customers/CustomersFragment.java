package com.example.pinyport.ui.customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pinyport.databinding.FragmentCustomersBinding;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CustomersViewModel ordersViewModel =
                new ViewModelProvider(this).get(CustomersViewModel.class);
        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCustomers;
        ordersViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}