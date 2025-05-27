package view;

import controller.ChatController;
import dao.ContactDAO;
import dao.GroupDAO;
import dao.MessageDAO;
import dao.UserDAO;
import model.*;
import util.UIUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import util.EmojiUtils;
import util.FileUtils;
import java.io.File;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

public class MainChatWindow extends JFrame {
    // Modern color scheme
    private static final Color PRIMARY_BACKGROUND = new Color(248, 249, 250);
    private static final Color SIDEBAR_BACKGROUND = new Color(255, 255, 255);
    private static final Color CHAT_BACKGROUND = new Color(255, 255, 255);
    private static final Color HEADER_BACKGROUND = new Color(255, 255, 255);
    private static final Color MESSAGE_BUBBLE_SENT = new Color(0, 132, 255);
    private static final Color MESSAGE_BUBBLE_RECEIVED = new Color(233, 236, 239);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color TEXT_MUTED = new Color(173, 181, 189);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color ACCENT_COLOR = new Color(0, 132, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color ONLINE_COLOR = new Color(34, 197, 94);
    
    // Fonts with emoji support
    private static final Font TITLE_FONT = EmojiUtils.getEmojiFont(20).deriveFont(Font.BOLD);
    private static final Font SUBTITLE_FONT = EmojiUtils.getEmojiFont(14);
    private static final Font MESSAGE_FONT = EmojiUtils.getEmojiFont(14);
    private static final Font SMALL_FONT = EmojiUtils.getEmojiFont(12);
    private static final Font BOLD_FONT = EmojiUtils.getEmojiFont(14).deriveFont(Font.BOLD);
    
    // Components
    private User currentUser;
    private JList<Object> contactList;
    private DefaultListModel<Object> contactModel;
    private JPanel chatMessagesPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel selectedUserLabel;
    private JLabel userStatusLabel;
    private JLabel selectedUserAvatar;
    private ChatController chatController;
    private Object currentContact = null;
    
    private JButton addFriendButton;
    private JButton friendRequestButton;
    private JTextField searchField;
    private JButton emojiButton;
    private JButton attachButton;
    private JPopupMenu emojiPopup;
    private JPopupMenu attachmentPopup;
    private Timer statusUpdateTimer;
    
    // DAOs
    private ContactDAO contactDAO;
    private GroupDAO groupDAO;
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    
    // Timer for checking new messages
    private Timer messageCheckTimer;
    private long lastMessageCheck = 0;
    
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
        
        initComponents();
        loadContactsAndGroups();
        startMessageCheckTimer();
        startStatusUpdateTimer(); // Add this line
        
        // Update user status to online
        userDAO.updateUserStatus(currentUser.getId(), "online");
    }
    
