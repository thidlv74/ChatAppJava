package view;

import dao.ContactDAO;
import dao.UserDAO;
import model.Contact;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FriendRequestDialog extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    private static final Color DARK_SECONDARY = new Color(45, 45, 45);
    private static final Color DARK_BORDER = new Color(60, 60, 60);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color SECONDARY_TEXT = new Color(150, 150, 150);
    private static final Color ACCENT_COLOR = new Color(0, 132, 255);
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    private User currentUser;
    private JList<Contact> requestsList;
    private DefaultListModel<Contact> requestsModel;
    private ContactDAO contactDAO;
    private UserDAO userDAO;
    private MainChatWindow parentWindow;
    
    public FriendRequestDialog(MainChatWindow parent, User currentUser) {
        super(parent, "Lời Mời Kết Bạn", true);
        this.parentWindow = parent;
        this.currentUser = currentUser;
        this.contactDAO = new ContactDAO();
        this.userDAO = new UserDAO();
        
        initComponents();
        loadFriendRequests();
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
        JLabel titleLabel = new JLabel("Lời Mời Kết Bạn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Requests panel
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBackground(DARK_BACKGROUND);
        
        requestsModel = new DefaultListModel<>();
        requestsList = new JList<>(requestsModel);
        requestsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsList.setCellRenderer(new FriendRequestCellRenderer());
        requestsList.setBackground(DARK_BACKGROUND);
        requestsList.setForeground(TEXT_COLOR);
        requestsList.setSelectionBackground(DARK_SECONDARY);
        requestsList.setSelectionForeground(TEXT_COLOR);
        requestsList.setFixedCellHeight(80);
        
        JScrollPane scrollPane = new JScrollPane(requestsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        
        requestsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(DARK_BACKGROUND);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(DARK_SECONDARY);
        closeButton.setForeground(TEXT_COLOR);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        buttonPanel.add(closeButton);
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(requestsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add action listeners
        closeButton.addActionListener(e -> dispose());
    }
    
    private void loadFriendRequests() {
        requestsModel.clear();
        List<Contact> pendingRequests = contactDAO.getPendingContactRequests(currentUser.getId());
        
        for (Contact request : pendingRequests) {
            requestsModel.addElement(request);
        }
        
        if (requestsModel.isEmpty()) {
            // Show empty state
            JLabel emptyLabel = new JLabel("Không có lời mời kết bạn nào");
            emptyLabel.setForeground(SECONDARY_TEXT);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        }
    }
    
    private void acceptFriendRequest(Contact request) {
        boolean success = contactDAO.acceptContact(currentUser.getId(), request.getContactUser().getId());
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Đã chấp nhận lời mời kết bạn từ " + request.getContactUser().getDisplayName(), 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Remove from list
            requestsModel.removeElement(request);
            
            // Refresh parent window
            if (parentWindow != null) {
                parentWindow.refreshContactList();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không thể chấp nhận lời mời kết bạn. Vui lòng thử lại sau.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rejectFriendRequest(Contact request) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn từ chối lời mời kết bạn từ " + request.getContactUser().getDisplayName() + "?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = contactDAO.removeContact(request.getUserId(), currentUser.getId());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã từ chối lời mời kết bạn từ " + request.getContactUser().getDisplayName(), 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Remove from list
                requestsModel.removeElement(request);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể từ chối lời mời kết bạn. Vui lòng thử lại sau.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom cell renderer for friend requests
    private class FriendRequestCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            
            if (!(value instanceof Contact)) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            
            Contact request = (Contact) value;
            User user = request.getContactUser();
            
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(new EmptyBorder(8, 10, 8, 10));
            
            if (isSelected) {
                panel.setBackground(DARK_SECONDARY);
            } else {
                panel.setBackground(DARK_BACKGROUND);
            }
            
            // Avatar
            JLabel avatarLabel = new JLabel();
            avatarLabel.setPreferredSize(new Dimension(50, 50));
            avatarLabel.setOpaque(true);
            avatarLabel.setBackground(getColorForUser(user.getDisplayName()));
            avatarLabel.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 18));
            avatarLabel.setText(user.getDisplayName().substring(0, 1).toUpperCase());
            
            // User info
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(panel.getBackground());
            
            JLabel nameLabel = new JLabel(user.getDisplayName());
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            JLabel usernameLabel = new JLabel("@" + user.getUsername() + " muốn kết bạn với bạn");
            usernameLabel.setForeground(SECONDARY_TEXT);
            usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            infoPanel.add(nameLabel);
            infoPanel.add(usernameLabel);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonPanel.setBackground(panel.getBackground());
            
            JButton acceptButton = new JButton("Chấp nhận");
            acceptButton.setBackground(SUCCESS_COLOR);
            acceptButton.setForeground(Color.WHITE);
            acceptButton.setFocusPainted(false);
            acceptButton.setBorderPainted(false);
            acceptButton.setFont(new Font("Arial", Font.BOLD, 12));
            acceptButton.setPreferredSize(new Dimension(80, 30));
            
            JButton rejectButton = new JButton("Từ chối");
            rejectButton.setBackground(DANGER_COLOR);
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setFocusPainted(false);
            rejectButton.setBorderPainted(false);
            rejectButton.setFont(new Font("Arial", Font.BOLD, 12));
            rejectButton.setPreferredSize(new Dimension(80, 30));
            
            buttonPanel.add(acceptButton);
            buttonPanel.add(rejectButton);
            
            // Add action listeners
            acceptButton.addActionListener(e -> acceptFriendRequest(request));
            rejectButton.addActionListener(e -> rejectFriendRequest(request));
            
            panel.add(avatarLabel, BorderLayout.WEST);
            panel.add(infoPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.EAST);
            
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
