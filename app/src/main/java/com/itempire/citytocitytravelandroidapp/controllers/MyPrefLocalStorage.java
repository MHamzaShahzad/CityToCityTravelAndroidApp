package com.itempire.citytocitytravelandroidapp.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.models.User;

public class MyPrefLocalStorage {

    private static final String TAG = MyPrefLocalStorage.class.getName();

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

    public static void saveCurrentUserData(Context context, User user ){

        sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user); // user - instance of User
        Log.d(TAG, "saveCurrentUserData: "+ json );
        editor.putString(Constant.SHARED_PREF_USER_KEY, json).apply();

    }

    public static User getCurrentUserData(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constant.SHARED_PREF_USER_KEY, new Gson().toJson(new User()));
        return gson.fromJson(json, User.class);
    }

    public String getDeviceToken(){
        return sharedPreferences.getString(TOKEN_VALUE, "");
    }

    public static void clearPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }

}
