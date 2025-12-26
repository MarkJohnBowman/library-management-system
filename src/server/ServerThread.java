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
                    	handleRegistration();
                        break;
                    case "2":
                    	handleLogin();
                        break;
                    case "3":
                        sendMessage("Goodbye!");
                        running = false;
                        break;
                    default:
                        sendMessage("Invalid option. Please try again.");
                }
                    
                } else {
                   
                	// Post-login menu (role-based)
                    if (loggedInUser.isStudent()) {
                        running = handleStudentMenu();
                    } else if (loggedInUser.isLibrarian()) {
                        running = handleLibrarianMenu();
                    }
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
        
        // Handle student menu
        private boolean handleStudentMenu() throws IOException, ClassNotFoundException {
            sendMessage("\n=== STUDENT MENU ===");
            sendMessage("Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getId() + ")");
            sendMessage("3. Create Borrow Request");
            sendMessage("6. View My Records");
            sendMessage("7. Update Password");
            sendMessage("8. Logout");
            sendMessage("9. Exit");
            sendMessage("Choose an option:");
            
            String choice = (String) in.readObject();
            
            switch (choice.trim()) {
            case "3":
            	 handleCreateBorrowRequest();
                break;
            case "6":
                sendMessage("View records - coming soon!");
                break;
            case "7":
                sendMessage("Update password - coming soon!");
                break;
            case "8":
                loggedInUser = null;
                sendMessage("Logged out successfully!");
                break;
            case "9":
                sendMessage("Goodbye!");
                return false;
            default:
                sendMessage("Invalid option. Please try again.");
        }
        
        return true;
    }
        
    // handle librarian menu 
        private boolean handleLibrarianMenu() throws IOException, ClassNotFoundException {
            sendMessage("\n=== LIBRARIAN MENU ===");
            sendMessage("Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getId() + ")");
            sendMessage("3. Create New Book Entry");
            sendMessage("4. View All Records");
            sendMessage("5. Assign Borrow Request");
            sendMessage("6. View Records Assigned to Me");
            sendMessage("7. Update Password");
            sendMessage("8. Logout");
            sendMessage("9. Exit");
            sendMessage("Choose an option:");
            
            String choice = (String) in.readObject();
    
            switch (choice.trim()) {
            case "3":
                sendMessage("Create book entry - coming soon!");
                break;
            case "4":
                sendMessage("View all records - coming soon!");
                break;
            case "5":
                sendMessage("Assign request - coming soon!");
                break;
            case "6":
                sendMessage("View assigned records - coming soon!");
                break;
            case "7":
                sendMessage("Update password - coming soon!");
                break;
            case "8":
            	loggedInUser = null;
                sendMessage("Logged out successfully!");
                break;
            case "9":
                sendMessage("Goodbye!");
                return false;
            default:
                sendMessage("Invalid option. Please try again.");
        }
        
        return true;
    }
    
    // handle registration
    private void handleRegistration() {
        try {
            sendMessage("\n=== REGISTRATION ===");
            
            // Get name
            sendMessage("Enter your name:");
            String name = (String) in.readObject();
            
            // Get ID
            sendMessage("Enter your ID (Students: G00123456, Librarians: LIB001):");
            String userId = (String) in.readObject();
            
            // Check if ID already exists
            if (isIdTaken(userId)) {
                sendMessage("ERROR: ID already exists. Registration failed.");
                return;
            }
            
            // Get email
            sendMessage("Enter your email:");
            String email = (String) in.readObject();
    
            // Check if email already exists
            if (isEmailTaken(email)) {
                sendMessage("ERROR: Email already exists. Registration failed.");
                return;
            }
            
            // Get password
            sendMessage("Enter your password:");
            String password = (String) in.readObject();
            
            // Get department
            sendMessage("Enter your department name:");
            String departmentName = (String) in.readObject();
            
            // Get role
            sendMessage("Select role (1=Student, 2=Librarian):");
            String roleChoice = (String) in.readObject();
            
            // Convert to enum
            User.Role userRole;
            if (roleChoice.equals("1")) {
                userRole = User.Role.STUDENT;
            } else if (roleChoice.equals("2")) {
                userRole = User.Role.LIBRARIAN;
            } else {
                sendMessage("ERROR: Invalid role selection. Registration failed.");
                return;
            }
            
            // Create new user
            User newUser = new User(name, userId, email, password, departmentName, userRole);
            
            // Add to list
            synchronized (registeredUsers) {
                registeredUsers.add(newUser);
            }
            
            sendMessage("SUCCESS: Registration complete! You can now log in.");
            System.out.println("New user registered: " + newUser);
            
            // Save data
            Provider.saveData();
            
       } catch (IOException | ClassNotFoundException e) {
           System.err.println("Error during registration: " + e.getMessage());
       }
    }
    
    // Handle user login
    private void handleLogin() {
        try {
            sendMessage("\n=== LOGIN ===");
            
            // Get email
            sendMessage("Enter your email:");
            String email = (String) in.readObject();
            
            // Get password
            sendMessage("Enter your password:");
            String password = (String) in.readObject();
            
            // Find user with matching credentials
            User user = findUser(email, password);
    
            if (user != null) {
                loggedInUser = user;
                sendMessage("SUCCESS: Login successful! Welcome, " + user.getName());
                System.out.println("User logged in: " + user.getEmail());
            } else {
                sendMessage("ERROR: Invalid email or password. Login failed.");
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }
    
    // Handle password update
    private void handleUpdatePassword() {
        try {
            sendMessage("\n=== UPDATE PASSWORD ===");
            
            // Verify current password
            sendMessage("Enter your current password:");
            String currentPassword = (String) in.readObject();
            
            if (!loggedInUser.getPassword().equals(currentPassword)) {
                sendMessage("ERROR: Current password is incorrect. Password not changed.");
                return;
            }
            
         // Get new password
            sendMessage("Enter your new password:");
            String newPassword = (String) in.readObject();
            
            // Confirm new password
            sendMessage("Confirm your new password:");
            String confirmPassword = (String) in.readObject();
            
            if (!newPassword.equals(confirmPassword)) {
                sendMessage("ERROR: Passwords do not match. Password not changed.");
                return;
            }
            
         // Update password
            loggedInUser.setPassword(newPassword);
            sendMessage("SUCCESS: Password updated successfully!");
            System.out.println("Password updated for user: " + loggedInUser.getEmail());
            
            // Save data
            Provider.saveData();
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during password update: " + e.getMessage());
        }
    }
    
    // HandleCreateBorrowRequest
    private void handleCreateBorrowRequest() {
        try {
            sendMessage("\n=== CREATE BORROW REQUEST ===");
            
            // Generate unique record ID
            String recordId = "REQ" + System.currentTimeMillis();
            
            // Create borrow request
            LibraryRecord request = new LibraryRecord(recordId, loggedInUser.getId());
            
            // Add to records list
            synchronized (libraryRecords) {
                libraryRecords.add(request);
            }
            
            sendMessage("SUCCESS: Borrow request created!");
            sendMessage("Request ID: " + recordId);
            sendMessage("Status: REQUESTED");
            System.out.println("Borrow request created: " + recordId + " by " + loggedInUser.getId());
            
            // Save data
            Provider.saveData();
            
        } catch (Exception e) {
            System.err.println("Error creating borrow request: " + e.getMessage());
        }
    }
    
    // check if ID is already taken 
    private boolean isIdTaken(String userId) {
        synchronized (registeredUsers) {
            for (User user : registeredUsers) {
                if (user.getId().equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // check if email is already taken 
    private boolean isEmailTaken(String email) {
        synchronized (registeredUsers) {
            for (User user : registeredUsers) {
                if (user.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Find user by email and password
    private User findUser(String email, String password) {
        synchronized (registeredUsers) {
            for (User user : registeredUsers) {
                if (user.getEmail().equalsIgnoreCase(email) && 
                    user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
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