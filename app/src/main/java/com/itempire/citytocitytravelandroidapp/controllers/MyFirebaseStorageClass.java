package com.itempire.citytocitytravelandroidapp.controllers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirebaseStorageClass {

    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static String USER_IMAGES_REFERENCE = "user_images/";
    public static String POSTS_IMAGES_REFERENCE = "post_images/";

    public StorageReference getUserImagesReference(){
        return storage.getReference().child(USER_IMAGES_REFERENCE);

    }

    public StorageReference getPostsImagesReference(){
        return storage.getReference().child(POSTS_IMAGES_REFERENCE);
    }
}
