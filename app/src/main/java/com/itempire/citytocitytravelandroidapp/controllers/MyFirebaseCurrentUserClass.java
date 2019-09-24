package com.itempire.citytocitytravelandroidapp.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.MainActivity;
import com.itempire.citytocitytravelandroidapp.MainDrawerActivity;
import com.itempire.citytocitytravelandroidapp.models.User;

public class MyFirebaseCurrentUserClass {

    private static final String TAG = MyFirebaseCurrentUserClass.class.getName();

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser mUser = mAuth.getCurrentUser();
    private static ValueEventListener userValueEventListener;


    public static void initFirebaseUser() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static void SignOut(final Context context) {

        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        MainDrawerActivity.removeUserValueEventListener();
                        MyServicesControllerClass.stopCustomBackgroundService(context);
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

    public static void initAndSetUserValueEventListener(final Context context) {
        Log.e(TAG, "initAndSetUserValueEventListener: " + MyFirebaseCurrentUserClass.mUser.getUid());

        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null)
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        MyPrefLocalStorage.saveCurrentUserData(context, user);
                        MainDrawerActivity.updateUserInNavHeader(user);
                        Log.d(TAG, "onDataChange: " + dataSnapshot + "\n" + MyPrefLocalStorage.getCurrentUserData(context).getUserPhoneNumber());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).addValueEventListener(userValueEventListener);
    }

    public static void removeUserValueEventListener(Context context) {

        Log.d(TAG, "removeUserValueEventListener: ");

        if (userValueEventListener != null)
            MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).removeEventListener(userValueEventListener);
        MyPrefLocalStorage.clearPreferences(context);
    }


}
