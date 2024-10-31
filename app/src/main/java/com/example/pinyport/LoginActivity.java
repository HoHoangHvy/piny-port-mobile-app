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
        forgotPasswordTextView.setOnClickListener(v -> showPhoneNumberPopup());
    }

    private void signIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showPhoneNumberPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_enter_phone, null);
        builder.setView(dialogView);

        EditText phoneNumberEditText = dialogView.findViewById(R.id.etPhoneNumber);
        Button sendCodeButton = dialogView.findViewById(R.id.btnSendCode);

        sendCodeButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else {
                // Gửi mã xác nhận (thực hiện ở đây nếu cần)
                Toast.makeText(this, "Verification code sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                // Hiện popup nhập mã xác nhận
                showVerificationCodePopup();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showVerificationCodePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_enter_code, null);
        builder.setView(dialogView);

        EditText verificationCodeEditText = dialogView.findViewById(R.id.etVerificationCode);
        Button confirmCodeButton = dialogView.findViewById(R.id.btnVerifyCode);

        confirmCodeButton.setOnClickListener(v -> {
            String verificationCode = verificationCodeEditText.getText().toString().trim();
            if (verificationCode.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
            } else {
                // Kiểm tra mã xác nhận (thực hiện ở đây nếu cần)
                Toast.makeText(this, "Verification code verified", Toast.LENGTH_SHORT).show();
                // Hiện popup đặt mật khẩu mới
                showNewPasswordPopup();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showNewPasswordPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_new_password, null);
        builder.setView(dialogView);

        EditText newPasswordEditText = dialogView.findViewById(R.id.etNewPassword);
        EditText confirmNewPasswordEditText = dialogView.findViewById(R.id.etConfirmPassword);
        Button updatePasswordButton = dialogView.findViewById(R.id.btnConfirmReset);

        updatePasswordButton.setOnClickListener(v -> {
            try {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

                if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
