package util;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        // Simple email validation
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
    
    public static boolean isValidUsername(String username) {
        // Username should be at least 3 characters and contain only letters, numbers, and underscores
        return username != null && username.matches("^[a-zA-Z0-9_]{3,}$");
    }
    
    public static boolean isValidPassword(String password) {
        // Password should be at least 6 characters
        return password != null && password.length() >= 6;
    }
}
