package com.itempire.citytocitytravelandroidapp.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itempire.citytocitytravelandroidapp.MainActivity;

public class MyFirebaseCurrentUserClass {

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static FirebaseUser GetCurrentFirebaseUser(){
        return mAuth.getCurrentUser();
    }

    public static void SignOut(final Context context) {

        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ((Activity) context).startActivity(new Intent((Activity) context, MainActivity.class));
                        ((Activity) context).finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


}
