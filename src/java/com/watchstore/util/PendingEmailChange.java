/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.watchstore.util;

/**
 *
 * @author PC
 */

import java.sql.Timestamp;

public class PendingEmailChange {
    private String username;
    private String newEmail;
    private String token;
    private Timestamp expiresAt;

    // Getters and Setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Timestamp getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }
}
