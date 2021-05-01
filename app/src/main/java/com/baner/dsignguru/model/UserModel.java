package com.baner.dsignguru.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class UserModel implements Serializable {

    public String uid;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String occupation;
    public String language;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    public boolean isNew, isCreated;

    public UserModel(String uid, String firstName, String lastName, String email, String phone,
                     String occupation, String language) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
        this.language = language;
    }
}
