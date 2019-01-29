package com.crycetruly.happyhour.model;

/**
 * Created by Elia on 17/06/2018.
 */

public class User {
    private String email;

    public User(String email) {
        this.email = email;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

