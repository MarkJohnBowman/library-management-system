package server;

import java.io.IOException;
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
    
    // Shared data structures - thread-safe
    private static List<User> users = Collections.synchronizedList(new ArrayList<>());
    private static List<LibraryRecord> records = Collections.synchronizedList(new ArrayList<>());
    
    public static void main(String[] args) {
        Provider server = new Provider();
        server.start();
    }
    
    public void start() {
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
}