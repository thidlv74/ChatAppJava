package view;

import controller.AuthController;
import util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

public class LoginForm extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnForgotPassword;
    private AuthController authController;
    
    public LoginForm() {
        authController = new AuthController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Đăng Nhập - Chat App");
        setSize(1000, 700);
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
//        JLabel logoLabel = new JLabel("💬");
//        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
//        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        mainContainer.add(logoLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Chào mừng trở lại!");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Đăng nhập để tiếp tục trò chuyện");
        subtitleLabel.setFont(UIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(subtitleLabel, gbc);
        
        // Login form card
        JPanel formCard = UIUtils.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 20, 0);
        
        // Email field
        JLabel emailLabel = new JLabel("Email");
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
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtEmail, formGbc);
        
        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(UIUtils.LABEL_FONT);
        passwordLabel.setForeground(UIUtils.TEXT_PRIMARY);
        formGbc.gridy = 2;
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(passwordLabel, formGbc);
        
        txtPassword = UIUtils.createStyledPasswordField("Nhập mật khẩu của bạn");
        formGbc.gridy = 3;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtPassword, formGbc);
        
        // Login button
        btnLogin = UIUtils.createPrimaryButton("Đăng Nhập");
        formGbc.gridy = 4;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(btnLogin, formGbc);
        
        // Forgot password link
        btnForgotPassword = UIUtils.createLinkButton("Quên mật khẩu?");
        formGbc.gridy = 5;
        formGbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnForgotPassword, formGbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(formCard, gbc);
        
        // Register section
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel registerText = new JLabel("Chưa có tài khoản?");
        registerText.setFont(UIUtils.LINK_FONT);
        registerText.setForeground(UIUtils.TEXT_SECONDARY);
        
        btnRegister = UIUtils.createLinkButton("Đăng ký ngay");
        
        registerPanel.add(registerText);
        registerPanel.add(btnRegister);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(registerPanel, gbc);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Add action listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterForm();
            }
        });
        
        btnForgotPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openResetPasswordForm();
            }
        });
        
        // Enter key listeners
        txtEmail.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> login());
    }
    
    private void login() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Clear placeholder text for validation
        if (email.equals("Nhập địa chỉ email của bạn")) {
            email = "";
        }
        if (password.equals("Nhập mật khẩu của bạn")) {
            password = "";
        }
        
        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        
        // Show loading state
        btnLogin.setText("Đang đăng nhập...");
        btnLogin.setEnabled(false);
        
        final String finalEmail = email;
        final String finalPassword = password;
        
        // Use SwingWorker for background processing
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return authController.loginWithEmail(finalEmail, finalPassword);
            }
            
            @Override
            protected void done() {
                try {
                    int loginResult = get();
                    
                    btnLogin.setText("Đăng Nhập");
                    btnLogin.setEnabled(true);
                    
                    switch (loginResult) {
                        case AuthController.LOGIN_SUCCESS:
                            showSuccessMessage("Đăng nhập thành công!");
                            openMainChatWindow(finalEmail);
                            break;
                        case AuthController.WRONG_PASSWORD:
                            showErrorMessage("Mật khẩu không chính xác");
                            break;
                        case AuthController.USER_NOT_FOUND:
                            int option = JOptionPane.showConfirmDialog(LoginForm.this, 
                                "Email không tồn tại. Bạn có muốn đăng ký tài khoản mới?", 
                                "Tài khoản không tồn tại", 
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                            
                            if (option == JOptionPane.YES_OPTION) {
                                openRegisterForm();
                            }
                            break;
                    }
                } catch (Exception e) {
                    btnLogin.setText("Đăng Nhập");
                    btnLogin.setEnabled(true);
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
    
    private void openRegisterForm() {
        RegisterForm registerForm = new RegisterForm();
        registerForm.setVisible(true);
        this.dispose();
    }
    
    private void openResetPasswordForm() {
        ResetPasswordForm resetForm = new ResetPasswordForm();
        resetForm.setVisible(true);
        this.dispose();
    }
    
    private void openMainChatWindow(String email) {
        MainChatWindow chatWindow = new MainChatWindow(email);
        chatWindow.setVisible(true);
        this.dispose();
    }
}
