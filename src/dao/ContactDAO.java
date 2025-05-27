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
        System.out.println("Adding contact: " + userId + " -> " + contactUserId);
        
        // Check if contact already exists
        if (isContactExists(userId, contactUserId)) {
            System.out.println("Contact already exists");
            return false;
        }
        
        String query = "INSERT INTO contacts (user_id, contact_user_id, status) VALUES (?, ?, 'pending')";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactUserId);
            
            int rowsInserted = statement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted);
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean acceptContact(int userId, int contactUserId) {
        System.out.println("Accepting contact: userId=" + userId + ", contactUserId=" + contactUserId);
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update the original request to accepted
            String updateQuery = "UPDATE contacts SET status = 'accepted' WHERE user_id = ? AND contact_user_id = ? AND status = 'pending'";
            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, contactUserId);
                updateStatement.setInt(2, userId);
                int updatedRows = updateStatement.executeUpdate();
                
                System.out.println("Updated rows: " + updatedRows);
                
                if (updatedRows == 0) {
                    System.out.println("No pending request found to update");
                    conn.rollback();
                    return false;
                }
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
                        int insertedRows = insertStatement.executeUpdate();
                        System.out.println("Inserted reverse contact rows: " + insertedRows);
                    }
                } else {
                    // Update existing reverse contact
                    String updateReverseQuery = "UPDATE contacts SET status = 'accepted' WHERE user_id = ? AND contact_user_id = ?";
                    try (PreparedStatement updateReverseStatement = conn.prepareStatement(updateReverseQuery)) {
                        updateReverseStatement.setInt(1, userId);
                        updateReverseStatement.setInt(2, contactUserId);
                        int updatedReverseRows = updateReverseStatement.executeUpdate();
                        System.out.println("Updated reverse contact rows: " + updatedReverseRows);
                    }
                }
            }
            
            conn.commit();
            System.out.println("Transaction committed successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error accepting contact: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
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
        System.out.println("Removing contact: " + userId + " <-> " + contactUserId);
        
        String query = "DELETE FROM contacts WHERE (user_id = ? AND contact_user_id = ?) OR (user_id = ? AND contact_user_id = ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, contactUserId);
            statement.setInt(3, contactUserId);
            statement.setInt(4, userId);
            
            int rowsDeleted = statement.executeUpdate();
            System.out.println("Rows deleted: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error removing contact: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Contact> getPendingContactRequests(int userId) {
        System.out.println("Getting pending requests for user ID: " + userId);
        
        List<Contact> requests = new ArrayList<>();
        String query = """
            SELECT c.*, u.username, u.display_name, u.avatar_url, u.status
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
                
                // User information (người gửi lời mời)
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setDisplayName(resultSet.getString("display_name"));
                user.setAvatarUrl(resultSet.getString("avatar_url"));
                user.setStatus(resultSet.getString("status"));
                
                contact.setContactUser(user);
                requests.add(contact);
                
                System.out.println("Found pending request from: " + user.getDisplayName() + " (ID: " + user.getId() + ")");
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending contact requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Total pending requests found: " + requests.size());
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
    
    // Debug method to check contact relationships
    public void debugContactRelationship(int userId1, int userId2) {
        String query = "SELECT * FROM contacts WHERE (user_id = ? AND contact_user_id = ?) OR (user_id = ? AND contact_user_id = ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId1);
            statement.setInt(2, userId2);
            statement.setInt(3, userId2);
            statement.setInt(4, userId1);
            
            ResultSet resultSet = statement.executeQuery();
            System.out.println("=== DEBUG CONTACT RELATIONSHIP ===");
            System.out.println("User1 ID: " + userId1 + ", User2 ID: " + userId2);
            
            boolean hasData = false;
            while (resultSet.next()) {
                hasData = true;
                System.out.println("Contact ID: " + resultSet.getInt("id"));
                System.out.println("User ID: " + resultSet.getInt("user_id"));
                System.out.println("Contact User ID: " + resultSet.getInt("contact_user_id"));
                System.out.println("Status: " + resultSet.getString("status"));
                System.out.println("Created At: " + resultSet.getTimestamp("created_at"));
                System.out.println("---");
            }
            
            if (!hasData) {
                System.out.println("No contact relationship found");
            }
            
            System.out.println("=== END DEBUG ===");
            
        } catch (SQLException e) {
            System.err.println("Error in debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
