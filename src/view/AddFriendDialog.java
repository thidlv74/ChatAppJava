package view;

import controller.AuthController;
import dao.ContactDAO;
import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddFriendDialog extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    private static final Color DARK_SECONDARY = new Color(45, 45, 45);
    private static final Color DARK_BORDER = new Color(60, 60, 60);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color SECONDARY_TEXT = new Color(150, 150, 150);
    private static final Color ACCENT_COLOR = new Color(0, 132, 255);
    
    private User currentUser;
    private JTextField searchField;
    private JList<User> searchResultsList;
    private DefaultListModel<User> searchResultsModel;
    private JButton searchButton;
    private JButton addButton;
    private JButton cancelButton;
    private UserDAO userDAO;
    private ContactDAO contactDAO;
    private MainChatWindow parentWindow;
    
    public AddFriendDialog(MainChatWindow parent, User currentUser) {
        super(parent, "Thêm Bạn Bè", true);
        this.parentWindow = parent;
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.contactDAO = new ContactDAO();
        
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Tìm Kiếm và Thêm Bạn Bè");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(DARK_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel searchLabel = new JLabel("Tìm kiếm theo tên đăng nhập, tên hiển thị hoặc email:");
        searchLabel.setForeground(TEXT_COLOR);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 0));
        searchInputPanel.setBackground(DARK_BACKGROUND);
        
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(DARK_SECONDARY);
        searchField.setForeground(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        searchButton = new JButton("Tìm Kiếm");
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchButton, BorderLayout.EAST);
        
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DARK_BACKGROUND);
        
        JLabel resultsLabel = new JLabel("Kết quả tìm kiếm:");
        resultsLabel.setForeground(TEXT_COLOR);
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsList.setCellRenderer(new UserListCellRenderer());
        searchResultsList.setBackground(DARK_BACKGROUND);
        searchResultsList.setForeground(TEXT_COLOR);
        searchResultsList.setSelectionBackground(DARK_SECONDARY);
        searchResultsList.setSelectionForeground(TEXT_COLOR);
        searchResultsList.setFixedCellHeight(60);
        
        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        
        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(DARK_BACKGROUND);
        
        addButton = new JButton("Gửi Lời Mời Kết Bạn");
        addButton.setBackground(ACCENT_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setEnabled(false);
        
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(DARK_SECONDARY);
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                addButton.setEnabled(searchResultsList.getSelectedValue() != null);
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSelectedFriend();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập từ khóa tìm kiếm", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Clear previous results
        searchResultsModel.clear();
        
        // Search for users
        List<User> searchResults = userDAO.searchUsers(searchTerm);
        
        // Filter out current user and existing contacts
        for (User user : searchResults) {
            if (user.getId() != currentUser.getId() && !isAlreadyContact(user.getId())) {
                searchResultsModel.addElement(user);
            }
        }
        
        if (searchResultsModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy người dùng nào phù hợp", 
                "Kết quả tìm kiếm", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private boolean isAlreadyContact(int userId) {
        // Check if user is already a contact or has pending request
        return contactDAO.isContactExists(currentUser.getId(), userId);
    }
    
    private void addSelectedFriend() {
        User selectedUser = searchResultsList.getSelectedValue();
        if (selectedUser == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có muốn gửi lời mời kết bạn đến " + selectedUser.getDisplayName() + "?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = contactDAO.addContact(currentUser.getId(), selectedUser.getId());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã gửi lời mời kết bạn đến " + selectedUser.getDisplayName(), 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Remove from search results
                searchResultsModel.removeElement(selectedUser);
                addButton.setEnabled(false);
                
                // Refresh parent window if needed
                if (parentWindow != null) {
                    parentWindow.refreshContactList();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể gửi lời mời kết bạn. Vui lòng thử lại sau.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom cell renderer for user list
    private class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            
            if (!(value instanceof User)) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            
            User user = (User) value;
            
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(new EmptyBorder(8, 10, 8, 10));
            
            if (isSelected) {
                panel.setBackground(DARK_SECONDARY);
            } else {
                panel.setBackground(DARK_BACKGROUND);
            }
            
            // Avatar
            JLabel avatarLabel = new JLabel();
            avatarLabel.setPreferredSize(new Dimension(40, 40));
            avatarLabel.setOpaque(true);
            avatarLabel.setBackground(getColorForUser(user.getDisplayName()));
            avatarLabel.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 16));
            avatarLabel.setText(user.getDisplayName().substring(0, 1).toUpperCase());
            
            // User info
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(panel.getBackground());
            
            JLabel nameLabel = new JLabel(user.getDisplayName());
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            JLabel usernameLabel = new JLabel("@" + user.getUsername());
            usernameLabel.setForeground(SECONDARY_TEXT);
            usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            infoPanel.add(nameLabel);
            infoPanel.add(usernameLabel);
            
            panel.add(avatarLabel, BorderLayout.WEST);
            panel.add(infoPanel, BorderLayout.CENTER);
            
            return panel;
        }
        
        private Color getColorForUser(String name) {
            // Generate consistent color based on name
            int hash = name.hashCode();
            int r = 100 + Math.abs(hash % 100);
            int g = 100 + Math.abs((hash >> 8) % 100);
            int b = 100 + Math.abs((hash >> 16) % 100);
            return new Color(r, g, b);
        }
    }
}
