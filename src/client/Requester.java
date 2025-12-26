package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Requester - Client application for Library Management System
 * Connects to Provider server on localhost:2004
 * 
 * @author Mark Bowman
 * @student G00389705
 */
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
    
    // clean up resources
    public void start() {
    	try {
            // Connect to server
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            
            // Set up streams (IMPORTANT: Output first, then input)
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // Set up scanner for user input
            scanner = new Scanner(System.in);
            
            System.out.println("Connection established. Ready to communicate.\n");
            
            // TODO: Handle communication loop
            
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
        
    }
    
    private void cleanup() {
        try {
            if (scanner != null) scanner.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("\nConnection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}