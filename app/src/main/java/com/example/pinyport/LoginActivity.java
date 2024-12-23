package com.example.pinyport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.pinyport.DTO.GenOtpRequest;
import com.example.pinyport.DTO.GenOtpResponse;
import com.example.pinyport.DTO.LoginOtpRequest;
import com.example.pinyport.DTO.LoginOtpResponse;
import com.example.pinyport.network.ApiClient;
import com.example.pinyport.network.ApiService;
import com.example.pinyport.network.SharedPrefsManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView forgotPasswordTextView;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;
    private TextView tvSwitchMode;
    private ViewSwitcher loginSwitcher;
    private ApiService apiService;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.signin);

        // Initialize SharedPrefsManager
        sharedPrefsManager = new SharedPrefsManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        signInButton = findViewById(R.id.btnLogin);
        togglePasswordVisibility = findViewById(R.id.ivTogglePassword);
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);

        loginSwitcher = findViewById(R.id.loginSwitcher);
        tvSwitchMode = findViewById(R.id.tvSwitchMode);

        // Switch between login modes
        tvSwitchMode.setOnClickListener(v -> {
            if (loginSwitcher.getDisplayedChild() == 1) {
                // Switch to OTP Login
                loginSwitcher.showNext();
                tvSwitchMode.setText("Switch to Password Login");
                signInButton.setText("SEND OTP");
            } else {
                // Switch to Password Login
                loginSwitcher.showPrevious();
                tvSwitchMode.setText("Switch to OTP Login");
                signInButton.setText("SIGN IN");
            }
        });

        // Toggle password visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.eye_off);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.eye);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Forgot Password
        forgotPasswordTextView.setOnClickListener(v -> showPhoneNumberInputDialog());

        signInButton.setOnClickListener(v -> {
            if (loginSwitcher.getDisplayedChild() == 1) {
                // Password Login logic
                EditText etEmail = findViewById(R.id.etEmail);
                EditText etPassword = findViewById(R.id.etPassword);

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Add your login logic here
                    // For now, we are just displaying a toast message
                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    authSuccess();
                }
                // Perform validation and login API call
            } else {
                // OTP Login logic
                EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
                String phoneNumber = etPhoneNumber.getText().toString();

                if (isValidPhoneNumber(phoneNumber)) {
                    GenOtpRequest request = new GenOtpRequest(phoneNumber);
                    apiService.genOtp(request).enqueue(new Callback<GenOtpResponse>() {
                        @Override
                        public void onResponse(Call<GenOtpResponse> call, Response<GenOtpResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(LoginActivity.this, "OTP sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                                showOtpVerifyDialog(response.body().getData().getUser_id()); // Proceed to OTP verification dialog
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GenOtpResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }

                // Perform validation and OTP verification API call
            }
        });
    }

    private void showOtpVerifyDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_otp_input, null);
        builder.setView(dialogView);

        PinView otpEditText = dialogView.findViewById(R.id.etp_otp);
        Button verifyOtpButton = dialogView.findViewById(R.id.btn_verify);

        AlertDialog alertDialog = builder.create();

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (isValidOtp(otp)) {
                // Call the server to verify OTP and login
                apiService.verifyOtp(new LoginOtpRequest(userId, otp)).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonResponse = response.body();

                            // Extract fields dynamically
                            boolean success = jsonResponse.get("success").getAsBoolean();
                            String message = jsonResponse.get("message").getAsString();

                            JsonObject data = jsonResponse.getAsJsonObject("data");
                            String token = data.get("token").getAsString();

                            JsonObject user = data.getAsJsonObject("user");
                            String userId = user.get("id").getAsString();
                            String name = user.get("name").getAsString();
                            String email = user.get("email").getAsString();
                            String userObject = user.toString();

                            // Save token and user ID to SharedPreferences
                            sharedPrefsManager.saveToken(token);
                            sharedPrefsManager.saveUserId(userId);
                            sharedPrefsManager.setLoggedIn(true);
                            sharedPrefsManager.saveUser(userObject);

                            authSuccess();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    // Show the phone number input dialog
    private void showPhoneNumberInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_phone_input, null);
        builder.setView(dialogView);

        TextInputEditText phoneNumberEditText = dialogView.findViewById(R.id.etPhoneNumber);
        Button sendCodeButton = dialogView.findViewById(R.id.btnSendCode);

        AlertDialog alertDialog = builder.create();

        sendCodeButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (isValidPhoneNumber(phoneNumber)) {
                // Show OTP dialog after validating the phone number
                showOtpInputDialog();
                alertDialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    // Show OTP input dialog
    private void showOtpInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_otp_input, null);
        builder.setView(dialogView);

        PinView otpEditText = dialogView.findViewById(R.id.etp_otp);
        Button verifyOtpButton = dialogView.findViewById(R.id.btn_verify);

        AlertDialog alertDialog = builder.create();

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (isValidOtp(otp)) {
                // Show new password dialog after validating the OTP
                showNewPasswordDialog();
                alertDialog.dismiss();
            } else {
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    // Validate OTP
    private boolean isValidOtp(String otp) {
        // Add your OTP validation logic here
        return otp.length() == 6; // Example validation
    }

    // Show new password input dialog
    private void showNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_password, null);
        builder.setView(dialogView);

        TextInputEditText newPasswordEditText = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText confirmNewPasswordEditText = dialogView.findViewById(R.id.etConfirmNewPassword);
        Button resetPasswordButton = dialogView.findViewById(R.id.btnResetPassword);

        AlertDialog alertDialog = builder.create();

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Update password logic here
                Toast.makeText(this, "Password successfully updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                authSuccess();
            }
        });

        alertDialog.show();
    }

    private void authSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Validate the phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Add your phone number validation logic here
        return phoneNumber.length() == 10; // Example validation
    }
}