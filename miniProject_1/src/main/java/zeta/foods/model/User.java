package zeta.foods.model;

import java.time.ZonedDateTime;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password; // Stored as hashed
    private String role; // "admin", "waiter", or "customer"
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLogin;

    // Constructors
    public User() {}
    
    public User(Long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isAdmin() {
        return "admin".equals(role);
    }
    
    public boolean isWaiter() {
        return "waiter".equals(role);
    }
    
    public boolean isCustomer() {
        return "customer".equals(role);
    }
}
