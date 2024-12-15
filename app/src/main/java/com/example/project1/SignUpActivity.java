package com.example.project1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailInput, firstNameInput, lastNameInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private DatabaseHelper dbHelper;
    private TextView signInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        dbHelper = new DatabaseHelper(this);

        signUpButton.setOnClickListener(v -> handleSignUp());
        signInLink.setOnClickListener(v -> handleSignInLink());

        // Add TextWatcher to password and confirm password fields for real-time validation
        passwordInput.addTextChangedListener(new PasswordTextWatcher());
        confirmPasswordInput.addTextChangedListener(new ConfirmPasswordTextWatcher());
    }

    /**
     * Initializes views from the layout.
     */
    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        signInLink = findViewById(R.id.signInLink);
    }

    /**
     * Handles the sign-up logic when the sign-up button is clicked.
     */
    private void handleSignUp() {
        String email = emailInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (validateInputs(email, firstName, lastName, password, confirmPassword)) {
            if (dbHelper.addUser(new User(email, firstName, lastName, password))) {
                showToast("Sign-Up Successful!");
                finish(); // Navigate back to login screen
            } else {
                showToast("Error! User already exists.");
            }
        }
    }
    /**
     * Handles the view switch when the sign-in link is clicked.
     */
    private void handleSignInLink() {
        // Navigate back to the login screen
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close current activity
    }

    /**
     * Validates the user input fields.
     */
    private boolean validateInputs(String email, String firstName, String lastName, String password, String confirmPassword) {
        resetFieldStates();

        return validateField(InputValidator.isValidEmail(email), emailInput, "Invalid email format")
                & validateField(InputValidator.isValidName(firstName), firstNameInput, "First name must be 5–20 characters")
                & validateField(InputValidator.isValidName(lastName), lastNameInput, "Last name must be 5–20 characters")
                & validateField(InputValidator.isValidPassword(password), passwordInput, "Password must be 6–12 characters with at least one uppercase letter, one lowercase letter, and one number")
                & validateField(InputValidator.validatePassword(password, confirmPassword), confirmPasswordInput, "Passwords do not match");
    }

    /**
     * Validates a single input field and sets an error message if invalid.
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
        setFieldDefaultState(firstNameInput);
        setFieldDefaultState(lastNameInput);
        setFieldDefaultState(passwordInput);
        setFieldDefaultState(confirmPasswordInput);
    }

    /**
     * Sets the default state for a single input field.
     */
    private void setFieldDefaultState(EditText inputField) {
        Drawable drawable = ContextCompat.getDrawable(SignUpActivity.this, R.drawable.edit_text_bg);
        inputField.setBackground(drawable);
    }

    /**
     * Shows a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * TextWatcher for real-time password validation.
     */
    private class PasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Optional: Handle if needed before text changes
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateField(InputValidator.isValidPassword(s.toString()), passwordInput, "Password must be 6–12 characters with at least one uppercase letter, one lowercase letter, and one number");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Optional: Handle if needed after text changes
        }
    }

    /**
     * TextWatcher for real-time confirm password validation.
     */
    private class ConfirmPasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Optional: Handle if needed before text changes
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String password = passwordInput.getText().toString().trim();
            validateField(InputValidator.validatePassword(password, s.toString()), confirmPasswordInput, "Passwords do not match");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Optional: Handle if needed after text changes
        }
    }
}
