package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MessageBubbleRenderer extends JPanel {
    private static final Color SENDER_COLOR = new Color(60, 60, 60);
    private static final Color RECEIVER_COLOR = new Color(0, 132, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final int ARC_SIZE = 15;
    private static final int PADDING = 10;
    
    private final String sender;
    private final String message;
    private final String time;
    private final boolean isOwnMessage;
    
    public MessageBubbleRenderer(String sender, String message, String time, boolean isOwnMessage) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.isOwnMessage = isOwnMessage;
        
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setLayout(new BorderLayout());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Calculate bubble dimensions
        int bubbleWidth = width - 20;
        int bubbleHeight = height - 20;
        int x = isOwnMessage ? width - bubbleWidth - 10 : 10;
        int y = 10;
        
        // Draw bubble
        g2d.setColor(isOwnMessage ? SENDER_COLOR : RECEIVER_COLOR);
        g2d.fill(new RoundRectangle2D.Float(x, y, bubbleWidth, bubbleHeight, ARC_SIZE, ARC_SIZE));
        
        // Draw text
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Draw sender name
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(sender, x + PADDING, y + 20);
        
        // Draw message
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        drawWrappedText(g2d, message, x + PADDING, y + 40, bubbleWidth - 2 * PADDING);
        
        // Draw time
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(200, 200, 200));
        
        FontMetrics fm = g2d.getFontMetrics();
        int timeWidth = fm.stringWidth(time);
        int timeX = isOwnMessage ? x + bubbleWidth - timeWidth - PADDING : x + PADDING;
        g2d.drawString(time, timeX, y + bubbleHeight - PADDING);
        
        g2d.dispose();
    }
    
    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        
        String[] words = text.split("\\s");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                g2d.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word + " ");
                currentY += lineHeight;
            }
        }
        
        // Draw the last line
        g2d.drawString(line.toString(), x, currentY);
    }
    
    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(new Font("Arial", Font.PLAIN, 14));
        int messageWidth = fm.stringWidth(message);
        int lines = 1 + messageWidth / (getWidth() - 40);
        
        return new Dimension(getWidth(), 60 + lines * fm.getHeight());
    }
}
