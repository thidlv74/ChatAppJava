package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 9999;
    private static HashMap<String, PrintWriter> clients = new HashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(50);
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                
                // Create a new client handler
                ClientHandler handler = new ClientHandler(clientSocket);
                
                // Execute the client handler in the thread pool
                pool.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Client handler class
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private Scanner reader;
        private PrintWriter writer;
        private String username;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                // Set up input and output streams
                reader = new Scanner(socket.getInputStream());
                writer = new PrintWriter(socket.getOutputStream(), true);
                
                // Get username
                username = reader.nextLine();
                System.out.println("User connected: " + username);
                
                // Add client to the map
                synchronized (clients) {
                    clients.put(username, writer);
                }
                
                // Process messages
                String message;
                while (reader.hasNextLine()) {
                    message = reader.nextLine();
                    
                    // Check if it's a private message
                    if (message.contains(":")) {
                        String[] parts = message.split(":", 2);
                        String recipient = parts[0];
                        String content = parts[1];
                        
                        // Send private message
                        sendPrivateMessage(recipient, username + ": " + content);
                    } else {
                        // Broadcast the message
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                // Remove client from the map
                if (username != null) {
                    synchronized (clients) {
                        clients.remove(username);
                    }
                    System.out.println("User disconnected: " + username);
                }
                
                // Close resources
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
        
        // Send a message to all clients
        private void broadcast(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients.values()) {
                    writer.println(message);
                }
            }
        }
        
        // Send a private message to a specific client
        private void sendPrivateMessage(String recipient, String message) {
            synchronized (clients) {
                PrintWriter recipientWriter = clients.get(recipient);
                if (recipientWriter != null) {
                    recipientWriter.println(message);
                    writer.println("To " + recipient + ": " + message);
                } else {
                    writer.println("User " + recipient + " is not online.");
                }
            }
        }
    }
}