    private void startMessageCheckTimer() {
        // Check for new messages every 2 seconds
        messageCheckTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForNewMessages();
            }
        });
        messageCheckTimer.start();
        System.out.println("Message check timer started");
    }
    
    private void checkForNewMessages() {
        if (currentContact == null) {
            return;
        }
        
        try {
            if (currentContact instanceof Contact) {
                Contact contact = (Contact) currentContact;
                List<Message> messages = messageDAO.getPrivateMessageHistory(currentUser.getId(), contact.getContactUser().getId());
                
                // Check if there are new messages
                if (messages.size() > chatMessagesPanel.getComponentCount()) {
                    System.out.println("New messages detected, refreshing chat");
                    loadPrivateMessages(currentUser.getId(), contact.getContactUser().getId());
                }
            } else if (currentContact instanceof Group) {
                Group group = (Group) currentContact;
                List<Message> messages = messageDAO.getGroupMessageHistory(group.getId());
                
                // Check if there are new messages
                if (messages.size() > chatMessagesPanel.getComponentCount()) {
                    System.out.println("New group messages detected, refreshing chat");
                    loadGroupMessages(group.getId());
                }
            }
        } catch (Exception ex) {
            // Ignore errors in background check
        }
    }
    
    private void initComponents() {
        setTitle("Chat App - " + currentUser.getDisplayName());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(PRIMARY_BACKGROUND);
        
        // Create main layout
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(createSidebarPanel());
        mainSplitPane.setRightComponent(createChatPanel());
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setDividerSize(1);
        mainSplitPane.setBorder(null);
        mainSplitPane.setBackground(PRIMARY_BACKGROUND);
        
        mainContainer.add(mainSplitPane, BorderLayout.CENTER);
        add(mainContainer);
        
        // Connect to chat server
        chatController.connect();
        
        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                userDAO.updateUserLastSeen(currentUser.getId());
                userDAO.updateUserStatus(currentUser.getId(), "offline");
                chatController.disconnect();
                if (messageCheckTimer != null) {
                    messageCheckTimer.stop();
                }
                if (statusUpdateTimer != null) {
                    statusUpdateTimer.stop();
                }
            }
        });
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_BACKGROUND);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        sidebarPanel.setPreferredSize(new Dimension(350, 0));
        
        // Header with user info
        JPanel headerPanel = createSidebarHeader();
        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search bar
        JPanel searchPanel = createSearchPanel();
        sidebarPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = createActionPanel();
        sidebarPanel.add(actionPanel, BorderLayout.SOUTH);
        
        return sidebarPanel;
    }
    
    private JPanel createSidebarHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SIDEBAR_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(20, 20, 15, 20));
        
        // User avatar and info
        JPanel userInfoPanel = new JPanel(new BorderLayout(12, 0));
        userInfoPanel.setBackground(SIDEBAR_BACKGROUND);
        
        // Avatar
        JLabel avatarLabel = createAvatarLabel(currentUser.getDisplayName(), 45);
        userInfoPanel.add(avatarLabel, BorderLayout.WEST);
        
        // User details
        JPanel userDetailsPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        userDetailsPanel.setBackground(SIDEBAR_BACKGROUND);
        
        JLabel nameLabel = new JLabel(currentUser.getDisplayName());
        nameLabel.setFont(TITLE_FONT);
        nameLabel.setForeground(TEXT_PRIMARY);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setBackground(SIDEBAR_BACKGROUND);
        
        JLabel statusDot = new JLabel("●");
        statusDot.setForeground(ONLINE_COLOR);
        statusDot.setFont(new Font("Arial", Font.PLAIN, 8));
        
        JLabel statusLabel = new JLabel("Đang hoạt động");
        statusLabel.setFont(SMALL_FONT);
        statusLabel.setForeground(TEXT_SECONDARY);
        
        statusPanel.add(statusDot);
        statusPanel.add(Box.createHorizontalStrut(5));
        statusPanel.add(statusLabel);
        
        userDetailsPanel.add(nameLabel);
        userDetailsPanel.add(statusPanel);
        
        userInfoPanel.add(userDetailsPanel, BorderLayout.CENTER);
        
        headerPanel.add(userInfoPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(SIDEBAR_BACKGROUND);
        
        // Search field
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setBackground(SIDEBAR_BACKGROUND);
        searchFieldPanel.setBorder(new EmptyBorder(0, 20, 15, 20));
        
        searchField = new JTextField();
        searchField.setFont(SUBTITLE_FONT);
        searchField.setBackground(PRIMARY_BACKGROUND);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 40)
        ));
        searchField.setText("Tìm kiếm cuộc trò chuyện...");
        searchField.setForeground(TEXT_MUTED);
        
        // Search icon
        JLabel searchIcon = EmojiUtils.createEmojiLabel("🔍", 14);
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        JPanel searchInputPanel = new JPanel(new BorderLayout());
        searchInputPanel.setBackground(PRIMARY_BACKGROUND);
        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchIcon, BorderLayout.EAST);
        
        searchFieldPanel.add(searchInputPanel, BorderLayout.CENTER);
        searchContainer.add(searchFieldPanel, BorderLayout.NORTH);
        
        // Contact list
        contactModel = new DefaultListModel<>();
        contactList = new JList<>(contactModel);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setCellRenderer(new ModernContactListCellRenderer());
        contactList.setBackground(SIDEBAR_BACKGROUND);
        contactList.setSelectionBackground(PRIMARY_BACKGROUND);
        contactList.setFixedCellHeight(70);
        contactList.setBorder(null);
        
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        contactScrollPane.setBorder(null);
        contactScrollPane.getViewport().setBackground(SIDEBAR_BACKGROUND);
        contactScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        searchContainer.add(contactScrollPane, BorderLayout.CENTER);
        
        // Add listeners
        setupSearchFieldListeners();
        setupContactListListeners();
        
        return searchContainer;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        actionPanel.setBackground(SIDEBAR_BACKGROUND);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        addFriendButton = createModernButton("➕ Thêm bạn", ACCENT_COLOR, Color.WHITE);
        friendRequestButton = createModernButton("📩 Lời mời", PRIMARY_BACKGROUND, TEXT_PRIMARY);
        
        // Đảm bảo font hỗ trợ emoji
        addFriendButton.setFont(EmojiUtils.getEmojiFont(13).deriveFont(Font.BOLD));
        friendRequestButton.setFont(EmojiUtils.getEmojiFont(13).deriveFont(Font.BOLD));
        
        actionPanel.add(addFriendButton);
        actionPanel.add(friendRequestButton);
        
        // Add action listeners
        addFriendButton.addActionListener(e -> openAddFriendDialog());
        friendRequestButton.addActionListener(e -> openFriendRequestDialog());
        
        return actionPanel;
    }
    
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(CHAT_BACKGROUND);
        
        // Chat header
        JPanel chatHeader = createChatHeader();
        chatPanel.add(chatHeader, BorderLayout.NORTH);
        
        // Messages area
        JPanel messagesContainer = createMessagesContainer();
        chatPanel.add(messagesContainer, BorderLayout.CENTER);
        
        // Message input
        JPanel messageInputPanel = createMessageInputPanel();
        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);
        
        return chatPanel;
    }
    
    private JPanel createChatHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(15, 25, 15, 25)
        ));
        
        // Contact info
        JPanel contactInfoPanel = new JPanel(new BorderLayout(12, 0));
        contactInfoPanel.setBackground(HEADER_BACKGROUND);
        
        // Avatar
        selectedUserAvatar = createAvatarLabel("?", 40);
        contactInfoPanel.add(selectedUserAvatar, BorderLayout.WEST);
        
        // Contact details
        JPanel contactDetailsPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        contactDetailsPanel.setBackground(HEADER_BACKGROUND);
        
        selectedUserLabel = new JLabel("Chọn một cuộc trò chuyện");
        selectedUserLabel.setFont(BOLD_FONT);
        selectedUserLabel.setForeground(TEXT_PRIMARY);
        
        userStatusLabel = new JLabel("Chưa có cuộc trò chuyện nào được chọn");
        userStatusLabel.setFont(SMALL_FONT);
        userStatusLabel.setForeground(TEXT_SECONDARY);
        
        contactDetailsPanel.add(selectedUserLabel);
        contactDetailsPanel.add(userStatusLabel);
        
        contactInfoPanel.add(contactDetailsPanel, BorderLayout.CENTER);
        
        headerPanel.add(contactInfoPanel, BorderLayout.WEST);
        
        // Action buttons (call, video, info)
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionButtonsPanel.setBackground(HEADER_BACKGROUND);

        JButton callButton = createIconButton("📞");
        callButton.setPreferredSize(new Dimension(50, 45));
        JButton videoButton = createIconButton("📹");
        videoButton.setPreferredSize(new Dimension(50, 45));
        JButton infoButton = createIconButton("!️");
        infoButton.setPreferredSize(new Dimension(50, 45));

        actionButtonsPanel.add(callButton);
        actionButtonsPanel.add(videoButton);
        actionButtonsPanel.add(infoButton);
        
        headerPanel.add(actionButtonsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMessagesContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CHAT_BACKGROUND);
        
        // Messages panel with custom layout
        chatMessagesPanel = new JPanel();
        chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        chatMessagesPanel.setBackground(CHAT_BACKGROUND);
        chatMessagesPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Scroll pane
        chatScrollPane = new JScrollPane(chatMessagesPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.getViewport().setBackground(CHAT_BACKGROUND);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        container.add(chatScrollPane, BorderLayout.CENTER);
        
        return container;
    }
    
    private JPanel createMessageInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(CHAT_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        // Input container
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setBackground(CHAT_BACKGROUND);
        
        // Left buttons (emoji, attachment)
        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtonsPanel.setBackground(CHAT_BACKGROUND);
        
        emojiButton = createIconButton("😊");
        emojiButton.setPreferredSize(new Dimension(50, 45));
        attachButton = createIconButton("📎");
        attachButton.setPreferredSize(new Dimension(50, 45));
        
        leftButtonsPanel.add(emojiButton);
        leftButtonsPanel.add(attachButton);
        
        // Message field
        messageField = new JTextField();
        messageField.setFont(MESSAGE_FONT);
        messageField.setBackground(PRIMARY_BACKGROUND);
        messageField.setForeground(TEXT_PRIMARY);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        messageField.setText("Nhập tin nhắn...");
        messageField.setForeground(TEXT_MUTED);
        
        // Send button
        sendButton = new JButton("📤");
        sendButton.setFont(EmojiUtils.getEmojiFont(16));
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(50, 45));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add components
        inputContainer.add(leftButtonsPanel, BorderLayout.WEST);
        inputContainer.add(messageField, BorderLayout.CENTER);
        inputContainer.add(sendButton, BorderLayout.EAST);
        
        inputPanel.add(inputContainer, BorderLayout.CENTER);
        
        // Add listeners
        setupMessageInputListeners();
        setupEmojiAndAttachmentListeners();
        
        return inputPanel;
    }
    
    private void setupEmojiAndAttachmentListeners() {
        // Emoji button listener
        emojiButton.addActionListener(e -> showEmojiPopup());
        
        // Attachment button listener
        attachButton.addActionListener(e -> showAttachmentPopup());
    }

    private void showEmojiPopup() {
        if (emojiPopup == null) {
            emojiPopup = new JPopupMenu();
            emojiPopup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            
            EmojiPanel emojiPanel = new EmojiPanel(new EmojiPanel.EmojiSelectionListener() {
                @Override
                public void onEmojiSelected(String emoji) {
                    insertEmojiIntoMessage(emoji);
                    emojiPopup.setVisible(false);
                }
            });
            
            emojiPopup.add(emojiPanel);
        }
        
        emojiPopup.show(emojiButton, 0, -emojiPopup.getPreferredSize().height);
    }

    private void showAttachmentPopup() {
        if (attachmentPopup == null) {
            attachmentPopup = new JPopupMenu();
            attachmentPopup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            
            JMenuItem imageItem = new JMenuItem("📷 Hình ảnh");
            JMenuItem videoItem = new JMenuItem("🎬 Video");
            JMenuItem fileItem = new JMenuItem("📄 File");
            JMenuItem voiceItem = new JMenuItem("🎤 Tin nhắn thoại");
            
            imageItem.setFont(EmojiUtils.getEmojiFont(14));
            videoItem.setFont(EmojiUtils.getEmojiFont(14));
            fileItem.setFont(EmojiUtils.getEmojiFont(14));
            voiceItem.setFont(EmojiUtils.getEmojiFont(14));
            
            imageItem.addActionListener(e -> selectAndSendImage());
            videoItem.addActionListener(e -> selectAndSendVideo());
            fileItem.addActionListener(e -> selectAndSendFile());
            voiceItem.addActionListener(e -> recordVoiceMessage());
            
            attachmentPopup.add(imageItem);
            attachmentPopup.add(videoItem);
            attachmentPopup.add(fileItem);
            attachmentPopup.addSeparator();
            attachmentPopup.add(voiceItem);
        }
        
        attachmentPopup.show(attachButton, 0, -attachmentPopup.getPreferredSize().height);
    }

    private void insertEmojiIntoMessage(String emoji) {
        String currentText = messageField.getText();
        if (currentText.equals("Nhập tin nhắn...")) {
            messageField.setText(emoji);
            messageField.setForeground(TEXT_PRIMARY);
        } else {
            int caretPosition = messageField.getCaretPosition();
            String newText = currentText.substring(0, caretPosition) + emoji + currentText.substring(caretPosition);
            messageField.setText(newText);
            messageField.setCaretPosition(caretPosition + emoji.length());
        }
        messageField.requestFocus();
    }

    private void selectAndSendImage() {
        File imageFile = FileUtils.selectImageFile(this);
        if (imageFile != null) {
            sendFileMessage(imageFile, "image");
        }
    }

    private void selectAndSendVideo() {
        File videoFile = FileUtils.selectVideoFile(this);
        if (videoFile != null) {
            sendFileMessage(videoFile, "video");
        }
    }

    private void selectAndSendFile() {
        File file = FileUtils.selectAnyFile(this);
        if (file != null) {
            sendFileMessage(file, "file");
        }
    }

    private void recordVoiceMessage() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng ghi âm sẽ được phát triển trong phiên bản tiếp theo", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendFileMessage(File file, String messageType) {
        try {
            // Save file to uploads directory
            String savedPath = FileUtils.saveFile(file);
            
            // Create message
            Message message = new Message();
            message.setSenderId(currentUser.getId());
            message.setMessageType(messageType);
            message.setFileUrl(savedPath);
            message.setFileName(file.getName());
            message.setFileSize(file.length());
            message.setContent(file.getName()); // Use filename as content
            
            if (currentContact instanceof Contact) {
                Contact contact = (Contact) currentContact;
                message.setRecipientId(contact.getContactUser().getId());
            } else if (currentContact instanceof Group) {
                Group group = (Group) currentContact;
                message.setGroupId(group.getId());
            }
            
            // Save to database
            boolean sent = messageDAO.saveMessage(message);
            
            if (sent) {
                // Add file message bubble to UI
                addFileMessageBubble(message, true);
                
                // Refresh UI
                chatMessagesPanel.revalidate();
                chatMessagesPanel.repaint();
                
                // Scroll to bottom
                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
                
                System.out.println("File message sent: " + file.getName());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể gửi file. Vui lòng thử lại sau.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi gửi file: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addFileMessageBubble(Message message, boolean isOwnMessage) {
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setBackground(CHAT_BACKGROUND);
        messageContainer.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Message bubble
        JPanel messageBubble = new JPanel(new BorderLayout());
        messageBubble.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        if (isOwnMessage) {
            messageBubble.setBackground(MESSAGE_BUBBLE_SENT);
            messageContainer.add(messageBubble, BorderLayout.EAST);
        } else {
            messageBubble.setBackground(MESSAGE_BUBBLE_RECEIVED);
            messageContainer.add(messageBubble, BorderLayout.WEST);
        }
        
        // File content panel
        JPanel filePanel = new JPanel(new BorderLayout(10, 5));
        filePanel.setBackground(messageBubble.getBackground());
        
        // File icon and info
        JPanel fileInfoPanel = new JPanel(new BorderLayout(10, 0));
        fileInfoPanel.setBackground(messageBubble.getBackground());
        
        // File icon
        String emoji = EmojiUtils.getFileTypeEmoji(message.getFileName());
        JLabel fileIcon = new JLabel(emoji);
        fileIcon.setFont(EmojiUtils.getEmojiFont(24));
        
        // File details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.setBackground(messageBubble.getBackground());
        
        JLabel fileNameLabel = new JLabel(message.getFileName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fileNameLabel.setForeground(isOwnMessage ? Color.WHITE : TEXT_PRIMARY);
        
        JLabel fileSizeLabel = new JLabel(FileUtils.formatFileSize(message.getFileSize()));
        fileSizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        fileSizeLabel.setForeground(isOwnMessage ? new Color(255, 255, 255, 180) : TEXT_MUTED);
        
        detailsPanel.add(fileNameLabel);
        detailsPanel.add(fileSizeLabel);
        
        fileInfoPanel.add(fileIcon, BorderLayout.WEST);
        fileInfoPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // If it's an image, show thumbnail
        if (FileUtils.isImageFile(message.getFileName())) {
            ImageIcon thumbnail = FileUtils.createImageThumbnail(message.getFileUrl(), 150, 100);
            if (thumbnail != null) {
                JLabel thumbnailLabel = new JLabel(thumbnail);
                thumbnailLabel.setBorder(BorderFactory.createLineBorder(
                    isOwnMessage ? Color.WHITE : BORDER_COLOR, 1));
                filePanel.add(thumbnailLabel, BorderLayout.NORTH);
            }
        }
        
        filePanel.add(fileInfoPanel, BorderLayout.CENTER);
        
        // Add click listener to open file
        filePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                FileUtils.openFile(message.getFileUrl());
            }
        });
        
        messageBubble.add(filePanel, BorderLayout.CENTER);
        
        // Timestamp
        if (message.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            JLabel timeLabel = new JLabel(sdf.format(message.getTimestamp()));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(isOwnMessage ? new Color(255, 255, 255, 180) : TEXT_MUTED);
            timeLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
            messageBubble.add(timeLabel, BorderLayout.SOUTH);
        }
        
        chatMessagesPanel.add(messageContainer);
    }
    
    private void setupSearchFieldListeners() {
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm cuộc trò chuyện...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 14, 9, 39)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Tìm kiếm cuộc trò chuyện...");
                    searchField.setForeground(TEXT_MUTED);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 40)
                ));
            }
        });
    }
    
    private void setupContactListListeners() {
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
    }
    
    private void setupMessageInputListeners() {
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals("Nhập tin nhắn...")) {
                    messageField.setText("");
                    messageField.setForeground(TEXT_PRIMARY);
                }
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                    BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().trim().isEmpty()) {
                    messageField.setText("Nhập tin nhắn...");
                    messageField.setForeground(TEXT_MUTED);
                }
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        messageField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
    }
    
    private void startStatusUpdateTimer() {
        // Update status every 30 seconds
        statusUpdateTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateContactStatus();
            }
        });
        statusUpdateTimer.start();
    }

    private void updateContactStatus() {
        if (currentContact instanceof Contact) {
            Contact contact = (Contact) currentContact;
            User updatedUser = userDAO.getUserWithCurrentStatus(contact.getContactUser().getId());
            if (updatedUser != null) {
                contact.setContactUser(updatedUser);
                userStatusLabel.setText(getStatusText(updatedUser.getStatus(), updatedUser.getLastSeen()));
            }
        }
    }
    
    private JLabel createAvatarLabel(String name, int size) {
        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(size, size));
        avatar.setOpaque(true);
        avatar.setBackground(getColorForUser(name));
        avatar.setBorder(null);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        
        if (name != null && !name.isEmpty() && !name.equals("?")) {
            avatar.setText(name.substring(0, 1).toUpperCase());
        } else {
            avatar.setText("?");
        }
        
        return avatar;
    }
    
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor.equals(ACCENT_COLOR)) {
                    button.setBackground(bgColor.brighter());
                } else {
                    button.setBackground(BORDER_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JButton createIconButton(String icon) {
        JButton button = new JButton(icon);
        button.setFont(EmojiUtils.getEmojiFont(16));
        button.setBackground(PRIMARY_BACKGROUND);
        button.setForeground(TEXT_SECONDARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(40, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BORDER_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_BACKGROUND);
            }
        });
        
        return button;
    }
    
    private Color getColorForUser(String name) {
        if (name == null || name.isEmpty()) {
            return new Color(108, 117, 125);
        }
        
        // Generate consistent color based on name
        int hash = name.hashCode();
        Color[] colors = {
            new Color(255, 107, 107),
            new Color(54, 162, 235),
            new Color(255, 193, 7),
            new Color(40, 167, 69),
            new Color(220, 53, 69),
            new Color(102, 16, 242),
            new Color(255, 159, 64),
            new Color(75, 192, 192)
        };
        
        return colors[Math.abs(hash) % colors.length];
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
    
    private void selectContact(Object contact) {
        currentContact = contact;
        
        if (contact instanceof Contact) {
            Contact c = (Contact) contact;
            User contactUser = c.getContactUser();
            selectedUserLabel.setText(contactUser.getDisplayName());
            userStatusLabel.setText(getStatusText(contactUser.getStatus(), contactUser.getLastSeen()));
            
            // Update avatar
            selectedUserAvatar.setText(contactUser.getDisplayName().substring(0, 1).toUpperCase());
            selectedUserAvatar.setBackground(getColorForUser(contactUser.getDisplayName()));
            
            // Load private message history
            loadPrivateMessages(currentUser.getId(), contactUser.getId());
            
        } else if (contact instanceof Group) {
            Group g = (Group) contact;
            selectedUserLabel.setText(g.getName());
            userStatusLabel.setText(g.getMembers() != null ? g.getMembers().size() + " thành viên" : "Nhóm chat");
            
            // Update avatar
            selectedUserAvatar.setText(g.getName().substring(0, 1).toUpperCase());
            selectedUserAvatar.setBackground(getColorForUser(g.getName()));
            
            // Load group message history
            loadGroupMessages(g.getId());
        }
        
        // Reset message field
        if (messageField.getText().equals("Nhập tin nhắn...")) {
            messageField.setForeground(TEXT_MUTED);
        }
        
        // Update chat controller
        chatController.setCurrentRecipient(selectedUserLabel.getText());
    }
    
    private void loadPrivateMessages(int user1Id, int user2Id) {
        chatMessagesPanel.removeAll();
        
        List<Message> messages = messageDAO.getPrivateMessageHistory(user1Id, user2Id);
        
        for (Message message : messages) {
            boolean isOwnMessage = message.getSenderId() == currentUser.getId();
        
            if ("text".equals(message.getMessageType())) {
                addMessageBubble(message.getContent(), isOwnMessage, message.getTimestamp());
            } else {
                addFileMessageBubble(message, isOwnMessage);
            }
        }
        
        // Mark messages as read
        messageDAO.markConversationAsRead(currentUser.getId(), user2Id);
        
        // Refresh UI
        chatMessagesPanel.revalidate();
        chatMessagesPanel.repaint();
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void loadGroupMessages(int groupId) {
        chatMessagesPanel.removeAll();
        
        List<Message> messages = messageDAO.getGroupMessageHistory(groupId);
        
        for (Message message : messages) {
            boolean isOwnMessage = message.getSenderId() == currentUser.getId();
            addMessageBubble(message.getContent(), isOwnMessage, message.getTimestamp());
        }
        
        // Refresh UI
        chatMessagesPanel.revalidate();
        chatMessagesPanel.repaint();
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void addMessageBubble(String messageText, boolean isOwnMessage, java.sql.Timestamp timestamp) {
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setBackground(CHAT_BACKGROUND);
        messageContainer.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Message bubble
        JPanel messageBubble = new JPanel(new BorderLayout());
        messageBubble.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        if (isOwnMessage) {
            messageBubble.setBackground(MESSAGE_BUBBLE_SENT);
            messageContainer.add(messageBubble, BorderLayout.EAST);
        } else {
            messageBubble.setBackground(MESSAGE_BUBBLE_RECEIVED);
            messageContainer.add(messageBubble, BorderLayout.WEST);
        }
        
        // Message text
        JLabel messageLabel = new JLabel("<html><div style='width: 200px;'>" + messageText + "</div></html>");
        messageLabel.setFont(MESSAGE_FONT);
        messageLabel.setForeground(isOwnMessage ? Color.WHITE : TEXT_PRIMARY);
        messageBubble.add(messageLabel, BorderLayout.CENTER);
        
        // Timestamp
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            JLabel timeLabel = new JLabel(sdf.format(timestamp));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(isOwnMessage ? new Color(255, 255, 255, 180) : TEXT_MUTED);
            timeLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
            messageBubble.add(timeLabel, BorderLayout.SOUTH);
        }
        
        chatMessagesPanel.add(messageContainer);
    }
    
    private String getStatusText(String status, java.sql.Timestamp lastSeen) {
        String statusEmoji = EmojiUtils.getStatusEmoji(status != null ? status : "offline");
        
        if ("online".equals(status)) {
            return statusEmoji + " Đang hoạt động";
        } else if ("away".equals(status)) {
            return statusEmoji + " Vắng mặt";
        } else if ("busy".equals(status)) {
            return statusEmoji + " Bận";
        } else {
            if (lastSeen != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                return statusEmoji + " Hoạt động lần cuối: " + sdf.format(lastSeen);
            }
            return statusEmoji + " Ngoại tuyến";
        }
    }
    
    private void sendMessage() {
        String messageContent = messageField.getText().trim();
        
        if (messageContent.isEmpty() || messageContent.equals("Nhập tin nhắn...")) {
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
                System.out.println("Sending message to user ID: " + contact.getContactUser().getId());
            } else if (currentContact instanceof Group) {
                Group group = (Group) currentContact;
                message.setGroupId(group.getId());
                System.out.println("Sending message to group ID: " + group.getId());
            }
            
            // Save message to database
            boolean sent = messageDAO.saveMessage(message);
            
            if (sent) {
                System.out.println("Message saved to database: " + messageContent);
                
                // Add message bubble to UI
                addMessageBubble(messageContent, true, new java.sql.Timestamp(System.currentTimeMillis()));
                
                // Clear message field
                messageField.setText("Nhập tin nhắn...");
                messageField.setForeground(TEXT_MUTED);
                
                // Refresh UI
                chatMessagesPanel.revalidate();
                chatMessagesPanel.repaint();
                
                // Scroll to bottom
                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
                
                // Send through chat controller
                chatController.sendMessage(message);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể gửi tin nhắn. Vui lòng thử lại sau.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    // Modern contact list cell renderer
    private class ModernContactListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            
            JPanel panel = new JPanel(new BorderLayout(12, 0));
            panel.setBorder(new EmptyBorder(12, 20, 12, 20));
            
            if (isSelected) {
                panel.setBackground(PRIMARY_BACKGROUND);
            } else {
                panel.setBackground(SIDEBAR_BACKGROUND);
            }
            
            // Avatar
            JLabel avatarLabel = null;
            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(panel.getBackground());
            
            if (value instanceof Contact) {
                Contact contact = (Contact) value;
                User contactUser = contact.getContactUser();
                
                avatarLabel = createAvatarLabel(contactUser.getDisplayName(), 45);
                
                // Name and preview
                JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
                textPanel.setBackground(panel.getBackground());
                
                JLabel nameLabel = new JLabel(contactUser.getDisplayName());
                nameLabel.setFont(BOLD_FONT);
                nameLabel.setForeground(TEXT_PRIMARY);
                
                // Get last message
                Message lastMessage = messageDAO.getLastMessage(currentUser.getId(), contactUser.getId());
                String preview = lastMessage != null ? lastMessage.getContent() : "Chưa có tin nhắn";
                if (preview.length() > 35) {
                    preview = preview.substring(0, 35) + "...";
                }
                
                JLabel previewLabel = new JLabel(preview);
                previewLabel.setFont(SMALL_FONT);
                previewLabel.setForeground(TEXT_SECONDARY);
                
                textPanel.add(nameLabel);
                textPanel.add(previewLabel);
                
                infoPanel.add(textPanel, BorderLayout.CENTER);
                
                // Unread count
                int unreadCount = messageDAO.getUnreadMessageCount(currentUser.getId(), contactUser.getId());
                if (unreadCount > 0) {
                    JPanel rightPanel = new JPanel(new BorderLayout());
                    rightPanel.setBackground(panel.getBackground());
                    
                    JLabel unreadLabel = new JLabel(String.valueOf(unreadCount));
                    unreadLabel.setOpaque(true);
                    unreadLabel.setBackground(ACCENT_COLOR);
                    unreadLabel.setForeground(Color.WHITE);
                    unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    unreadLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    unreadLabel.setPreferredSize(new Dimension(20, 20));
                    
                    rightPanel.add(unreadLabel, BorderLayout.NORTH);
                    infoPanel.add(rightPanel, BorderLayout.EAST);
                }
                
            } else if (value instanceof Group) {
                Group group = (Group) value;
                
                avatarLabel = createAvatarLabel(group.getName(), 45);
                
                // Name and preview
                JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
                textPanel.setBackground(panel.getBackground());
                
                JLabel nameLabel = new JLabel(group.getName());
                nameLabel.setFont(BOLD_FONT);
                nameLabel.setForeground(TEXT_PRIMARY);
                
                // Get last message
                Message lastMessage = messageDAO.getLastGroupMessage(group.getId());
                String preview = lastMessage != null ? 
                    lastMessage.getSenderDisplayName() + ": " + lastMessage.getContent() : 
                    "Chưa có tin nhắn";
                if (preview.length() > 35) {
                    preview = preview.substring(0, 35) + "...";
                }
                
                JLabel previewLabel = new JLabel(preview);
                previewLabel.setFont(SMALL_FONT);
                previewLabel.setForeground(TEXT_SECONDARY);
                
                textPanel.add(nameLabel);
                textPanel.add(previewLabel);
                
                infoPanel.add(textPanel, BorderLayout.CENTER);
            }
            
            if (avatarLabel != null) {
                panel.add(avatarLabel, BorderLayout.WEST);
            }
            panel.add(infoPanel, BorderLayout.CENTER);
            
            return panel;
        }
    }
}
