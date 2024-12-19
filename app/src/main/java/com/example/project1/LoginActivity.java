package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private CheckBox rememberMe;
    private Button loginButton,backToMainButton;
    private TextView signUpLink;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Use correct layout

        initializeViews();
        dbHelper = DatabaseHelper.getInstance(this);

        setupSharedPreferences();
        loadSavedCredentials();

        // Print database contents for debugging
//        dbHelper.printDatabaseContents();

        setupEventListeners();
    }

    /**
     * Initialize view components.
     */
    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberMe = findViewById(R.id.rememberMe);
        loginButton = findViewById(R.id.loginButton);
        backToMainButton = findViewById(R.id.backToMain);
        signUpLink = findViewById(R.id.signUpLink);
    }

    /**
     * Setup SharedPreferences for "Remember Me" functionality.
     */
    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Load previously saved credentials if "Remember Me" is checked.
     */
    private void loadSavedCredentials() {
        if (sharedPreferences.getBoolean("remember", false)) {
            emailInput.setText(sharedPreferences.getString("email", ""));
            passwordInput.setText(sharedPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }
    }

    /**
     * Setup event listeners for buttons and links.
     */
    private void setupEventListeners() {
        loginButton.setOnClickListener(v -> loginButtonOnClick());
        backToMainButton.setOnClickListener(v -> navigateToMain());
        signUpLink.setOnClickListener(v -> signUpLinkOnClick());
    }

    /**
     * Handle Sign-Up link click event.
     */
    private void signUpLinkOnClick() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent); // Navigate to Sign-Up Activity
    }

    /**
     * Handle Login button click event.
     */
    private void loginButtonOnClick() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (validateInputs(email, password)) {
            if (dbHelper.checkUser(email, password)) {
                showToast("Login Successful!");

                // Save credentials if "Remember Me" is checked
                if (rememberMe.isChecked()) {
                    saveCredentials(email, password);
                } else {
                    clearSavedCredentials(); // Clear if "Remember Me" is unchecked
                }

                // Navigate to HomeActivity, also save the email and the user first and last name
                editor.putString("userName", dbHelper.getUserName(email));
                editor.putString("userEmail", email).commit();
                navigateToHome();
            } else {
                showToast("Invalid Credentials!");
            }
        }
    }

    /**
     * Validate the input fields.
     */
    private boolean validateInputs(String email, String password) {
        resetFieldStates();

        return validateField(!email.isEmpty() && email.contains("@"), emailInput, "Invalid email format")
                & validateField(!password.isEmpty(),
                passwordInput, "password can't be empty");
    }

    /**
     * Validate a single field and set error if invalid.
     */
    private boolean validateField(boolean isValidCondition, EditText inputField, String errorMessage) {
        if (!isValidCondition) {
            inputField.setError(errorMessage);
            inputField.setBackgroundColor(Color.RED);
        }
        return isValidCondition;
    }

    /**
     * Resets the background color of all input fields to their default state.
     */
    private void resetFieldStates() {
        setFieldDefaultState(emailInput);
        setFieldDefaultState(passwordInput);
    }

    /**
     * Sets the default background state for input fields.
     */
    private void setFieldDefaultState(EditText inputField) {
        Drawable drawable = ContextCompat.getDrawable(LoginActivity.this, R.drawable.edit_text_bg);
        inputField.setBackground(drawable);
    }

    /**
     * Show a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Save credentials if "Remember Me" is checked.
     */
    private void saveCredentials(String email, String password) {
        editor.putBoolean("remember", true);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    /**
     * Clear saved credentials when "Remember Me" is unchecked.
     */
    private void clearSavedCredentials() {
        editor.clear();
        editor.apply();
    }

    /**
     * Navigate to the TaskActivity.
     */
    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close current activity
    }

    private void navigateToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
