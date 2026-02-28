package com.example.demo.dto;

public class UserRegister {
    private String username;

    public UserRegister()  {}

    public UserRegister(String username) {
        this.username = username;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
}
