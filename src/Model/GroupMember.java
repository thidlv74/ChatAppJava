package model;

import java.sql.Timestamp;

public class GroupMember {
    private int id;
    private int groupId;
    private int userId;
    private String role; // admin, member
    private Timestamp joinedAt;
    private User user;
    
    public GroupMember() {
    }
    
    public GroupMember(int groupId, int userId, String role) {
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Timestamp getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "GroupMember{" + 
               "id=" + id + 
               ", groupId=" + groupId + 
               ", userId=" + userId + 
               ", role='" + role + '\'' + 
               ", joinedAt=" + joinedAt + 
               '}';
    }
}

