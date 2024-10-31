package com.example.pinyport.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import java.util.Calendar;

import com.example.pinyport.LoginActivity;
import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private EditText birthdayField;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        birthdayField = binding.birthdayField;
        birthdayField.setOnClickListener(v -> showDatePickerDialog());

        binding.signoutButton.setOnClickListener(v -> {
            // Create and show the Material dialog
            new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle("Confirm Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        // Clear the shared preferences
                        requireActivity().getSharedPreferences("userPrefs", 0).edit().clear().apply();
                        // Redirect to the login activity
                        redirectToLogin();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return root;
    }
    private void redirectToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            android.R.style.Theme_Material_Light_Dialog,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // When a date is selected, update the EditText with the chosen date
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                birthdayField.setText(selectedDate);
            },
            year, month, day
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}