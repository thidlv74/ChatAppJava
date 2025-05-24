package view;

import controller.AuthController;
import util.UIUtils;
import util.ValidationUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

public class RegisterForm extends JFrame {
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnBack;
    private AuthController authController;
    
    public RegisterForm() {
        authController = new AuthController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Đăng Ký - Chat App");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set background
        getContentPane().setBackground(UIUtils.BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        // Main container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(UIUtils.BACKGROUND_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo/Icon
        JLabel logoLabel = new JLabel("🚀");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainContainer.add(logoLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Tạo tài khoản mới");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Tham gia cộng đồng chat ngay hôm nay");
        subtitleLabel.setFont(UIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(subtitleLabel, gbc);
        
        // Register form card
        JPanel formCard = UIUtils.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);
        
        // Username field
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(UIUtils.LABEL_FONT);
        usernameLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(usernameLabel, formGbc);
        
        txtUsername = UIUtils.createStyledTextField("Chọn tên đăng nhập");
        formGbc.gridy = 1;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtUsername, formGbc);
        
        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UIUtils.LABEL_FONT);
        emailLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridy = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(emailLabel, formGbc);
        
        txtEmail = UIUtils.createStyledTextField("Nhập địa chỉ email của bạn");
        formGbc.gridy = 3;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtEmail, formGbc);
        
        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(UIUtils.LABEL_FONT);
        passwordLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridy = 4;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(passwordLabel, formGbc);
        
        txtPassword = UIUtils.createStyledPasswordField("Tạo mật khẩu mạnh");
        formGbc.gridy = 5;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtPassword, formGbc);
        
        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu");
        confirmPasswordLabel.setFont(UIUtils.LABEL_FONT);
        confirmPasswordLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridy = 6;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(confirmPasswordLabel, formGbc);
        
        txtConfirmPassword = UIUtils.createStyledPasswordField("Nhập lại mật khẩu");
        formGbc.gridy = 7;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtConfirmPassword, formGbc);
        
        // Register button
        btnRegister = UIUtils.createPrimaryButton("Tạo Tài Khoản");
        formGbc.gridy = 8;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(btnRegister, formGbc);
        
        // Back button
        btnBack = UIUtils.createSecondaryButton("Quay Lại Đăng Nhập");
        formGbc.gridy = 9;
        formGbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnBack, formGbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(formCard, gbc);
        
        // Terms section
        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        termsPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel termsText = new JLabel("<html><center>Bằng việc đăng ký, bạn đồng ý với<br>Điều khoản sử dụng và Chính sách bảo mật</center></html>");
        termsText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        termsText.setForeground(UIUtils.TEXT_SECONDARY);
        termsText.setHorizontalAlignment(SwingConstants.CENTER);
        
        termsPanel.add(termsText);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(termsPanel, gbc);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Add action listeners
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
        
        // Enter key navigation
        txtUsername.addActionListener(e -> txtEmail.requestFocus());
        txtEmail.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> txtConfirmPassword.requestFocus());
        txtConfirmPassword.addActionListener(e -> register());
    }
    
    private void register() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        // Clear placeholder text for validation
        if (username.equals("Chọn tên đăng nhập")) username = "";
        if (email.equals("Nhập địa chỉ email của bạn")) email = "";
        if (password.equals("Tạo mật khẩu mạnh")) password = "";
        if (confirmPassword.equals("Nhập lại mật khẩu")) confirmPassword = "";
        
        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            showErrorMessage("Email không hợp lệ");
            return;
        }
        
        if (password.length() < 6) {
            showErrorMessage("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Mật khẩu xác nhận không khớp");
            return;
        }
        
        // Show loading state
        btnRegister.setText("Đang tạo tài khoản...");
        btnRegister.setEnabled(false);
        
        final String finalUsername = username;
        final String finalEmail = email;
        final String finalPassword = password;
        
        // Use SwingWorker for background processing
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return authController.register(finalUsername, finalEmail, finalPassword);
            }
            
            @Override
            protected void done() {
                try {
                    int registerResult = get();
                    
                    btnRegister.setText("Tạo Tài Khoản");
                    btnRegister.setEnabled(true);
                    
                    switch (registerResult) {
                        case AuthController.REGISTER_SUCCESS:
                            showSuccessMessage("Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
                            backToLogin();
                            break;
                        case AuthController.USERNAME_EXISTS:
                            showErrorMessage("Tên đăng nhập đã tồn tại");
                            break;
                        case AuthController.EMAIL_EXISTS:
                            showErrorMessage("Email đã được sử dụng");
                            break;
                        case AuthController.REGISTER_FAILED:
                            showErrorMessage("Đăng ký thất bại. Vui lòng thử lại.");
                            break;
                    }
                } catch (Exception e) {
                    btnRegister.setText("Tạo Tài Khoản");
                    btnRegister.setEnabled(true);
                    showErrorMessage("Có lỗi xảy ra. Vui lòng thử lại.");
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void backToLogin() {
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        this.dispose();
    }
}
