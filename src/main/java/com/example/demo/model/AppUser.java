package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_USER or ROLE_ADMIN

    @Column(name = "is_immutable")
    private Boolean isImmutable = false;

    // ✅ Constructors
    public AppUser() {}

    public AppUser(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isImmutable = false;
    }

    public AppUser(String username, String password, String role, boolean isImmutable) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isImmutable = isImmutable;
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsImmutable() {
        return isImmutable != null ? isImmutable : false;
    }

    public void setIsImmutable(Boolean isImmutable) {
        this.isImmutable = isImmutable != null ? isImmutable : false;
    }
}