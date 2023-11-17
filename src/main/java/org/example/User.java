package org.example;

import java.util.UUID;

public class User {
    String id;
    String userName;

    public User() {
    }

    public User(String userName) {
        this.id = UUID.randomUUID().toString();
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String id) {
        this.userName = userName;
    }
}
