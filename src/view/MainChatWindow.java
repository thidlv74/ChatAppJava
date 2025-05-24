package view;

import controller.ChatController;
import dao.ContactDAO;
import dao.GroupDAO;
import dao.MessageDAO;
import dao.UserDAO;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainChatWindow extends JFrame {
    // Constants
    private static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    private static final Color DARK_SECONDARY = new Color(45, 45, 45);
    private static final Color DARK_BORDER = new Color(60, 60, 60);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color SECONDARY_TEXT = new Color(150, 150, 150);
    private static final Color ACCENT_COLOR = new Color(0, 132, 255);
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Components
    private User currentUser;
    private JList<Object> contactList;
    private DefaultListModel<Object> contactModel;
    private JTextPane chatPane;
    private StyledDocument chatDocument;
    private JTextField messageField;
    private JButton sendButton;
    private JPanel userInfoPanel;
    private JLabel selectedUserLabel;
    private JLabel userStatusLabel;
    private ChatController chatController;
    private JPanel rightPanel;
    private JScrollPane chatScrollPane;
    private Object currentContact = null;
    
    private JButton addFriendButton;
    private JButton friendRequestButton;
    
    // DAOs
    private ContactDAO contactDAO;
    private GroupDAO groupDAO;
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    
    // Message input state
    private boolean isMessageFieldFocused = false;
    private final String MESSAGE_PLACEHOLDER = "Nhập tin nhắn...";
    
    public MainChatWindow(String email) {
        // Initialize DAOs
        this.contactDAO = new ContactDAO();
        this.groupDAO = new GroupDAO();
        this.messageDAO = new MessageDAO();
        this.userDAO = new UserDAO();
        
        // Get current user by email
        this.currentUser = userDAO.getUserByEmail(email);
        if (this.currentUser == null) {
            JOptionPane.showMessageDialog(null, "Không thể tải thông tin người dùng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        this.chatController = new ChatController(currentUser.getUsername());
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Panel.background", DARK_BACKGROUND);
            UIManager.put("OptionPane.background", DARK_BACKGROUND);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
        loadContactsAndGroups();
        
        // Update user status to online
        userDAO.updateUserStatus(currentUser.getId(), "online");
    }
    
    private void initComponents() {
        setTitle("Ứng Dụng Chat - " + currentUser.getDisplayName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BACKGROUND);
        
        // Left panel - Contact list
        JPanel leftPanel = createLeftPanel();
        
        // Center panel - Chat area
        JPanel centerPanel = createCenterPanel();
        
        // Right panel - User info and media
        rightPanel = createRightPanel();
        
        // Add panels to main panel using JSplitPane for resizable panels
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        leftSplitPane.setDividerLocation(280);
        leftSplitPane.setDividerSize(1);
        leftSplitPane.setBorder(null);
        leftSplitPane.setBackground(DARK_BACKGROUND);
        
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, rightPanel);
        rightSplitPane.setDividerLocation(900);
        rightSplitPane.setDividerSize(1);
        rightSplitPane.setBorder(null);
        rightSplitPane.setBackground(DARK_BACKGROUND);
        
        mainPanel.add(rightSplitPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Connect to chat server
        chatController.connect();
        
        // Add window listener to disconnect when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                // Update user status to offline
                userDAO.updateUserStatus(currentUser.getId(), "offline");
                chatController.disconnect();
            }
        });
    }
    
    private void loadContactsAndGroups() {
        contactModel.clear();
        
        // Load contacts
        List<Contact> contacts = contactDAO.getUserContacts(currentUser.getId());
        for (Contact contact : contacts) {
            contactModel.addElement(contact);
        }
        
        // Load groups
        List<Group> groups = groupDAO.getUserGroups(currentUser.getId());
        for (Group group : groups) {
            contactModel.addElement(group);
        }
        
        // Select first item if available
        if (contactModel.getSize() > 0) {
            SwingUtilities.invokeLater(() -> {
                contactList.setSelectedIndex(0);
                selectContact(contactModel.getElementAt(0));
            });
        }
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(280, getHeight()));
        leftPanel.setBackground(DARK_BACKGROUND);
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DARK_BORDER));
        
        // Top panel with user info and search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BACKGROUND);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // User profile at top
        JPanel userProfilePanel = new JPanel(new BorderLayout(10, 0));
        userProfilePanel.setBackground(DARK_BACKGROUND);
        
        // User avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setBackground(new Color(100, 100, 100));
        avatarLabel.setOpaque(true);
        avatarLabel.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
        avatarLabel.setText(currentUser.getDisplayName().substring(0, 1).toUpperCase());
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setForeground(TEXT_COLOR);
        
        userProfilePanel.add(avatarLabel, BorderLayout.WEST);
        
        // App name
        JLabel appNameLabel = new JLabel("Chat App - " + currentUser.getDisplayName());
        appNameLabel.setForeground(TEXT_COLOR);
        appNameLabel.setFont(BOLD_FONT);
        userProfilePanel.add(appNameLabel, BorderLayout.CENTER);
        
        topPanel.add(userProfilePanel, BorderLayout.NORTH);
        
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(DARK_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
        
        JTextField searchField = new JTextField();
        searchField.setText("Tìm kiếm");
        searchField.setForeground(SECONDARY_TEXT);
        searchField.setBackground(DARK_SECONDARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 25, 5, 5)
        ));
        
        // Search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(SECONDARY_TEXT);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        leftPanel.add(topPanel, BorderLayout.NORTH);
        
        // Contact list
        contactModel = new DefaultListModel<>();
        contactList = new JList<>(contactModel);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setCellRenderer(new ContactGroupListCellRenderer());
        contactList.setBackground(DARK_BACKGROUND);
        contactList.setForeground(TEXT_COLOR);
        contactList.setSelectionBackground(DARK_SECONDARY);
        contactList.setSelectionForeground(TEXT_COLOR);
        contactList.setFixedCellHeight(70);
        
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        contactScrollPane.setBorder(null);
        contactScrollPane.getViewport().setBackground(DARK_BACKGROUND);
        
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);
        
        // Bottom panel with navigation icons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(DARK_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DARK_BORDER));

        addFriendButton = new JButton("➕ Thêm bạn");
        addFriendButton.setBackground(ACCENT_COLOR);
        addFriendButton.setForeground(Color.WHITE);
        addFriendButton.setFocusPainted(false);
        addFriendButton.setBorderPainted(false);
        addFriendButton.setFont(new Font("Arial", Font.BOLD, 12));

        friendRequestButton = new JButton("📩 Lời mời");
        friendRequestButton.setBackground(DARK_SECONDARY);
        friendRequestButton.setForeground(TEXT_COLOR);
        friendRequestButton.setFocusPainted(false);
        friendRequestButton.setBorderPainted(false);
        friendRequestButton.setFont(new Font("Arial", Font.BOLD, 12));

        bottomPanel.add(addFriendButton);
        bottomPanel.add(friendRequestButton);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners for new buttons
        addFriendButton.addActionListener(e -> openAddFriendDialog());
        friendRequestButton.addActionListener(e -> openFriendRequestDialog());
        
        // Add action listeners
        contactList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = contactList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Object contact = contactModel.getElementAt(index);
                        selectContact(contact);
                    }
                }
            }
        });
        
        // Focus listener for search field
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm");
                    searchField.setForeground(SECONDARY_TEXT);
                }
            }
        });
        
        return leftPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);
        
        // User info panel at top
        userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBackground(DARK_BACKGROUND);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, DARK_BORDER),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // User info with avatar
        JPanel userInfoWithAvatar = new JPanel(new BorderLayout(10, 0));
        userInfoWithAvatar.setBackground(DARK_BACKGROUND);
        
        // Avatar
        JLabel contactAvatarLabel = new JLabel();
        contactAvatarLabel.setPreferredSize(new Dimension(40, 40));
        contactAvatarLabel.setBackground(new Color(100, 100, 100));
        contactAvatarLabel.setOpaque(true);
        contactAvatarLabel.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
        contactAvatarLabel.setText("?");
        contactAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contactAvatarLabel.setForeground(TEXT_COLOR);
        
        userInfoWithAvatar.add(contactAvatarLabel, BorderLayout.WEST);
        
        // User info
        JPanel userTextInfo = new JPanel(new GridLayout(2, 1));
        userTextInfo.setBackground(DARK_BACKGROUND);
        
        selectedUserLabel = new JLabel("Chọn một cuộc trò chuyện");
        selectedUserLabel.setFont(BOLD_FONT);
        selectedUserLabel.setForeground(TEXT_COLOR);
        
        userStatusLabel = new JLabel("Chưa có cuộc trò chuyện nào được chọn");
        userStatusLabel.setFont(SMALL_FONT);
        userStatusLabel.setForeground(SECONDARY_TEXT);
        
        userTextInfo.add(selectedUserLabel);
        userTextInfo.add(userStatusLabel);
        
        userInfoWithAvatar.add(userTextInfo, BorderLayout.CENTER);
        
        userInfoPanel.add(userInfoWithAvatar, BorderLayout.WEST);
        
        centerPanel.add(userInfoPanel, BorderLayout.NORTH);
        
        // Chat area
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(DARK_BACKGROUND);
        chatDocument = chatPane.getStyledDocument();
        
        // Create styles for different message types
        createChatStyles();
        
        chatScrollPane = new JScrollPane(chatPane);
        chatScrollPane.setBorder(null);
        chatScrollPane.getViewport().setBackground(DARK_BACKGROUND);
        
        centerPanel.add(chatScrollPane, BorderLayout.CENTER);
        
        // Message input area
        JPanel messagePanel = createMessageInputPanel();
        centerPanel.add(messagePanel, BorderLayout.SOUTH);
        
        return centerPanel;
    }
    
    private JPanel createMessageInputPanel() {
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBackground(DARK_BACKGROUND);
        messagePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Message input and send
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(DARK_BACKGROUND);
        
        // Create message field with proper styling
        messageField = new JTextField();
        messageField.setFont(NORMAL_FONT);
        messageField.setBackground(DARK_SECONDARY);
        messageField.setForeground(SECONDARY_TEXT);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        messageField.setText(MESSAGE_PLACEHOLDER);
        messageField.setPreferredSize(new Dimension(0, 40));
        
        // Add focus listeners for placeholder functionality
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!isMessageFieldFocused && messageField.getText().equals(MESSAGE_PLACEHOLDER)) {
                    messageField.setText("");
                    messageField.setForeground(TEXT_COLOR);
                }
                isMessageFieldFocused = true;
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 9, 7, 9)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isMessageFieldFocused = false;
                if (messageField.getText().trim().isEmpty()) {
                    messageField.setText(MESSAGE_PLACEHOLDER);
                    messageField.setForeground(SECONDARY_TEXT);
                }
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_BORDER, 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });
        
        // Send button with better styling
        sendButton = new JButton("➤");
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(50, 40));
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for send button
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(ACCENT_COLOR.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(ACCENT_COLOR);
            }
        });
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        messagePanel.add(inputPanel, BorderLayout.CENTER);
        
        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addActionListener(e -> sendMessage());
        
        // Add key listener for better UX
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    messageField.transferFocus();
                }
            }
        });
        
        return messagePanel;
    }
    
    private void createChatStyles() {
        // Create styles for different message types
        Style defaultStyle = chatPane.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, "Arial");
        StyleConstants.setFontSize(defaultStyle, 14);
        
        Style timestampStyle = chatPane.addStyle("timestamp", defaultStyle);
        StyleConstants.setForeground(timestampStyle, SECONDARY_TEXT);
        StyleConstants.setAlignment(timestampStyle, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(timestampStyle, 12);
        
        Style senderStyle = chatPane.addStyle("sender", defaultStyle);
        StyleConstants.setForeground(senderStyle, new Color(180, 180, 180));
        StyleConstants.setBold(senderStyle, true);
        
        Style messageStyle = chatPane.addStyle("message", defaultStyle);
        StyleConstants.setForeground(messageStyle, TEXT_COLOR);
        
        Style ownMessageStyle = chatPane.addStyle("ownMessage", defaultStyle);
        StyleConstants.setForeground(ownMessageStyle, TEXT_COLOR);
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(280, getHeight()));
        rightPanel.setBackground(DARK_BACKGROUND);
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, DARK_BORDER));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel infoLabel = new JLabel("Thông tin hội thoại");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoLabel.setForeground(TEXT_COLOR);
        headerPanel.add(infoLabel, BorderLayout.CENTER);
        
        rightPanel.add(headerPanel, BorderLayout.NORTH);
        
        return rightPanel;
    }
    
    private void selectContact(Object contact) {
        currentContact = contact;
        
        if (contact instanceof Contact) {
            Contact c = (Contact) contact;
            User contactUser = c.getContactUser();
            selectedUserLabel.setText(contactUser.getDisplayName());
            userStatusLabel.setText(getStatusText(contactUser.getStatus(), contactUser.getLastSeen()));
            
            // Load private message history
            loadPrivateMessages(currentUser.getId(), contactUser.getId());
            
        } else if (contact instanceof Group) {
            Group g = (Group) contact;
            selectedUserLabel.setText(g.getName());
            userStatusLabel.setText(g.getMembers() != null ? g.getMembers().size() + " thành viên" : "Nhóm chat");
            
            // Load group message history
            loadGroupMessages(g.getId());
        }
        
        // Reset message field
        resetMessageField();
        
        // Update chat controller
        chatController.setCurrentRecipient(selectedUserLabel.getText());
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void resetMessageField() {
        if (!isMessageFieldFocused) {
            messageField.setText(MESSAGE_PLACEHOLDER);
            messageField.setForeground(SECONDARY_TEXT);
        }
    }
    
    private void loadPrivateMessages(int user1Id, int user2Id) {
        try {
            chatDocument.remove(0, chatDocument.getLength());
            
            List<Message> messages = messageDAO.getPrivateMessageHistory(user1Id, user2Id);
            
            for (Message message : messages) {
                boolean isOwnMessage = message.getSenderId() == currentUser.getId();
                String senderName = isOwnMessage ? "Bạn" : message.getSenderDisplayName();
                appendMessage(senderName, message.getContent(), isOwnMessage);
            }
            
            // Mark messages as read
            messageDAO.markConversationAsRead(currentUser.getId(), user2Id);
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void loadGroupMessages(int groupId) {
        try {
            chatDocument.remove(0, chatDocument.getLength());
            
            List<Message> messages = messageDAO.getGroupMessageHistory(groupId);
            
            for (Message message : messages) {
                boolean isOwnMessage = message.getSenderId() == currentUser.getId();
                String senderName = isOwnMessage ? "Bạn" : message.getSenderDisplayName();
                appendMessage(senderName, message.getContent(), isOwnMessage);
            }
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private String getStatusText(String status, java.sql.Timestamp lastSeen) {
        if ("online".equals(status)) {
            return "Đang hoạt động";
        } else if ("away".equals(status)) {
            return "Vắng mặt";
        } else if ("busy".equals(status)) {
            return "Bận";
        } else {
            if (lastSeen != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                return "Hoạt động lần cuối: " + sdf.format(lastSeen);
            }
            return "Ngoại tuyến";
        }
    }
    
    private void sendMessage() {
        String messageContent = getMessageText();
        
        if (messageContent.isEmpty()) {
            return;
        }
        
        if (currentContact == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một cuộc trò chuyện trước khi gửi tin nhắn", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Message message = new Message();
            message.setSenderId(currentUser.getId());
            message.setContent(messageContent);
            
            if (currentContact instanceof Contact) {
                Contact contact = (Contact) currentContact;
                message.setRecipientId(contact.getContactUser().getId());
            } else if (currentContact instanceof Group) {
                Group group = (Group) currentContact;
                message.setGroupId(group.getId());
            }
            
            // Save message to database
            boolean sent = messageDAO.saveMessage(message);
            
            if (sent) {
                // Display the message in the chat area
                appendMessage("Bạn", messageContent, true);
                
                // Clear the message field
                clearMessageField();
                
                // Send through chat controller (for real-time communication)
                chatController.sendMessage(message);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể gửi tin nhắn. Vui lòng thử lại sau.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private String getMessageText() {
        String text = messageField.getText().trim();
        if (text.equals(MESSAGE_PLACEHOLDER)) {
            return "";
        }
        return text;
    }
    
    private void clearMessageField() {
        messageField.setText("");
        if (!isMessageFieldFocused) {
            messageField.setText(MESSAGE_PLACEHOLDER);
            messageField.setForeground(SECONDARY_TEXT);
        } else {
            messageField.setForeground(TEXT_COLOR);
        }
    }
    
    private void appendMessage(String sender, String message, boolean isOwnMessage) throws BadLocationException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timestamp = sdf.format(new java.util.Date());
        
        // Add a new line if document is not empty
        if (chatDocument.getLength() > 0) {
            chatDocument.insertString(chatDocument.getLength(), "\n", null);
        }
        
        // Add sender and message
        chatDocument.insertString(chatDocument.getLength(), sender + " (" + timestamp + "): ", chatPane.getStyle("sender"));
        chatDocument.insertString(chatDocument.getLength(), message, isOwnMessage ? chatPane.getStyle("ownMessage") : chatPane.getStyle("message"));
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void openAddFriendDialog() {
        AddFriendDialog dialog = new AddFriendDialog(this, currentUser);
        dialog.setVisible(true);
    }

    private void openFriendRequestDialog() {
        FriendRequestDialog dialog = new FriendRequestDialog(this, currentUser);
        dialog.setVisible(true);
    }

    public void refreshContactList() {
        loadContactsAndGroups();
    }
    
    // Inner class for rendering contacts and groups in the list
    private class ContactGroupListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
            avatarLabel.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            // Info panel
            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(panel.getBackground());
            
            JPanel nameTimePanel = new JPanel(new BorderLayout());
            nameTimePanel.setBackground(panel.getBackground());
            
            if (value instanceof Contact) {
                Contact contact = (Contact) value;
                User contactUser = contact.getContactUser();
                
                avatarLabel.setBackground(getColorForUser(contactUser.getDisplayName()));
                avatarLabel.setText(contactUser.getDisplayName().substring(0, 1).toUpperCase());
                
                JLabel nameLabel = new JLabel(contactUser.getDisplayName());
                nameLabel.setForeground(TEXT_COLOR);
                nameLabel.setFont(BOLD_FONT);
                
                // Get last message
                Message lastMessage = messageDAO.getLastMessage(currentUser.getId(), contactUser.getId());
                String preview = lastMessage != null ? lastMessage.getContent() : "Chưa có tin nhắn";
                if (preview.length() > 30) {
                    preview = preview.substring(0, 30) + "...";
                }
                
                JLabel previewLabel = new JLabel(preview);
                previewLabel.setForeground(SECONDARY_TEXT);
                previewLabel.setFont(SMALL_FONT);
                
                nameTimePanel.add(nameLabel, BorderLayout.CENTER);
                infoPanel.add(nameTimePanel, BorderLayout.NORTH);
                infoPanel.add(previewLabel, BorderLayout.CENTER);
                
                // Unread count
                int unreadCount = messageDAO.getUnreadMessageCount(currentUser.getId(), contactUser.getId());
                if (unreadCount > 0) {
                    JLabel unreadLabel = new JLabel(String.valueOf(unreadCount));
                    unreadLabel.setOpaque(true);
                    unreadLabel.setBackground(ACCENT_COLOR);
                    unreadLabel.setForeground(Color.WHITE);
                    unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    unreadLabel.setFont(new Font("Arial", Font.BOLD, 11));
                    unreadLabel.setPreferredSize(new Dimension(20, 20));
                    nameTimePanel.add(unreadLabel, BorderLayout.EAST);
                }
                
            } else if (value instanceof Group) {
                Group group = (Group) value;
                
                avatarLabel.setBackground(getColorForUser(group.getName()));
                avatarLabel.setText(group.getName().substring(0, 1).toUpperCase());
                
                JLabel nameLabel = new JLabel(group.getName());
                nameLabel.setForeground(TEXT_COLOR);
                nameLabel.setFont(BOLD_FONT);
                
                // Get last message
                Message lastMessage = messageDAO.getLastGroupMessage(group.getId());
                String preview = lastMessage != null ? 
                    lastMessage.getSenderDisplayName() + ": " + lastMessage.getContent() : 
                    "Chưa có tin nhắn";
                if (preview.length() > 30) {
                    preview = preview.substring(0, 30) + "...";
                }
                
                JLabel previewLabel = new JLabel(preview);
                previewLabel.setForeground(SECONDARY_TEXT);
                previewLabel.setFont(SMALL_FONT);
                
                nameTimePanel.add(nameLabel, BorderLayout.CENTER);
                infoPanel.add(nameTimePanel, BorderLayout.NORTH);
                infoPanel.add(previewLabel, BorderLayout.CENTER);
            }
            
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
