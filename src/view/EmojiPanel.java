package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EmojiPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color HOVER_COLOR = new Color(245, 247, 250);
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 20);
    
    private EmojiSelectionListener listener;
    
    // Emoji categories
    private static final String[][] EMOJI_CATEGORIES = {
        // Smileys & People
        {"😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
         "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
         "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩"},
        
        // Hearts & Symbols
        {"❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
         "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
         "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐"},
        
        // Activities & Objects
        {"⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
         "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳",
         "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛷", "⛸️"},
        
        // Food & Drink
        {"🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
         "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
         "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🥔"}
    };
    
    private static final String[] CATEGORY_NAMES = {
        "Biểu cảm", "Trái tim", "Hoạt động", "Đồ ăn"
    };
    
    public interface EmojiSelectionListener {
        void onEmojiSelected(String emoji);
    }
    
    public EmojiPanel(EmojiSelectionListener listener) {
        this.listener = listener;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(350, 300));
        
        // Create tabbed pane for categories
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        for (int i = 0; i < EMOJI_CATEGORIES.length; i++) {
            JPanel categoryPanel = createCategoryPanel(EMOJI_CATEGORIES[i]);
            tabbedPane.addTab(CATEGORY_NAMES[i], categoryPanel);
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Recently used section
//        JPanel recentPanel = createRecentPanel();
//        add(recentPanel, BorderLayout.NORTH);
    }
    
    private JPanel createCategoryPanel(String[] emojis) {
        JPanel panel = new JPanel(new GridLayout(0, 6, 5, 5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for (String emoji : emojis) {
            JButton emojiButton = createEmojiButton(emoji);
            panel.add(emojiButton);
        }
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
        
        return container;
    }
    
//    private JPanel createRecentPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(BACKGROUND_COLOR);
//        panel.setBorder(BorderFactory.createTitledBorder("Gần đây"));
//        
//        JPanel emojiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
//        emojiPanel.setBackground(BACKGROUND_COLOR);
//        
//        // Add some recent emojis
//        String[] recentEmojis = {"😊", "❤️", "👍", "😂", "🔥", "💯"};
//        for (String emoji : recentEmojis) {
//            JButton emojiButton = createEmojiButton(emoji);
//            emojiButton.setPreferredSize(new Dimension(35, 35));
//            emojiPanel.add(emojiButton);
//        }
//        
//        panel.add(emojiPanel, BorderLayout.CENTER);
//        panel.setPreferredSize(new Dimension(0, 70));
//        
//        return panel;
//    }
    
    private JButton createEmojiButton(String emoji) {
        JButton button = new JButton(emoji);
        button.setFont(EMOJI_FONT);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(40, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                button.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
        
        // Add click listener
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onEmojiSelected(emoji);
                }
            }
        });
        
        return button;
    }
}
