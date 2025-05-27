package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;

import java.util.HashMap;
import java.util.Map;

public class AuthController {
    public static final int LOGIN_SUCCESS = 1;
    public static final int WRONG_PASSWORD = 2;
    public static final int USER_NOT_FOUND = 3;
    public static final int REGISTER_SUCCESS = 1;
    public static final int USERNAME_EXISTS = 2;
    public static final int EMAIL_EXISTS = 3;
    public static final int REGISTER_FAILED = 4;
    public static final int OTP_SUCCESS = 5;
    public static final int OTP_FAILED = 6;

    private UserDAO userDAO;
    private Map<String, String> otpStorage;

    public AuthController() {
        userDAO = new UserDAO();
        otpStorage = new HashMap<>();
    }

    public int login(String username, String password) {
        if (userDAO.validateUser(username, password)) {
            return LOGIN_SUCCESS;
        } else if (userDAO.usernameExists(username)) {
            return WRONG_PASSWORD;
        } else {
            return USER_NOT_FOUND;
        }
    }

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

    public String generateAndSendOTP(String email) {
        if (userDAO.emailExists(email)) {
            String otp = PasswordUtils.generateOTP(6);
            otpStorage.put(email, otp);
            PasswordUtils.sendOTPEmail("kimcheese2310@gmail.com", otp); // Gửi đến email thực tế
            return otp;
        }
        return null;
    }

//    public int verifyOTPAndResetPassword(String email, String otp, String newPassword) {
//        String storedOTP = otpStorage.get(email);
//        System.out.println("Stored OTP: " + storedOTP);
//        System.out.println("Input OTP: " + otp);
//
//        if (storedOTP != null && storedOTP.equals(otp)) {
//            boolean updateResult = userDAO.updatePasswordByEmail(email, newPassword);
//            System.out.println("Update password result: " + updateResult);
//            if (updateResult) {
//                otpStorage.remove(email);
//                return OTP_SUCCESS;
//            } else {
//                System.out.println("Failed to update password in DB");
//            }
//        } else {
//            System.out.println("OTP không khớp hoặc OTP chưa tồn tại");
//        }
//        return OTP_FAILED;
//    }
    
    // Chỉ kiểm tra OTP, không đổi mật khẩu, không xóa OTP
    public int verifyOTP(String email, String otp) {
        String storedOTP = otpStorage.get(email);
        if (storedOTP != null && storedOTP.equals(otp.trim())) {
            return OTP_SUCCESS;
        }
        return OTP_FAILED;
    }

    // Đổi mật khẩu và xóa OTP
    public int resetPassword(String email, String newPassword) {
        if (userDAO.updatePasswordByEmail(email, newPassword)) {
            otpStorage.remove(email);
            return OTP_SUCCESS;
        }
        return OTP_FAILED;
    }



    public User getUser(String username) {
        return userDAO.getUser(username);
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
}