package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class ProfileActivity extends AppCompatActivity {

    private EditText oldPasswordInput, newPasswordInput, confirmNewPasswordInput, emailInput, currentPasswordForEmail;
    private Button updatePasswordButton, updateEmailButton, BackButton;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String loggedInEmail;
    private Switch darkModeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Use correct layout

        initializeViews();
        dbHelper = DatabaseHelper.getInstance(this);

        // Retrieve logged-in user's email from SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        loggedInEmail = sharedPreferences.getString("userEmail", null);

        // Set the current email in the emailInput field
        emailInput.setText(loggedInEmail);

        setupEventListeners();
        setupDarkModeSwitch();

    }

    private void initializeViews() {
        oldPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmPasswordInput);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        emailInput = findViewById(R.id.emailInput);  // This should show the current email automatically
        currentPasswordForEmail = findViewById(R.id.currentPasswordForEmail);
        updateEmailButton = findViewById(R.id.updateEmailButton);
        BackButton = findViewById(R.id.backToHomeButton);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
    }

    private void setupEventListeners() {
        updatePasswordButton.setOnClickListener(v -> updatePasswordButtonOnClick());
        updateEmailButton.setOnClickListener(v -> updateEmailButtonOnClick());
        BackButton.setOnClickListener(view -> navigateToHome());
    }

    private void updatePasswordButtonOnClick() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();

        if (validateInputs(oldPassword, newPassword, confirmNewPassword)) {
            // Use the InputValidator's isValidPassword method to check the new password
            if (!InputValidator.isValidPassword(newPassword)) {
                showToast("Password must be between 6 to 12 characters, contain at least one number, one lowercase letter, and one uppercase letter.");
                return;
            }

            if (dbHelper.checkUser(loggedInEmail, oldPassword)) {
                if (newPassword.equals(confirmNewPassword)) {
                    boolean isPasswordUpdated = dbHelper.updatePassword(newPassword, loggedInEmail);
                    if (isPasswordUpdated) {
                        showToast("Password updated successfully!");
                    } else {
                        showToast("Failed to update password. Please try again.");
                    }
                } else {
                    showToast("New passwords do not match!");
                }
            } else {
                showToast("Old password is incorrect!");
            }
        }
    }

    private void updateEmailButtonOnClick() {
        String newEmail = emailInput.getText().toString().trim();
        String currentPassword = currentPasswordForEmail.getText().toString().trim();

        if (validateEmailUpdateInputs(newEmail, currentPassword)) {
            if (dbHelper.updateEmailSafely(newEmail, loggedInEmail, currentPassword)) {
                showToast("Email updated successfully!");
                // Update SharedPreferences
                sharedPreferences.edit().putString("userEmail", newEmail).apply();
                // Reflect change in the UI or re-fetch data
                emailInput.setText(newEmail);
                loggedInEmail = newEmail;  // Update the local cache of the user's email
            } else {
                showToast("Failed to update email. Please ensure the new email is unique and your current password is correct.");
            }
        }
    }

    private boolean validateEmailUpdateInputs(String newEmail, String currentPassword) {
        if (newEmail.isEmpty() || currentPassword.isEmpty()) {
            showToast("All fields are required.");
            return false;
        }
        return true;
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmNewPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showToast("All fields are required.");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void setupDarkModeSwitch() {
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        darkModeSwitch.setChecked(isDarkMode);
        applyTheme(isDarkMode);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();
            applyTheme(isChecked);
        });
    }

    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
