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
    private JTextField txtEmail;
    private JButton btnReset;
    private JButton btnBack;
    private AuthController authController;
    
    public ResetPasswordForm() {
        authController = new AuthController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Quên Mật Khẩu - Chat App");
        setSize(500, 550);
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
        JLabel logoLabel = new JLabel("🔑");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainContainer.add(logoLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Quên mật khẩu?");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>Không sao! Nhập email của bạn và chúng tôi<br>sẽ gửi mật khẩu mới cho bạn</center></html>");
        subtitleLabel.setFont(UIUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(UIUtils.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(subtitleLabel, gbc);
        
        // Reset form card
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
        formGbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(txtEmail, formGbc);
        
        // Reset button
        btnReset = UIUtils.createPrimaryButton("Gửi Mật Khẩu Mới");
        formGbc.gridy = 2;
        formGbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(btnReset, formGbc);
        
        // Back button
        btnBack = UIUtils.createSecondaryButton("Quay Lại Đăng Nhập");
        formGbc.gridy = 3;
        formGbc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnBack, formGbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(formCard, gbc);
        
        // Help section
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        helpPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        JLabel helpText = new JLabel("<html><center>Bạn nhớ lại mật khẩu?</center></html>");
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
        
        // Add action listeners
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
        
        // Enter key listener
        txtEmail.addActionListener(e -> resetPassword());
    }
    
    private void resetPassword() {
        String email = txtEmail.getText().trim();
        
        // Clear placeholder text for validation
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
        
        // Show loading state
        btnReset.setText("Đang gửi...");
        btnReset.setEnabled(false);
        
        final String finalEmail = email;
        
        // Use SwingWorker for background processing
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return authController.resetPasswordByEmail(finalEmail);
            }
            
            @Override
            protected void done() {
                try {
                    String newPassword = get();
                    
                    btnReset.setText("Gửi Mật Khẩu Mới");
                    btnReset.setEnabled(true);
                    
                    if (newPassword != null) {
                        showSuccessDialog("Mật khẩu mới đã được gửi!", 
                            "Mật khẩu mới đã được tạo và gửi đến email của bạn.\n" +
                            "Vui lòng kiểm tra email hoặc console để lấy mật khẩu mới.\n\n" +
                            "Bạn có thể đăng nhập ngay bây giờ.");
                    } else {
                        showErrorMessage("Email không tồn tại trong hệ thống");
                    }
                } catch (Exception e) {
                    btnReset.setText("Gửi Mật Khẩu Mới");
                    btnReset.setEnabled(true);
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
