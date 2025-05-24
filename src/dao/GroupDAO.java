package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Group;
import model.GroupMember;
import model.User;

public class GroupDAO {
    private Connection connection;
    
    public GroupDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public List<Group> getUserGroups(int userId) {
        List<Group> groups = new ArrayList<>();
        String query = """
            SELECT g.*, u.display_name as creator_name
            FROM chat_groups g
            JOIN group_members gm ON g.id = gm.group_id
            JOIN users u ON g.created_by = u.id
            WHERE gm.user_id = ?
            ORDER BY g.name
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Group group = new Group();
                group.setId(resultSet.getInt("id"));
                group.setName(resultSet.getString("name"));
                group.setDescription(resultSet.getString("description"));
                group.setAvatarUrl(resultSet.getString("avatar_url"));
                group.setCreatedBy(resultSet.getInt("created_by"));
                group.setCreatedAt(resultSet.getTimestamp("created_at"));
                group.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                group.setCreatorName(resultSet.getString("creator_name"));
                
                groups.add(group);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user groups: " + e.getMessage());
        }
        
        return groups;
    }
    
    public Group createGroup(String name, String description, int createdBy) {
        String query = "INSERT INTO chat_groups (name, description, created_by) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, createdBy);
            
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int groupId = generatedKeys.getInt(1);
                    
                    // Add creator as admin
                    addGroupMember(groupId, createdBy, "admin");
                    
                    // Return the created group
                    return getGroupById(groupId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
        }
        
        return null;
    }
    
    public Group getGroupById(int groupId) {
        String query = """
            SELECT g.*, u.display_name as creator_name
            FROM chat_groups g
            JOIN users u ON g.created_by = u.id
            WHERE g.id = ?
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Group group = new Group();
                group.setId(resultSet.getInt("id"));
                group.setName(resultSet.getString("name"));
                group.setDescription(resultSet.getString("description"));
                group.setAvatarUrl(resultSet.getString("avatar_url"));
                group.setCreatedBy(resultSet.getInt("created_by"));
                group.setCreatedAt(resultSet.getTimestamp("created_at"));
                group.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                group.setCreatorName(resultSet.getString("creator_name"));
                
                return group;
            }
        } catch (SQLException e) {
            System.err.println("Error getting group by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addGroupMember(int groupId, int userId, String role) {
        String query = "INSERT INTO group_members (group_id, user_id, role) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            statement.setString(3, role);
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error adding group member: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeGroupMember(int groupId, int userId) {
        String query = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error removing group member: " + e.getMessage());
            return false;
        }
    }
    
    public List<GroupMember> getGroupMembers(int groupId) {
        List<GroupMember> members = new ArrayList<>();
        String query = """
            SELECT gm.*, u.username, u.display_name, u.avatar_url, u.status
            FROM group_members gm
            JOIN users u ON gm.user_id = u.id
            WHERE gm.group_id = ?
            ORDER BY gm.role DESC, u.display_name
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                GroupMember member = new GroupMember();
                member.setId(resultSet.getInt("id"));
                member.setGroupId(resultSet.getInt("group_id"));
                member.setUserId(resultSet.getInt("user_id"));
                member.setRole(resultSet.getString("role"));
                member.setJoinedAt(resultSet.getTimestamp("joined_at"));
                
                // User information
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setDisplayName(resultSet.getString("display_name"));
                user.setAvatarUrl(resultSet.getString("avatar_url"));
                user.setStatus(resultSet.getString("status"));
                
                member.setUser(user);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting group members: " + e.getMessage());
        }
        
        return members;
    }
    
    public boolean updateGroup(int groupId, String name, String description) {
        String query = "UPDATE chat_groups SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, groupId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating group: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteGroup(int groupId) {
        String query = "DELETE FROM chat_groups WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting group: " + e.getMessage());
            return false;
        }
    }
}
