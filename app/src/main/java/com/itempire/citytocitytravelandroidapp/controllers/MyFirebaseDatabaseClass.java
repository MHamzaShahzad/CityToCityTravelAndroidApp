package com.itempire.citytocitytravelandroidapp.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabaseClass {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static final String USERS_PROFILE_REFERENCE = "Users";
    public static final String USERS_TOKEN_REFERENCE = "UsersTokens";
    public static final String POSTS_REFERENCE = "Posts";

    public DatabaseReference getUsersProfileDBReference(){
        return database.getReference(USERS_PROFILE_REFERENCE);
    }

    public DatabaseReference getUsersTokenDBReference(){
        return database.getReference(USERS_TOKEN_REFERENCE);
    }

    public DatabaseReference getUsersPostsDBReference(){
        return database.getReference(POSTS_REFERENCE);
    }

}
