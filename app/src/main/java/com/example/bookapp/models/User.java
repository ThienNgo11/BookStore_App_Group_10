package com.example.bookapp.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String role;
    private boolean isActive; // true = active, false = inactive

    public User() {
    }

    public User(int id, String username, String password, String fullname, String email, String phone, String address, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.isActive = true; // Mặc định là active
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Sửa lại thành boolean
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Giữ lại method này để tương thích với code cũ (nếu có)
    public int getIsActive() {
        return isActive ? 1 : 0;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive == 1;
    }
}