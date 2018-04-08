package com.example.root.medium;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by root on 26/12/17.
 */
@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public String UID;
    public String PhotoUrl;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String username, String email,String UID,String PhotoUrl) {
        this.username = username;
        this.email = email;
        this.UID = UID;
        this.PhotoUrl = PhotoUrl;
    }

}
