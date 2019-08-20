package com.itempire.citytocitytravelandroidapp.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefLocalStorage {

    public static final String PREF_NAME = "AppInfo";
    private static final String TOKEN_VALUE = "AppInfo";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    Context context;

    public MyPrefLocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void storeDeviceToken(String token){

        editor.putString(TOKEN_VALUE, token).apply();

    }

    public String getDeviceToken(){
        return sharedPreferences.getString(TOKEN_VALUE, "");
    }


}
