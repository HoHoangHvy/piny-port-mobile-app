package com.example.pinyport;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView forgotPasswordTextView;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.signin);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        signInButton = findViewById(R.id.btnLogin);
        togglePasswordVisibility = findViewById(R.id.ivTogglePassword);
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);

        // Sự kiện đăng nhập
        signInButton.setOnClickListener(v -> signIn());

        // Sự kiện đổi hiển thị mật khẩu
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

        // Sự kiện Forgot Password
        forgotPasswordTextView.setOnClickListener(v -> showPhoneNumberInputDialog());
    }

    private void signIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
        } else {
            // Add your login logic here
            // For now, we are just displaying a toast message
            Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show();
            authSuccess();
        }
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
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

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
