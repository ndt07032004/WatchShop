package com.watchstore.model;

import java.sql.Timestamp;

public class Contact {
    private int id;
    private String name;
    private String email;
    private String message;
    private Timestamp createdAt; // ✅ Thêm thuộc tính thời gian gửi

    public Contact() {
    }

    // --- Getters và Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
