package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class FileUtils {
    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    
    // Supported file types
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};
    public static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "mkv", "mov", "wmv", "flv"};
    public static final String[] AUDIO_EXTENSIONS = {"mp3", "wav", "flac", "aac", "ogg"};
    public static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
    
    static {
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Error creating upload directory: " + e.getMessage());
        }
    }
    
    public static File selectImageFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Hình ảnh (*.jpg, *.jpeg, *.png, *.gif, *.bmp)", IMAGE_EXTENSIONS));
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    public static File selectVideoFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn video");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Video (*.mp4, *.avi, *.mkv, *.mov, *.wmv)", VIDEO_EXTENSIONS));
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    public static File selectAnyFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file");
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    public static String saveFile(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IOException("File không tồn tại");
        }
        
        if (sourceFile.length() > MAX_FILE_SIZE) {
            throw new IOException("File quá lớn. Kích thước tối đa là 50MB");
        }
        
        // Generate unique filename
        String originalName = sourceFile.getName();
        String extension = getFileExtension(originalName);
        String uniqueName = UUID.randomUUID().toString() + "." + extension;
        
        Path targetPath = Paths.get(UPLOAD_DIR + uniqueName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return targetPath.toString();
    }
    
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    public static String getFileType(String fileName) {
        String extension = getFileExtension(fileName);
        
        for (String ext : IMAGE_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "image";
            }
        }
        
        for (String ext : VIDEO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "video";
            }
        }
        
        for (String ext : AUDIO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "audio";
            }
        }
        
        return "file";
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    public static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        for (String ext : IMAGE_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isVideoFile(String fileName) {
        String extension = getFileExtension(fileName);
        for (String ext : VIDEO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAudioFile(String fileName) {
        String extension = getFileExtension(fileName);
        for (String ext : AUDIO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static ImageIcon createImageThumbnail(String imagePath, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error creating thumbnail: " + e.getMessage());
            return null;
        }
    }
    
    public static void openFile(String filePath) {
        try {
            Desktop desktop = Desktop.getDesktop();
            File file = new File(filePath);
            if (file.exists()) {
                desktop.open(file);
            }
        } catch (Exception e) {
            System.err.println("Error opening file: " + e.getMessage());
        }
    }
}
