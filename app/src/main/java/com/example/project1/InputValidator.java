package com.example.project1;

import java.util.regex.Pattern;

public  class InputValidator {

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$");
        return emailPattern.matcher(email).matches();
    }
    public static boolean isValidPassword(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,12}$");
        return passwordPattern.matcher(password).matches();
    }
    public static boolean isValidName(String name) {
        //the name should be between 5 and 20 characters inclusively
        return name.length() >= 5 && name.length() <= 20;
    }
    public static boolean validatePassword(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
}
