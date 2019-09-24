package com.itempire.citytocitytravelandroidapp.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseDatabaseClass {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static final DatabaseReference USERS_PROFILE_REFERENCE = database.getReference("Users");
    public static final DatabaseReference USERS_TOKEN_REFERENCE = database.getReference("UsersTokens");
    public static final DatabaseReference VEHICLES_REFERENCE = database.getReference("Vehicles");
    public static final DatabaseReference POSTS_REFERENCE = database.getReference("Posts");
    public static final DatabaseReference POSTS_OFFERS_REFERENCE = database.getReference("Offers");

}
