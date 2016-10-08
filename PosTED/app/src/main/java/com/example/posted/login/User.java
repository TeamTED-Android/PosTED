package com.example.posted.login;


public class User {

    private long mId;
    private String mUsername;
    private String mPassword;

    public User(long id, String username, String password) {
        this.mId = id;
        this.mUsername = username;
        this.mPassword = password;
    }

    public long getId() {
        return this.mId;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
}
