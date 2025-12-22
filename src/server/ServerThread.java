package server;

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
    private List<User> registeredUsers;
    private List<LibraryRecord> libraryRecords;
    
    public ServerThread(Socket socket, List<User> users, List<LibraryRecord> records) {
        this.socket = socket;
        this.registeredUsers = users;
        this.libraryRecords = records;
    }
    
    @Override
    public void run() {
        System.out.println("ServerThread started for client: " + socket.getInetAddress().getHostAddress());
        
        
    }
}