package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Contact;
import model.User;

public class ContactDAO {
    private Connection connection;
    
    public ContactDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public List<Contact> getUserContacts(int userId) {
        List<Contact> contacts = new ArrayList<>();
        String query = """
            SELECT c.*, u.username, u.display_name, u.avatar_url, u.status, u.last_seen
            FROM contacts c
            JOIN users u ON c.contact_user_id = u.id
            WHERE c.user_id = ? AND c.status = 'accepted'
            ORDER BY u.display_name
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Contact contact = new Contact();
                contact.setId(resultSet.getInt("id"));
                contact.setUserId(resultSet.getInt("user_id"));
                contact.setContactUserId(resultSet.getInt("contact_user_id"));
                contact.setStatus(resultSet.getString("status"));
                contact.setCreatedAt(resultSet.getTimestamp("created_at"));
                
                // User information
                User user = new User();
                user.setId(resultSet.getInt("contact_user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setDisplayName(resultSet.getString("display_name"));
                user.setAvatarUrl(resultSet.getString("avatar_url"));
                user.setStatus(resultSet.getString("status"));
                user.setLastSeen(resultSet.getTimestamp("last_seen"));
                
                contact.setContactUser(user);
                contacts.add(contact);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user contacts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return contacts;
    }
    
    public boolean addContact(int userId, int contactUserId) {
        // Check if contact already exists
        if (isContactExists(userId, contactUserId)) {
            return false;
        }
        
        String query = "INSERT INTO contacts (user_id, contact_user_id, status) VALUES (?, ?, 'pending')";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactUserId);
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean acceptContact(int userId, int contactUserId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update the original request to accepted
            String updateQuery = "UPDATE contacts SET status = 'accepted' WHERE user_id = ? AND contact_user_id = ?";
            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, contactUserId);
                updateStatement.setInt(2, userId);
                updateStatement.executeUpdate();
            }
            
            // Add reverse contact if not exists
            String checkQuery = "SELECT COUNT(*) FROM contacts WHERE user_id = ? AND contact_user_id = ?";
            try (PreparedStatement checkStatement = conn.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, userId);
                checkStatement.setInt(2, contactUserId);
                ResultSet rs = checkStatement.executeQuery();
                rs.next();
                
                if (rs.getInt(1) == 0) {
                    // Insert reverse contact
                    String insertQuery = "INSERT INTO contacts (user_id, contact_user_id, status) VALUES (?, ?, 'accepted')";
                    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, contactUserId);
                        insertStatement.executeUpdate();
                    }
                } else {
                    // Update existing reverse contact
                    String updateReverseQuery = "UPDATE contacts SET status = 'accepted' WHERE user_id = ? AND contact_user_id = ?";
                    try (PreparedStatement updateReverseStatement = conn.prepareStatement(updateReverseQuery)) {
                        updateReverseStatement.setInt(1, userId);
                        updateReverseStatement.setInt(2, contactUserId);
                        updateReverseStatement.executeUpdate();
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error accepting contact: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean removeContact(int userId, int contactUserId) {
        String query = "DELETE FROM contacts WHERE (user_id = ? AND contact_user_id = ?) OR (user_id = ? AND contact_user_id = ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactUserId);
            statement.setInt(3, contactUserId);
            statement.setInt(4, userId);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error removing contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Contact> getPendingContactRequests(int userId) {
        List<Contact> requests = new ArrayList<>();
        String query = """
            SELECT c.*, u.username, u.display_name, u.avatar_url
            FROM contacts c
            JOIN users u ON c.user_id = u.id
            WHERE c.contact_user_id = ? AND c.status = 'pending'
            ORDER BY c.created_at DESC
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Contact contact = new Contact();
                contact.setId(resultSet.getInt("id"));
                contact.setUserId(resultSet.getInt("user_id"));
                contact.setContactUserId(resultSet.getInt("contact_user_id"));
                contact.setStatus(resultSet.getString("status"));
                contact.setCreatedAt(resultSet.getTimestamp("created_at"));
                
                // User information
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setDisplayName(resultSet.getString("display_name"));
                user.setAvatarUrl(resultSet.getString("avatar_url"));
                
                contact.setContactUser(user);
                requests.add(contact);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending contact requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }

    public boolean isContactExists(int userId, int contactUserId) {
        String query = "SELECT COUNT(*) FROM contacts WHERE (user_id = ? AND contact_user_id = ?) OR (user_id = ? AND contact_user_id = ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactUserId);
            statement.setInt(3, contactUserId);
            statement.setInt(4, userId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if contact exists: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
