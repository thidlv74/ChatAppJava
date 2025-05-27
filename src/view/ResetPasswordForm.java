package view;

import controller.AuthController;
import util.UIUtils;
import util.ValidationUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

public class ResetPasswordForm extends JFrame {
    private JLabel emailLabel;
    private JLabel otpLabel;
    private JLabel newPasswordLabel;
    private JTextField txtEmail;
    private JTextField txtOTP;
    private JPasswordField txtNewPassword;
    private JButton btnSendOTP;
    private JButton btnReset;
    private JButton btnBack;
    private AuthController authController;
    private boolean isOTPMode = false;
    private boolean isResetMode = false;

    public ResetPasswordForm() {
        authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Quên Mật Khẩu - Chat App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(UIUtils.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(UIUtils.BACKGROUND_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Quên mật khẩu?");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Nhập email để nhận mã OTP");
        subtitleLabel.setFont(UIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(subtitleLabel, gbc);

        JPanel formCard = UIUtils.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);

        emailLabel = new JLabel("Email");
        emailLabel.setFont(UIUtils.LABEL_FONT);
        emailLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(emailLabel, formGbc);

        txtEmail = UIUtils.createStyledTextField("Nhập địa chỉ email của bạn");
        formGbc.gridy = 1;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtEmail, formGbc);

        otpLabel = new JLabel("Mã OTP");
        otpLabel.setFont(UIUtils.LABEL_FONT);
        otpLabel.setForeground(UIUtils.TEXT_PRIMARY);
        otpLabel.setVisible(false);
        formGbc.gridy = 2;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(otpLabel, formGbc);

        txtOTP = UIUtils.createStyledTextField("Nhập mã OTP");
        txtOTP.setVisible(false);
        txtOTP.setEnabled(false);
        txtOTP.setColumns(6); // Đặt chiều rộng cho 6 số
        formGbc.gridy = 3;
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtOTP, formGbc);

        newPasswordLabel = new JLabel("Mật khẩu mới");
        newPasswordLabel.setFont(UIUtils.LABEL_FONT);
        newPasswordLabel.setForeground(UIUtils.TEXT_PRIMARY);
        newPasswordLabel.setVisible(false);
        formGbc.gridy = 4;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(newPasswordLabel, formGbc);

