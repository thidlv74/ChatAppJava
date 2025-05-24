package model;

import java.sql.Timestamp;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private String description;
    private String avatarUrl;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String creatorName;
    private List<GroupMember> members;
    
    public Group() {
    }
    
    public Group(String name, String description, int createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public List<GroupMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }
    
    @Override
    public String toString() {
        return "Group{" + 
               "id=" + id + 
               ", name='" + name + '\'' + 
               ", description='" + description + '\'' + 
               ", createdBy=" + createdBy + 
               ", createdAt=" + createdAt + 
               '}';
    }
}

