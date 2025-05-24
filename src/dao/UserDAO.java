package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO {
    private Connection connection;
    
    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validateUserByEmail(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error validating user by email: " + e.getMessage());
            return false;
        }
    }
    
    public boolean usernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }
    
    public boolean emailExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registerUser(String username, String email, String password) {
        String query = "INSERT INTO users (username, email, password, display_name) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, username); // Use username as default display name
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validateUserEmail(String username, String email) {
        String query = "SELECT * FROM users WHERE username = ? AND email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error validating user email: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updatePassword(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newPassword);
            statement.setString(2, email);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password by email: " + e.getMessage());
            return false;
        }
    }
    
    public String getEmail(String username) {
        String query = "SELECT email FROM users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("email");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting email: " + e.getMessage());
            return null;
        }
    }
    
    public User getUser(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return createUserFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            return null;
        }
    }
    
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return createUserFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            return null;
        }
    }
    
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return createUserFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }
    
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        
        // Handle null display_name
        String displayName = resultSet.getString("display_name");
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = resultSet.getString("username");
        }
        user.setDisplayName(displayName);
        
        user.setAvatarUrl(resultSet.getString("avatar_url"));
        user.setStatus(resultSet.getString("status"));
        user.setLastSeen(resultSet.getTimestamp("last_seen"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return user;
    }
    
    public boolean updateUserStatus(int userId, String status) {
        String query = "UPDATE users SET status = ?, last_seen = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, userId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }
    
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE username LIKE ? OR display_name LIKE ? OR email LIKE ? LIMIT 20";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        
        return users;
    }
    
    public boolean updateProfile(int userId, String displayName, String email) {
        String query = "UPDATE users SET display_name = ?, email = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, displayName);
            statement.setString(2, email);
            statement.setInt(3, userId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
}