        txtNewPassword = UIUtils.createStyledPasswordField("Nhập mật khẩu mới");
        txtNewPassword.setVisible(false);
        txtNewPassword.setEnabled(false);
        formGbc.gridy = 5;
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtNewPassword, formGbc);

        btnSendOTP = UIUtils.createPrimaryButton("Gửi Mã OTP");
        formGbc.gridy = 6;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(btnSendOTP, formGbc);

        btnReset = UIUtils.createPrimaryButton("Đặt Lại Mật Khẩu");
        btnReset.setVisible(false);
        btnReset.setEnabled(false);
        formGbc.gridy = 7;
        formCard.add(btnReset, formGbc);

        btnBack = UIUtils.createSecondaryButton("Quay Lại Đăng Nhập");
        formGbc.gridy = 8;
        formGbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnBack, formGbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(formCard, gbc);

        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        helpPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        JLabel helpText = new JLabel("Bạn nhớ lại mật khẩu?");
        helpText.setFont(UIUtils.LINK_FONT);
        helpText.setForeground(UIUtils.TEXT_SECONDARY);

        JButton loginLink = UIUtils.createLinkButton("Đăng nhập ngay");
        loginLink.addActionListener(e -> backToLogin());

        helpPanel.add(helpText);
        helpPanel.add(loginLink);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(helpPanel, gbc);

        add(mainContainer, BorderLayout.CENTER);

        btnSendOTP.addActionListener(e -> sendOTP());
        btnReset.addActionListener(e -> resetPassword());
        btnBack.addActionListener(e -> backToLogin());
        txtEmail.addActionListener(e -> sendOTP());
        txtOTP.addActionListener(e -> resetPassword());
        txtNewPassword.addActionListener(e -> resetPassword());
    }

    private void sendOTP() {
        String email = txtEmail.getText().trim();
        if (email.equals("Nhập địa chỉ email của bạn")) {
            email = "";
        }

        if (email.isEmpty()) {
            showErrorMessage("Vui lòng nhập email");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showErrorMessage("Email không hợp lệ");
            return;
        }

        btnSendOTP.setText("Đang gửi...");
        btnSendOTP.setEnabled(false);

        final String finalEmail = email;
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return authController.generateAndSendOTP(finalEmail);
            }

            @Override
            protected void done() {
                try {
                    String otp = get();
                    btnSendOTP.setText("Gửi Mã OTP");
                    btnSendOTP.setEnabled(true);

                    if (otp != null) {
                        isOTPMode = true;
                        // Chuyển sang trang nhập OTP
                        txtEmail.setVisible(false); // Ẩn ô Email
                        emailLabel.setVisible(false); // Ẩn label Email
                        txtOTP.setVisible(true); // Hiển thị ô OTP
                        txtOTP.setEnabled(true); // Kích hoạt ô OTP
                        otpLabel.setVisible(true); // Hiện label OTP
                        btnSendOTP.setVisible(false); // Ẩn nút Gửi Mã OTP
                        btnReset.setVisible(true); // Hiển thị nút Đặt Lại Mật Khẩu
                        btnReset.setEnabled(true); // Kích hoạt nút Đặt Lại
                        txtOTP.setText(""); // Xóa nội dung cũ
                        txtOTP.requestFocus(); // Đưa con trỏ vào ô OTP
                        showSuccessMessage("Mã OTP đã được gửi đến email của bạn!");
                    } else {
                        showErrorMessage("Email không tồn tại trong hệ thống");
                    }
                } catch (Exception e) {
                    btnSendOTP.setText("Gửi Mã OTP");
                    btnSendOTP.setEnabled(true);
                    showErrorMessage("Có lỗi xảy ra. Vui lòng thử lại.");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void resetPassword() {
        if (!isOTPMode) {
            showErrorMessage("Vui lòng gửi mã OTP trước!");
            return;
        }

        String email = txtEmail.getText().trim(); // Lấy email từ lần nhập trước
        String otp = txtOTP.getText().trim();
        String newPassword = new String(txtNewPassword.getPassword()).trim();

        if (otp.equals("Nhập mã OTP") || otp.isEmpty()) {
            showErrorMessage("Vui lòng nhập mã OTP");
            return;
        }

        if (otp.length() != 6 || !otp.matches("\\d{6}")) {
            showErrorMessage("Mã OTP phải là 6 chữ số!");
            return;
        }

        if (!isResetMode) {
            // Kiểm tra OTP trước khi cho phép nhập mật khẩu mới
            int result = authController.verifyOTP(email, otp); // Kiểm tra OTP mà không đổi mật khẩu
            if (result == AuthController.OTP_SUCCESS) {
                isResetMode = true;
                txtOTP.setVisible(false); // Ẩn ô OTP
                otpLabel.setVisible(false); // Ẩn label OTP
                txtNewPassword.setVisible(true); // Hiển thị ô Mật khẩu mới
                txtNewPassword.setEnabled(true); // Kích hoạt ô Mật khẩu mới
                newPasswordLabel.setVisible(true); // Hiển thị label mật khẩu mới
                txtNewPassword.setText(""); // Xóa nội dung cũ
                txtNewPassword.requestFocus(); // Đưa con trỏ vào ô Mật khẩu mới
                showSuccessMessage("Mã OTP đúng. Vui lòng nhập mật khẩu mới!");
            } else {
                showErrorMessage("Mã OTP không đúng. Vui lòng kiểm tra lại!");
                return;
            }
        } else {
            // Xử lý đặt lại mật khẩu
            if (newPassword.equals("Nhập mật khẩu mới") || newPassword.isEmpty()) {
                showErrorMessage("Vui lòng nhập mật khẩu mới");
                return;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                showErrorMessage("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }

            btnReset.setText("Đang xử lý...");
            btnReset.setEnabled(false);

            final String finalEmail = email;
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    return authController.resetPassword(finalEmail, newPassword);
                }

                @Override
                protected void done() {
                    try {
                        int result = get();
                        btnReset.setText("Đặt Lại Mật Khẩu");
                        btnReset.setEnabled(true);

                        if (result == AuthController.OTP_SUCCESS) {
                            showSuccessDialog("Đặt lại mật khẩu thành công!", 
                                "Mật khẩu của bạn đã được cập nhật.\nBạn có thể đăng nhập ngay bây giờ.");
                        } else {
                            showErrorMessage("Có lỗi xảy ra!");
                            showErrorMessage("Mã lỗi: " + result);
                        }
                    } catch (Exception e) {
                        btnReset.setText("Đặt Lại Mật Khẩu");
                        btnReset.setEnabled(true);
                        showErrorMessage("Có lỗi xảy ra. Vui lòng thử lại.");
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuccessDialog(String title, String message) {
        int option = JOptionPane.showConfirmDialog(this, 
            message, 
            title, 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null);

        if (option == JOptionPane.OK_OPTION) {
            backToLogin();
        }
    }

    private void backToLogin() {
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        this.dispose();
    }
}