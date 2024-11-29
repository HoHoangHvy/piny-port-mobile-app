package com.example.pinyport.ui.orders;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentCreateOrderBinding;

import java.util.Calendar;

public class CreateOrderFragment extends Fragment {
    private FragmentCreateOrderBinding binding;
    private Spinner lastAddedSpinner;
    private Button lastAddedButton;
    private boolean isFirstButtonHidden = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        setupcustomerSpinner();
        setupbranchSpinner();
        setupproductSpinner();
        setuppaymentSpinner();
        setupDateTimePicker();
        return binding.getRoot();
    }

    private void setupDateTimePicker() {
        EditText dateTimeEditText = binding.etDateValue;  // Liên kết với EditText
        dateTimeEditText.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày trước khi hiển thị lên EditText
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    binding.etDateValue.setText(date);  // Hiển thị ngày đã chọn vào EditText
                }, year, month, day);
        datePickerDialog.show();
    }



    private void setupcustomerSpinner() {
        String[] customerOptions = {"Customer A", "Customer B", "Customer C", "Customer D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), // Context từ Fragment
                android.R.layout.simple_spinner_item, // Layout hiển thị cho mỗi mục
                customerOptions // Dữ liệu
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCustomer.setAdapter(adapter);
        binding.spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCustomer = customerOptions[position];
                Toast.makeText(requireContext(), "Selected: " + selectedCustomer, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupbranchSpinner() {
        String[] branchOptions = {"Branch A", "Branch B", "Branch C", "Branch D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), // Context từ Fragment
                android.R.layout.simple_spinner_item, // Layout hiển thị cho mỗi mục
                branchOptions // Dữ liệu
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBranch.setAdapter(adapter);
        binding.spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBranch = branchOptions[position];
                Toast.makeText(requireContext(), "Selected: " + selectedBranch, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupproductSpinner() {
        String[] productOptions = {"Cà phê muối", "Ô long lài sữa"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, productOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerProduct.setAdapter(adapter);

        binding.btnAddSpinner.setOnClickListener(v -> addNewSpinner(productOptions));
    }

    private void addNewSpinner(String[] productOptions) {
        if (lastAddedButton != null) {
            lastAddedButton.setVisibility(View.GONE);
        }

        if (!isFirstButtonHidden) {
            binding.btnAddSpinner.setVisibility(View.GONE);
            isFirstButtonHidden = true;
        }

        LinearLayout horizontalLayout = new LinearLayout(requireContext());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setId(View.generateViewId());

        LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParams.setMargins(0, 0, 0, 0); // Khoảng cách trên và dưới
        horizontalLayout.setLayoutParams(horizontalLayoutParams);

        View newView = new View(requireContext());
        newView.setId(View.generateViewId());
        int widthInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
        int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(widthInPx, heightInPx);
        newView.setLayoutParams(viewLayoutParams);

        Spinner newSpinner = new Spinner(requireContext());
        newSpinner.setId(View.generateViewId()); // Đảm bảo Spinner có ID duy nhất
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, productOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(adapter);

        newSpinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_500)));

        int heightspInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                0, heightspInPx, 1.0f);
        newSpinner.setLayoutParams(spinnerLayoutParams);

        Button newButton = new Button(requireContext());
        newButton.setId(View.generateViewId());
        newButton.setText("+");
        newButton.setBackground(null); // Không có nền
        newButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_500)); // Chữ màu blue_500
        int widthbtInPx = (int) (60 * getResources().getDisplayMetrics().density); // 55dp chuyển thành px
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(widthbtInPx, LinearLayout.LayoutParams.WRAP_CONTENT);
        newButton.setLayoutParams(buttonLayoutParams);


        horizontalLayout.addView(newView);
        horizontalLayout.addView(newSpinner);
        horizontalLayout.addView(newButton);

        LinearLayout layoutProducts = binding.layoutProducts;
        layoutProducts.setOrientation(LinearLayout.VERTICAL);
        layoutProducts.addView(horizontalLayout);

        newButton.setOnClickListener(v -> addNewSpinner(productOptions));

        lastAddedSpinner = newSpinner;
        lastAddedButton = newButton;

        layoutProducts.requestLayout();
    }


    private void setuppaymentSpinner() {
        String[] paymentOptions = {"Momo", "Bank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), // Context từ Fragment
                android.R.layout.simple_spinner_item, // Layout hiển thị cho mỗi mục
                paymentOptions // Dữ liệu
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPayment.setAdapter(adapter);
        binding.spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBranch = paymentOptions[position];
                Toast.makeText(requireContext(), "Selected: " + selectedBranch, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
