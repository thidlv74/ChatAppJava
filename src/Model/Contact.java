package model;

import java.sql.Timestamp;

public class Contact {
    private int id;
    private int userId;
    private int contactUserId;
    private String status; // pending, accepted, blocked
    private Timestamp createdAt;
    private User contactUser; // The actual user object
    
    public Contact() {
    }
    
    public Contact(int userId, int contactUserId, String status) {
        this.userId = userId;
        this.contactUserId = contactUserId;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getContactUserId() {
        return contactUserId;
    }
    
    public void setContactUserId(int contactUserId) {
        this.contactUserId = contactUserId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getContactUser() {
        return contactUser;
    }
    
    public void setContactUser(User contactUser) {
        this.contactUser = contactUser;
    }
    
    @Override
    public String toString() {
        return "Contact{" + 
               "id=" + id + 
               ", userId=" + userId + 
               ", contactUserId=" + contactUserId + 
               ", status='" + status + '\'' + 
               ", createdAt=" + createdAt + 
               '}';
    }
}
