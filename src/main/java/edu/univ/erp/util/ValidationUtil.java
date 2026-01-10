package edu.univ.erp.util;

import edu.univ.erp.exception.ValidationException;


public class ValidationUtil {
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max);
        }
    }

    public static void validateRange(double value, double min, double max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max);
        }
    }

    public static void validateNonNegative(Double value, String fieldName) {
        validateNonNegative(value, fieldName, null);
    }

    public static void validateNonNegative(Double value, String fieldName, String customMessage) {
        if (value != null && value < 0) {
            String message = customMessage != null ? customMessage : fieldName + " must be greater than or equal to 0";
            throw new ValidationException(message);
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validateUsername(String username) {
        validateNotEmpty(username, "Username");
        if (username.length() < 3 || username.length() > 50) {
            throw new ValidationException("Username must be between 3 and 50 characters");
        }
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }
    }

    public static void validatePassword(String password) {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }

    public static void validateRole(String role) {
        validateNotEmpty(role, "Role");
        if (!role.equalsIgnoreCase("ADMIN") && 
            !role.equalsIgnoreCase("INSTRUCTOR") && 
            !role.equalsIgnoreCase("STUDENT")) {
            throw new ValidationException("Invalid role. Must be ADMIN, INSTRUCTOR, or STUDENT");
        }
    }
}

