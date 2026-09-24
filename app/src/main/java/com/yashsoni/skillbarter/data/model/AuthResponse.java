package com.yashsoni.skillbarter.data.model;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    private String token;
    private User user;
    private String message;

    public AuthResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
