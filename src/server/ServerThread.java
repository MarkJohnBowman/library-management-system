package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import models.LibraryRecord;
import models.User;

/**
 * ServerThread - Handles individual client connections
 * Each client gets its own thread
 * 
 * @author Mark Bowman
 * @student G00389705
 */
public class ServerThread extends Thread {
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private List<User> registeredUsers;
    private List<LibraryRecord> libraryRecords;
    private User loggedInUser = null;
    
    public ServerThread(Socket socket, List<User> users, List<LibraryRecord> records) {
        this.socket = socket;
        this.registeredUsers = users;
        this.libraryRecords = records;
    }
    
    @Override
    public void run() {
        System.out.println("ServerThread started for client: " + socket.getInetAddress().getHostAddress());
        
        try {
            // Set up streams
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // Send welcome message
            sendMessage("Welcome to the Library Management System!");
        
         // Main conversation loop
            boolean running = true;
            while (running) {
                
                if (loggedInUser == null) {
                    // Pre-login menu
                    sendMessage("\n=== MENU ===");
                    sendMessage("1. Register");
                    sendMessage("2. Login");
                    sendMessage("3. Exit");
                    sendMessage("Choose an option:");
                    
                    String choice = (String) in.readObject();
                    
                    switch (choice.trim()) {
                    case "1":
                        sendMessage("Registration coming soon!");
                        break;
                    case "2":
                        sendMessage("Login coming soon!");
                        break;
                    case "3":
                        sendMessage("Goodbye!");
                        running = false;
                        break;
                    default:
                        sendMessage("Invalid option. Please try again.");
                }
                    
                } else {
                   
                    sendMessage("You are logged in!");
                    running = false;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }  catch (ClassNotFoundException e) {
            System.err.println("Data received in unknown format");
        }finally {
            cleanup();
        }
    }
    
    // Send a message to the client
    private void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("server> " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Clean up resources
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}