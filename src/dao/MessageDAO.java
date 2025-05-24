package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.Message;

public class MessageDAO {
    private Connection connection;
    
    public MessageDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public boolean saveMessage(Message message) {
        String query = """
            INSERT INTO messages (sender_id, recipient_id, group_id, content, message_type, file_url, file_name, file_size) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, message.getSenderId());
            
            if (message.getRecipientId() != null) {
                statement.setInt(2, message.getRecipientId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            
            if (message.getGroupId() != null) {
                statement.setInt(3, message.getGroupId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            
            statement.setString(4, message.getContent());
            statement.setString(5, message.getMessageType());
            statement.setString(6, message.getFileUrl());
            statement.setString(7, message.getFileName());
            
            if (message.getFileSize() != null) {
                statement.setLong(8, message.getFileSize());
            } else {
                statement.setNull(8, java.sql.Types.BIGINT);
            }
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            return false;
        }
    }
    
    public List<Message> getPrivateMessageHistory(int user1Id, int user2Id) {
        List<Message> messages = new ArrayList<>();
        String query = """
            SELECT m.*, 
                   s.username as sender_username, s.display_name as sender_display_name,
                   r.username as recipient_username, r.display_name as recipient_display_name
            FROM messages m
            JOIN users s ON m.sender_id = s.id
            LEFT JOIN users r ON m.recipient_id = r.id
            WHERE ((m.sender_id = ? AND m.recipient_id = ?) OR (m.sender_id = ? AND m.recipient_id = ?))
            AND m.group_id IS NULL
            ORDER BY m.timestamp ASC
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user1Id);
            statement.setInt(2, user2Id);
            statement.setInt(3, user2Id);
            statement.setInt(4, user1Id);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setSenderId(resultSet.getInt("sender_id"));
                message.setRecipientId(resultSet.getInt("recipient_id"));
                message.setContent(resultSet.getString("content"));
                message.setMessageType(resultSet.getString("message_type"));
                message.setFileUrl(resultSet.getString("file_url"));
                message.setFileName(resultSet.getString("file_name"));
                message.setFileSize(resultSet.getLong("file_size"));
                message.setTimestamp(resultSet.getTimestamp("timestamp"));
                message.setRead(resultSet.getBoolean("is_read"));
                message.setEdited(resultSet.getBoolean("is_edited"));
                message.setEditedAt(resultSet.getTimestamp("edited_at"));
                
                // Set sender and recipient names
                message.setSenderUsername(resultSet.getString("sender_username"));
                message.setSenderDisplayName(resultSet.getString("sender_display_name"));
                message.setRecipientUsername(resultSet.getString("recipient_username"));
                message.setRecipientDisplayName(resultSet.getString("recipient_display_name"));
                
                messages.add(message);
            }
        } catch (SQLException e) {
            System.err.println("Error getting private message history: " + e.getMessage());
        }
        
        return messages;
    }
    
    public List<Message> getGroupMessageHistory(int groupId) {
        List<Message> messages = new ArrayList<>();
        String query = """
            SELECT m.*, 
                   s.username as sender_username, s.display_name as sender_display_name,
                   g.name as group_name
            FROM messages m
            JOIN users s ON m.sender_id = s.id
            JOIN chat_groups g ON m.group_id = g.id
            WHERE m.group_id = ?
            ORDER BY m.timestamp ASC
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setSenderId(resultSet.getInt("sender_id"));
                message.setGroupId(resultSet.getInt("group_id"));
                message.setContent(resultSet.getString("content"));
                message.setMessageType(resultSet.getString("message_type"));
                message.setFileUrl(resultSet.getString("file_url"));
                message.setFileName(resultSet.getString("file_name"));
                message.setFileSize(resultSet.getLong("file_size"));
                message.setTimestamp(resultSet.getTimestamp("timestamp"));
                message.setRead(resultSet.getBoolean("is_read"));
                message.setEdited(resultSet.getBoolean("is_edited"));
                message.setEditedAt(resultSet.getTimestamp("edited_at"));
                
                // Set sender and group names
                message.setSenderUsername(resultSet.getString("sender_username"));
                message.setSenderDisplayName(resultSet.getString("sender_display_name"));
                message.setGroupName(resultSet.getString("group_name"));
                
                messages.add(message);
            }
        } catch (SQLException e) {
            System.err.println("Error getting group message history: " + e.getMessage());
        }
        
        return messages;
    }
    
    public boolean markMessageAsRead(int messageId) {
        String query = "UPDATE messages SET is_read = TRUE WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, messageId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            return false;
        }
    }
    
    public boolean markConversationAsRead(int userId, int contactId) {
        String query = "UPDATE messages SET is_read = TRUE WHERE recipient_id = ? AND sender_id = ? AND is_read = FALSE";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error marking conversation as read: " + e.getMessage());
            return false;
        }
    }
    
    public int getUnreadMessageCount(int userId, int contactId) {
        String query = "SELECT COUNT(*) FROM messages WHERE recipient_id = ? AND sender_id = ? AND is_read = FALSE";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread message count: " + e.getMessage());
        }
        
        return 0;
    }
    
    public Message getLastMessage(int user1Id, int user2Id) {
        String query = """
            SELECT m.*, 
                   s.username as sender_username, s.display_name as sender_display_name
            FROM messages m
            JOIN users s ON m.sender_id = s.id
            WHERE ((m.sender_id = ? AND m.recipient_id = ?) OR (m.sender_id = ? AND m.recipient_id = ?))
            AND m.group_id IS NULL
            ORDER BY m.timestamp DESC
            LIMIT 1
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user1Id);
            statement.setInt(2, user2Id);
            statement.setInt(3, user2Id);
            statement.setInt(4, user1Id);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setSenderId(resultSet.getInt("sender_id"));
                message.setRecipientId(resultSet.getInt("recipient_id"));
                message.setContent(resultSet.getString("content"));
                message.setMessageType(resultSet.getString("message_type"));
                message.setTimestamp(resultSet.getTimestamp("timestamp"));
                message.setSenderUsername(resultSet.getString("sender_username"));
                message.setSenderDisplayName(resultSet.getString("sender_display_name"));
                
                return message;
            }
        } catch (SQLException e) {
            System.err.println("Error getting last message: " + e.getMessage());
        }
        
        return null;
    }
    
    public Message getLastGroupMessage(int groupId) {
        String query = """
            SELECT m.*, 
                   s.username as sender_username, s.display_name as sender_display_name
            FROM messages m
            JOIN users s ON m.sender_id = s.id
            WHERE m.group_id = ?
            ORDER BY m.timestamp DESC
            LIMIT 1
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setSenderId(resultSet.getInt("sender_id"));
                message.setGroupId(resultSet.getInt("group_id"));
                message.setContent(resultSet.getString("content"));
                message.setMessageType(resultSet.getString("message_type"));
                message.setTimestamp(resultSet.getTimestamp("timestamp"));
                message.setSenderUsername(resultSet.getString("sender_username"));
                message.setSenderDisplayName(resultSet.getString("sender_display_name"));
                
                return message;
            }
        } catch (SQLException e) {
            System.err.println("Error getting last group message: " + e.getMessage());
        }
        
        return null;
    }
}
