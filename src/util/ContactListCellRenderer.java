package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ContactListCellRenderer extends DefaultListCellRenderer {
    private static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    private static final Color DARK_SELECTED = new Color(45, 45, 45);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color SECONDARY_TEXT = new Color(150, 150, 150);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final Random random = new Random();
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));
        
        if (isSelected) {
            panel.setBackground(DARK_SELECTED);
        } else {
            panel.setBackground(DARK_BACKGROUND);
        }
        
        // Avatar panel
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(50, 50));
        avatarPanel.setBackground(panel.getBackground());
        
        // Avatar label
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setBackground(getRandomColor(value.toString()));
        avatarLabel.setOpaque(true);
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        
        String contactName = value.toString();
        String initial = contactName.substring(0, 1).toUpperCase();
        avatarLabel.setText(initial);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        
        // Contact info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(panel.getBackground());
        
        // Contact name and time
        JPanel nameTimePanel = new JPanel(new BorderLayout(5, 0));
        nameTimePanel.setBackground(panel.getBackground());
        
        JLabel nameLabel = new JLabel(contactName);
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        // Time based on contact name to keep it consistent
        String time = getTimeForContact(contactName);
        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(SECONDARY_TEXT);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        nameTimePanel.add(nameLabel, BorderLayout.CENTER);
        nameTimePanel.add(timeLabel, BorderLayout.EAST);
        
        // Message preview
        JPanel previewPanel = new JPanel(new BorderLayout(5, 0));
        previewPanel.setBackground(panel.getBackground());
        
        // Message preview based on contact name
        String preview = getPreviewForContact(contactName);
        
        JLabel previewLabel = new JLabel(preview);
        previewLabel.setForeground(SECONDARY_TEXT);
        previewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Unread count for some contacts
        if (shouldShowUnread(contactName)) {
            int unreadCount = getUnreadCountForContact(contactName);
            JLabel unreadLabel = new JLabel(String.valueOf(unreadCount));
            unreadLabel.setOpaque(true);
            unreadLabel.setBackground(new Color(0, 132, 255));
            unreadLabel.setForeground(Color.WHITE);
            unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);
            unreadLabel.setFont(new Font("Arial", Font.BOLD, 11));
            unreadLabel.setPreferredSize(new Dimension(20, 20));
            unreadLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 132, 255), 1));
            
            previewPanel.add(previewLabel, BorderLayout.CENTER);
            previewPanel.add(unreadLabel, BorderLayout.EAST);
        } else {
            previewPanel.add(previewLabel, BorderLayout.CENTER);
        }
        
        infoPanel.add(nameTimePanel, BorderLayout.NORTH);
        infoPanel.add(previewPanel, BorderLayout.CENTER);
        
        panel.add(avatarPanel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Color getRandomColor(String seed) {
        // Generate consistent color based on contact name
        Random seededRandom = new Random(seed.hashCode());
        int r = 100 + seededRandom.nextInt(100);
        int g = 100 + seededRandom.nextInt(100);
        int b = 100 + seededRandom.nextInt(100);
        return new Color(r, g, b);
    }
    
    private String getTimeForContact(String contactName) {
        // Generate consistent time based on contact name
        if (contactName.equals("Thầy Thuận")) {
            return "19:48";
        } else if (contactName.contains("Lớp") || contactName.contains("BHD")) {
            return "13:05";
        } else if (contactName.contains("Ngọc")) {
            return "26 phút";
        } else if (contactName.contains("scholarship")) {
            return "3 giờ";
        } else {
            // Generate random time for other contacts
            Random seededRandom = new Random(contactName.hashCode());
            int hour = 8 + seededRandom.nextInt(12);
            int minute = seededRandom.nextInt(60);
            return String.format("%02d:%02d", hour, minute);
        }
    }
    
    private String getPreviewForContact(String contactName) {
        // Generate consistent preview based on contact name
        if (contactName.equals("Thầy Thuận")) {
            return "Ok em. Tập trung vỡ ôn tập để thi tốt trong mấy ngày tới đây nhé Vân Thi.";
        } else if (contactName.contains("Lớp")) {
            return "Nguyễn Phước: Dịu được Nguyễn Phước Dịu thêm vào nhóm";
        } else if (contactName.contains("BHD")) {
            return "Chị Ánh (4T): Chị Ánh tham gia họp";
        } else if (contactName.contains("Ngọc")) {
            return "[Thiệp] Gửi lời chào Nguyễn Thị Ngọc";
        } else if (contactName.contains("scholarship")) {
            return "Lại Thanh Hoa: https://m.facebook.com/...";
        } else if (contactName.contains("Nhật")) {
            return "Ban: Cuộc gọi video đi";
        } else if (contactName.contains("Phụng")) {
            return "Ban: rồi";
        } else if (contactName.contains("Triển")) {
            return "Ban: hong có nhớ";
        } else if (contactName.contains("Luân")) {
            return "Ban: tới thầy =))))";
        } else if (contactName.contains("English")) {
            return "Thanh Thảo được Quốc Huy thêm vào";
        } else if (contactName.contains("Mom")) {
            return "Hình ảnh";
        } else if (contactName.contains("robocar")) {
            return "Phụng Nguyễn (ĐH): chạm vào nút đầu";
        } else {
            // Generate random preview for other contacts
            String[] previews = {
                "Xin chào, bạn khỏe không?",
                "Hẹn gặp lại sau nhé!",
                "Cảm ơn bạn rất nhiều.",
                "Ok, tôi sẽ gửi cho bạn sau.",
                "Bạn có thời gian không?",
                "Chúc mừng sinh nhật!",
                "Tôi đã nhận được rồi.",
                "Hôm nay thời tiết thế nào?",
                "Bạn đã ăn tối chưa?",
                "Gửi cho tôi file đó nhé."
            };
            
            Random seededRandom = new Random(contactName.hashCode());
            return previews[seededRandom.nextInt(previews.length)];
        }
    }
    
    private boolean shouldShowUnread(String contactName) {
        // Determine if contact should show unread count
        if (contactName.equals("Thầy Thuận") || 
            contactName.contains("Ngọc") || 
            contactName.contains("BHD")) {
            return true;
        }
        return false;
    }
    
    private int getUnreadCountForContact(String contactName) {
        // Generate consistent unread count based on contact name
        if (contactName.equals("Thầy Thuận")) {
            return 1;
        } else if (contactName.contains("BHD")) {
            return 3;
        } else if (contactName.contains("Ngọc")) {
            return 2;
        } else {
            return 1;
        }
    }
}
