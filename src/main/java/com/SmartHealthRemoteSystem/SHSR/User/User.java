package com.SmartHealthRemoteSystem.SHSR.User;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class User {

    @Id
    private String userId;

    private String name;

    private String contact;

    private String password;

    private String role;

    @Indexed(unique = true)
    private String email;

    public User() {
    }

    public User(String userId, String name, String password, String contact, String role, String email) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.email = email;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
