package util;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EmojiUtils {
    // Font hỗ trợ emoji
    public static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    public static final Font EMOJI_FONT_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 16);
    public static final Font EMOJI_FONT_SMALL = new Font("Segoe UI Emoji", Font.PLAIN, 12);
    
    // Map các emoji thường dùng
    private static final Map<String, String> EMOJI_MAP = new HashMap<>();
    
    static {
        // Emoji cơ bản
        EMOJI_MAP.put(":smile:", "😊");
        EMOJI_MAP.put(":happy:", "😄");
        EMOJI_MAP.put(":sad:", "😢");
        EMOJI_MAP.put(":love:", "❤️");
        EMOJI_MAP.put(":like:", "👍");
        EMOJI_MAP.put(":dislike:", "👎");
        EMOJI_MAP.put(":fire:", "🔥");
        EMOJI_MAP.put(":star:", "⭐");
        EMOJI_MAP.put(":heart:", "💖");
        EMOJI_MAP.put(":laugh:", "😂");
        EMOJI_MAP.put(":cry:", "😭");
        EMOJI_MAP.put(":angry:", "😠");
        EMOJI_MAP.put(":surprised:", "😲");
        EMOJI_MAP.put(":thinking:", "🤔");
        EMOJI_MAP.put(":cool:", "😎");
        EMOJI_MAP.put(":wink:", "😉");
        
        // Emoji hoạt động
        EMOJI_MAP.put(":call:", "📞");
        EMOJI_MAP.put(":video:", "📹");
        EMOJI_MAP.put(":message:", "💬");
        EMOJI_MAP.put(":send:", "📤");
        EMOJI_MAP.put(":receive:", "📥");
        EMOJI_MAP.put(":search:", "🔍");
        EMOJI_MAP.put(":add:", "➕");
        EMOJI_MAP.put(":remove:", "➖");
        EMOJI_MAP.put(":edit:", "✏️");
        EMOJI_MAP.put(":delete:", "🗑️");
        EMOJI_MAP.put(":settings:", "⚙️");
        EMOJI_MAP.put(":info:", "ℹ️");
        
        // Emoji trạng thái
        EMOJI_MAP.put(":online:", "🟢");
        EMOJI_MAP.put(":offline:", "⚫");
        EMOJI_MAP.put(":away:", "🟡");
        EMOJI_MAP.put(":busy:", "🔴");
        
        // Emoji thời gian
        EMOJI_MAP.put(":clock:", "🕐");
        EMOJI_MAP.put(":calendar:", "📅");
        EMOJI_MAP.put(":time:", "⏰");
        
        // Emoji file
        EMOJI_MAP.put(":file:", "📄");
        EMOJI_MAP.put(":image:", "🖼️");
        EMOJI_MAP.put(":music:", "🎵");
        EMOJI_MAP.put(":video_file:", "🎬");
        EMOJI_MAP.put(":folder:", "📁");
    }
    
    /**
     * Tạo JLabel với emoji
     */
    public static JLabel createEmojiLabel(String emoji, int fontSize) {
        JLabel label = new JLabel(emoji);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        return label;
    }
    
    /**
     * Tạo JButton với emoji
     */
    public static JButton createEmojiButton(String emoji, int fontSize) {
        JButton button = new JButton(emoji);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }
    
    /**
     * Chuyển đổi text shortcode thành emoji
     */
    public static String convertShortcodeToEmoji(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : EMOJI_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * Kiểm tra xem hệ thống có hỗ trợ emoji không
     */
    public static boolean isEmojiSupported() {
        try {
            Font font = new Font("Segoe UI Emoji", Font.PLAIN, 12);
            return font.canDisplay('😊');
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lấy font phù hợp cho emoji
     */
    public static Font getEmojiFont(int size) {
        // Thử các font hỗ trợ emoji theo thứ tự ưu tiên
        String[] emojiFont = {
            "Segoe UI Emoji",
            "Apple Color Emoji", 
            "Noto Color Emoji",
            "Android Emoji",
            "EmojiOne Color",
            "Twemoji Mozilla"
        };
        
        for (String fontName : emojiFont) {
            Font font = new Font(fontName, Font.PLAIN, size);
            if (font.canDisplay('😊')) {
                return font;
            }
        }
        
        // Fallback to default font
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }
    
    /**
     * Tạo text với emoji được format đúng
     */
    public static String formatTextWithEmoji(String text) {
        // Chuyển đổi shortcode thành emoji
        String formatted = convertShortcodeToEmoji(text);
        
        // Đảm bảo emoji được hiển thị đúng
        return formatted;
    }
    
    /**
     * Lấy emoji cho trạng thái người dùng
     */
    public static String getStatusEmoji(String status) {
        switch (status.toLowerCase()) {
            case "online":
                return "🟢";
            case "away":
                return "🟡";
            case "busy":
                return "🔴";
            case "offline":
            default:
                return "⚫";
        }
    }
    
    /**
     * Lấy emoji cho loại file
     */
    public static String getFileTypeEmoji(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "🖼️";
            case "mp3":
            case "wav":
            case "flac":
            case "aac":
                return "🎵";
            case "mp4":
            case "avi":
            case "mkv":
            case "mov":
                return "🎬";
            case "pdf":
                return "📄";
            case "doc":
            case "docx":
                return "📝";
            case "xls":
            case "xlsx":
                return "📊";
            case "zip":
            case "rar":
            case "7z":
                return "📦";
            default:
                return "📄";
        }
    }
}
