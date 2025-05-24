package util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UIUtils {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(74, 144, 226);
    public static final Color PRIMARY_DARK = new Color(54, 104, 186);
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color BORDER_COLOR = new Color(222, 226, 230);
    public static final Color ERROR_COLOR = new Color(220, 53, 69);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color INPUT_BACKGROUND = new Color(248, 249, 250);
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LINK_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    public static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(createRoundedBorder(BORDER_COLOR, 8, 12));
        field.setPreferredSize(new Dimension(300, 45));
        
        // Add placeholder functionality
        field.setText(placeholder);
        field.setForeground(TEXT_SECONDARY);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(createRoundedBorder(PRIMARY_COLOR, 8, 12));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(createRoundedBorder(BORDER_COLOR, 8, 12));
            }
        });
        
        return field;
    }
    
    public static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(INPUT_FONT);
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(createRoundedBorder(BORDER_COLOR, 8, 12));
        field.setPreferredSize(new Dimension(300, 45));
        field.setEchoChar((char) 0); // Show placeholder text initially
        
        // Add placeholder functionality
        field.setText(placeholder);
        field.setForeground(TEXT_SECONDARY);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                    field.setEchoChar('•');
                }
                field.setBorder(createRoundedBorder(PRIMARY_COLOR, 8, 12));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(createRoundedBorder(BORDER_COLOR, 8, 12));
            }
        });
        
        return field;
    }
    
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2.setColor(PRIMARY_COLOR);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(300, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(BORDER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BORDER_COLOR);
                } else {
                    g2.setColor(CARD_BACKGROUND);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(300, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public static JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(LINK_FONT);
        button.setForeground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(PRIMARY_DARK);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    public static Border createRoundedBorder(Color color, int radius, int padding) {
        return BorderFactory.createCompoundBorder(
            new RoundedBorder(color, radius),
            BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }
    
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Add subtle shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        panel.setBackground(CARD_BACKGROUND);
        panel.setOpaque(false);
        return panel;
    }
    
    // Custom rounded border class
    static class RoundedBorder implements Border {
        private Color color;
        private int radius;
        
        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
