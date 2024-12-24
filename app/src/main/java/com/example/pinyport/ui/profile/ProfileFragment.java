package com.example.pinyport.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.pinyport.LoginActivity;
import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentProfileBinding;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private EditText birthdayField;
    private SharedPrefsManager sharedPrefsManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize SharedPrefsManager
        sharedPrefsManager = new SharedPrefsManager(requireContext());

        // Load user data from SharedPreferences
        loadUserData();

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
    public String formatDate(String inputDate) {
        try {
            // Define the input date format (e.g., "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Parse the input date string into a Date object
            Date date = inputFormat.parse(inputDate);

            // Define the output date format (e.g., "dd/MM/yyyy")
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Format the Date object into the desired output format
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
    private void loadUserData() {
        // Retrieve the user data from SharedPreferences
        JsonObject user = sharedPrefsManager.getUser();

        if (user != null) {
            // Bind the user data to the UI elements
            binding.profileName.setText(user.get("name").getAsString());
            binding.roleTitle.setText(user.get("role_name").getAsString());
            binding.profileTitle.setText(user.get("level").getAsString());
            binding.branchField.setText(user.get("team_name").getAsString());
            binding.emailField.setText(user.get("email").getAsString());
            binding.phoneField.setText(user.get("phone_number").getAsString());
            binding.birthdayField.setText(formatDate(user.get("date_of_birth").getAsString()));
            binding.dateRegistered.setText(formatDate(user.get("date_registered").getAsString()));

            // Load the profile image using Glide
            String profileImageUrl = user.get("image").getAsString();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(profileImageUrl)
                        .circleCrop()
                        .into(binding.profileImage);
            } else {
                // If no image URL is provided, set a default image
                binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            // Handle the case where no user data is found
            binding.profileName.setText("No User Data Found");
            binding.profileTitle.setText("");
            binding.emailField.setText("");
            binding.phoneField.setText("");
            binding.birthdayField.setText("");

            // Set a default image when no user data is found
            binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }

    private void showDatePickerDialog() {
        // DatePickerDialog implementation remains the same
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}