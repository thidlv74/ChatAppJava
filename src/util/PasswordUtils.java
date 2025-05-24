package util;

import java.util.Random;

public class PasswordUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    public static String generateRandomPassword(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        
        return sb.toString();
    }
    
    public static void sendPasswordResetEmail(String email, String newPassword) {
        // In a real application, this would send an email
        // For this example, we'll just print to console
        System.out.println("=== Password Reset Email ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Password Reset");
        System.out.println("Body: Your new password is: " + newPassword);
        System.out.println("=========================");
    }
}
