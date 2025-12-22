package server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.LibraryRecord;
import models.User;

/**
 * Provider - Main server class
 * Listens for client connections on port 2004
 * 
 * @author Mark Bowman
 * @student G00389705
 */
public class Provider {
    
    private static final int PORT = 2004;
    private static final String USERS_FILE = "users.dat";
    private static final String RECORDS_FILE = "records.dat";
    
    // Shared data structures - thread-safe
    private static List<User> users = Collections.synchronizedList(new ArrayList<>());
    private static List<LibraryRecord> records = Collections.synchronizedList(new ArrayList<>());
    
    public static void main(String[] args) {
        Provider server = new Provider();
        server.start();
    }
    
    public void start() {
    	  // Load existing data
        loadData();
        
        // Add shutdown hook to save data when server stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nServer shutting down... Saving data");
            saveData();
        }));
        
    	System.out.println("===========================================");
        System.out.println(" Library Management Server");
        System.out.println(" Port: " + PORT);
        System.out.println("===========================================");
        
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for clients...");
            
            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                // Create new thread for this client
                ServerThread clientThread = new ServerThread(clientSocket, users, records);
                clientThread.start();
            }
            
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Load users and records from files
    private void loadData() {
        System.out.println("\n[LOADING DATA]");
        
        // Load users
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            @SuppressWarnings("unchecked")
            List<User> loadedUsers = (List<User>) ois.readObject();
            users.addAll(loadedUsers);
            System.out.println("Loaded " + users.size() + " users");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing users file. Starting fresh.");
        }
        
        // Load records
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RECORDS_FILE))) {
            @SuppressWarnings("unchecked")
            List<LibraryRecord> loadedRecords = (List<LibraryRecord>) ois.readObject();
            records.addAll(loadedRecords);
            System.out.println("Loaded " + records.size() + " records");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing records file. Starting fresh.");
        }
        
        System.out.println();
    }
    
    // Save users and records to files
    public static void saveData() {
        System.out.println("\n[SAVING DATA]");
        
        // Save users
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(new ArrayList<>(users));
            System.out.println("Saved " + users.size() + " users");
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
        
     // Save records
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RECORDS_FILE))) {
            oos.writeObject(new ArrayList<>(records));
            System.out.println("Saved " + records.size() + " records");
        } catch (IOException e) {
            System.err.println("Error saving records: " + e.getMessage());
        }
    }
}