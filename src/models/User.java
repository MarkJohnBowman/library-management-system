package models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role { STUDENT, LIBRARIAN }

    private String name, id, email, password, departmentName;
    private Role role;

    public User(String name, String id, String email, String password, String departmentName, Role role) {
        this.name = name; this.id = id; this.email = email;
        this.password = password; this.departmentName = departmentName; this.role = role;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDepartmentName() { return departmentName; }
    public Role getRole() { return role; }
    public void setPassword(String password) { this.password = password; }
    public boolean isStudent() { return role == Role.STUDENT; }
    public boolean isLibrarian() { return role == Role.LIBRARIAN; }
}