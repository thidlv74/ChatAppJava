package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;

public class AuthController {
    public static final int LOGIN_SUCCESS = 1;
    public static final int WRONG_PASSWORD = 2;
    public static final int USER_NOT_FOUND = 3;
    
    public static final int REGISTER_SUCCESS = 1;
    public static final int USERNAME_EXISTS = 2;
    public static final int EMAIL_EXISTS = 3;
    public static final int REGISTER_FAILED = 4;
    
    private UserDAO userDAO;
    
    public AuthController() {
        userDAO = new UserDAO();
    }
    
    // Original login method for backward compatibility
    public int login(String username, String password) {
        if (userDAO.validateUser(username, password)) {
            return LOGIN_SUCCESS;
        } else if (userDAO.usernameExists(username)) {
            return WRONG_PASSWORD;
        } else {
            return USER_NOT_FOUND;
        }
    }
    
    // New login method using email
    public int loginWithEmail(String email, String password) {
        if (userDAO.validateUserByEmail(email, password)) {
            return LOGIN_SUCCESS;
        } else if (userDAO.emailExists(email)) {
            return WRONG_PASSWORD;
        } else {
            return USER_NOT_FOUND;
        }
    }
    
    public int register(String username, String email, String password) {
        if (userDAO.usernameExists(username)) {
            return USERNAME_EXISTS;
        }
        
        if (userDAO.emailExists(email)) {
            return EMAIL_EXISTS;
        }
        
        boolean success = userDAO.registerUser(username, email, password);
        return success ? REGISTER_SUCCESS : REGISTER_FAILED;
    }
    
    public String resetPassword(String username, String email) {
        if (userDAO.validateUserEmail(username, email)) {
            // Generate new random password
            String newPassword = PasswordUtils.generateRandomPassword(8);
            
            // Update password in database
            if (userDAO.updatePassword(username, newPassword)) {
                // Send email with new password (simulated)
                String userEmail = userDAO.getEmail(username);
                PasswordUtils.sendPasswordResetEmail(userEmail, newPassword);
                
                return newPassword;
            }
        }
        
        return null;
    }
    
    // New reset password method using only email
    public String resetPasswordByEmail(String email) {
        if (userDAO.emailExists(email)) {
            // Generate new random password
            String newPassword = PasswordUtils.generateRandomPassword(8);
            
            // Update password in database
            if (userDAO.updatePasswordByEmail(email, newPassword)) {
                // Send email with new password (simulated)
                PasswordUtils.sendPasswordResetEmail(email, newPassword);
                
                return newPassword;
            }
        }
        
        return null;
    }
    
    public User getUser(String username) {
        return userDAO.getUser(username);
    }
    
    // New method to get user by email
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
}
