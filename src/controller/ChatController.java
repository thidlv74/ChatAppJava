package controller;

import model.Message;
import dao.UserDAO;
import dao.MessageDAO;
import model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatController {
    private String username;
    private User currentUser;
    private String currentRecipient;
    private Socket socket;
    private PrintWriter writer;
    private Scanner reader;
    private boolean connected = false;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    
    public ChatController(String username) {
        this.username = username;
        this.userDAO = new UserDAO();
        this.messageDAO = new MessageDAO();
        this.currentUser = userDAO.getUser(username);
    }
    
    public void connect() {
        try {
            // In a real application, you would connect to a server
            // For this example, we'll simulate the connection
            /*
            socket = new Socket("localhost", 9999);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new Scanner(socket.getInputStream());
            
            // Send username to server
            writer.println(username);
            
            // Start a thread to read incoming messages
            new Thread(new IncomingReader()).start();
            */
            
            connected = true;
            System.out.println("Chat controller connected for user: " + username);
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
    
    public void disconnect() {
        if (connected && socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
        connected = false;
        System.out.println("Chat controller disconnected for user: " + username);
    }
    
    public boolean sendMessage(Message message) {
        if (!connected) {
            System.out.println("Not connected to server, but message saved to database");
            return true; // Message is already saved to database in MainChatWindow
        }
        
        // In a real application, you would send the message to the server
        // For this example, we'll simulate sending
        /*
        if (writer != null) {
            String messageText = message.getContent();
            if (message.isGroupMessage()) {
                writer.println("GROUP:" + message.getGroupId() + ":" + messageText);
            } else {
                writer.println("PRIVATE:" + message.getRecipientId() + ":" + messageText);
            }
            return true;
        }
        */
        
        // For demonstration, we'll just return true
        System.out.println("Simulated sending message: " + message.getContent());
        return true;
    }
    
    public void setCurrentRecipient(String recipient) {
        this.currentRecipient = recipient;
    }
    
    public String getCurrentRecipient() {
        return currentRecipient;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    // Thread for reading incoming messages
    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            try {
                String messageText;
                while (reader != null && (messageText = reader.nextLine()) != null) {
                    // Parse the message (format: TYPE:sender_id:content or TYPE:group_id:sender_id:content)
                    String[] parts = messageText.split(":", 3);
                    if (parts.length >= 3) {
                        String type = parts[0];
                        
                        if ("PRIVATE".equals(type)) {
                            // Private message: PRIVATE:sender_id:content
                            int senderId = Integer.parseInt(parts[1]);
                            String content = parts[2];
                            
                            // Get sender info
                            User sender = userDAO.getUserById(senderId);
                            if (sender != null) {
                                // Create message object
                                Message receivedMessage = new Message();
                                receivedMessage.setSenderId(senderId);
                                receivedMessage.setRecipientId(currentUser.getId());
                                receivedMessage.setContent(content);
                                receivedMessage.setSenderUsername(sender.getUsername());
                                receivedMessage.setSenderDisplayName(sender.getDisplayName());
                                
                                // Handle received message (would call a callback in a real app)
                                System.out.println("Received private message: " + receivedMessage);
                                handleReceivedMessage(receivedMessage);
                            }
                            
                        } else if ("GROUP".equals(type) && parts.length >= 4) {
                            // Group message: GROUP:group_id:sender_id:content
                            int groupId = Integer.parseInt(parts[1]);
                            int senderId = Integer.parseInt(parts[2]);
                            String content = parts[3];
                            
                            // Get sender info
                            User sender = userDAO.getUserById(senderId);
                            if (sender != null) {
                                // Create message object
                                Message receivedMessage = new Message();
                                receivedMessage.setSenderId(senderId);
                                receivedMessage.setGroupId(groupId);
                                receivedMessage.setContent(content);
                                receivedMessage.setSenderUsername(sender.getUsername());
                                receivedMessage.setSenderDisplayName(sender.getDisplayName());
                                
                                // Handle received message (would call a callback in a real app)
                                System.out.println("Received group message: " + receivedMessage);
                                handleReceivedMessage(receivedMessage);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }
    }
    
    private void handleReceivedMessage(Message message) {
        // Save received message to database
        messageDAO.saveMessage(message);
        
        // In a real application, you would notify the UI to update
        // For now, we'll just log it
        System.out.println("Message saved to database: " + message.getContent());
    }
    
    // Method to simulate receiving a message (for testing)
    public void simulateReceivedMessage(String senderUsername, String content, boolean isGroupMessage) {
        User sender = userDAO.getUser(senderUsername);
        if (sender != null) {
            Message receivedMessage = new Message();
            receivedMessage.setSenderId(sender.getId());
            receivedMessage.setContent(content);
            receivedMessage.setSenderUsername(sender.getUsername());
            receivedMessage.setSenderDisplayName(sender.getDisplayName());
            
            if (isGroupMessage) {
                // For group messages, you'd need to specify which group
                // This is just for demonstration
                receivedMessage.setGroupId(1); // Assuming group ID 1
            } else {
                receivedMessage.setRecipientId(currentUser.getId());
            }
            
            handleReceivedMessage(receivedMessage);
        }
    }
}
