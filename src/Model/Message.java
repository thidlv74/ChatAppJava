package model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int senderId;
    private Integer recipientId; // Nullable for group messages
    private Integer groupId; // Nullable for private messages
    private String content;
    private String messageType; // text, image, file, audio, video
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Timestamp timestamp;
    private boolean isRead;
    private boolean isEdited;
    private Timestamp editedAt;
    
    // Additional fields for display purposes
    private String senderUsername;
    private String senderDisplayName;
    private String recipientUsername;
    private String recipientDisplayName;
    private String groupName;
    
    public Message() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.messageType = "text";
        this.isRead = false;
        this.isEdited = false;
    }
    
    public Message(int senderId, Integer recipientId, String content) {
        this();
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
    }
    
    public Message(int senderId, Integer groupId, String content, boolean isGroupMessage) {
        this();
        this.senderId = senderId;
        if (isGroupMessage) {
            this.groupId = groupId;
        } else {
            this.recipientId = groupId;
        }
        this.content = content;
    }
    
    // Constructor for backward compatibility with old code
    public Message(String senderUsername, String recipientUsername, String content) {
        this();
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.content = content;
        // Note: senderId and recipientId will need to be set separately
    }
    
    // Constructor for group messages with username
    public Message(String senderUsername, String recipientUsername, String content, boolean isGroupMessage) {
        this();
        this.senderUsername = senderUsername;
        this.content = content;
        if (isGroupMessage) {
            this.groupName = recipientUsername; // In this case, recipientUsername is actually group name
        } else {
            this.recipientUsername = recipientUsername;
        }
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public Integer getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }
    
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public void setEdited(boolean edited) {
        isEdited = edited;
    }
    
    public Timestamp getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(Timestamp editedAt) {
        this.editedAt = editedAt;
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getSenderDisplayName() {
        return senderDisplayName;
    }
    
    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }
    
    public String getRecipientUsername() {
        return recipientUsername;
    }
    
    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
    
    public String getRecipientDisplayName() {
        return recipientDisplayName;
    }
    
    public void setRecipientDisplayName(String recipientDisplayName) {
        this.recipientDisplayName = recipientDisplayName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public boolean isGroupMessage() {
        return groupId != null;
    }
    
    // Convenience methods for backward compatibility
    public String getSender() {
        return senderUsername != null ? senderUsername : String.valueOf(senderId);
    }
    
    public void setSender(String sender) {
        this.senderUsername = sender;
    }
    
    public String getRecipient() {
        if (isGroupMessage()) {
            return groupName;
        }
        return recipientUsername != null ? recipientUsername : String.valueOf(recipientId);
    }
    
    public void setRecipient(String recipient) {
        if (isGroupMessage()) {
            this.groupName = recipient;
        } else {
            this.recipientUsername = recipient;
        }
    }
    
    @Override
    public String toString() {
        return "Message{" + 
               "id=" + id + 
               ", senderId=" + senderId + 
               ", recipientId=" + recipientId + 
               ", groupId=" + groupId + 
               ", content='" + content + '\'' + 
               ", messageType='" + messageType + '\'' + 
               ", timestamp=" + timestamp + 
               ", isRead=" + isRead + 
               '}';
    }
}
