package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// Client application that connects to the library management server
public class Requester {
    
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2004;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println(" Library Management System - Client");
        System.out.println(" Connecting to server...");
        System.out.println("===========================================");
        
        Requester client = new Requester();
        client.start();
    }
    
    public void start() {
        try {
            // Connect to server
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            
            // Set up streams (output first, then input)
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // Set up scanner for user input
            scanner = new Scanner(System.in);
            
            System.out.println("Connection established. Ready to communicate.\n");
            
            // Main communication loop
            boolean running = true;
            while (running) {
                try {
                    // Receive message from server
                    String serverMessage = (String) in.readObject();
                    System.out.println(serverMessage);
                    
                    // Check if server is asking for input (ends with ":")
                    if (serverMessage.endsWith(":")) {
                        // Get user input and send to server
                        String userInput = scanner.nextLine();
                        out.writeObject(userInput);
                        out.flush();
                    }
                    
                    // Check for goodbye message
                    if (serverMessage.contains("Goodbye")) {
                        running = false;
                    }
                    
                } catch (EOFException e) {
                    System.out.println("Server closed connection.");
                    running = false;
                } catch (ClassNotFoundException e) {
                    System.err.println("Error: Received unknown data format from server");
                    running = false;
                }
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Error: Cannot find server at " + SERVER_ADDRESS);
            System.err.println("Make sure the server is running.");
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            System.err.println("Make sure the server is running on port " + SERVER_PORT);
        } finally {
            cleanup();
        }
    }
    
    // Clean up resources when done
    private void cleanup() {
        try {
            if (scanner != null) scanner.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("\nConnection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}