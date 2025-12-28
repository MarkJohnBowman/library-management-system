package models;

import java.io.Serializable;

// User class for students and librarians in the system
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Role enum - either student or librarian
    public enum Role {
        STUDENT,
        LIBRARIAN
    }

    private String name;
    private String id;  // Student format: G00389705, Librarian format: LIB001
    private String email;
    private String password;
    private String departmentName;
    private Role role;

    // Constructor
    public User(String name, String id, String email, String password, String departmentName, Role role) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.departmentName = departmentName;
        this.role = role;
    }

    // Getters
    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDepartmentName() { return departmentName; }
    public Role getRole() { return role; }

    // Setter - only password can be changed after creation
    public void setPassword(String password) { 
        this.password = password; 
    }

    // Check user type
    public boolean isStudent() {
        return role == Role.STUDENT;
    }

    public boolean isLibrarian() {
        return role == Role.LIBRARIAN;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', id='" + id + "', email='" + email + 
               "', department='" + departmentName + "', role=" + role + '}';
    }
}