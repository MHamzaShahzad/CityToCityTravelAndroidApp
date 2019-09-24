package com.itempire.citytocitytravelandroidapp.models;

public class User {

    String userId, userName, userPhoneNumber, userAddress, userGender, userImageUrl, userType;

    public User() {
    }

    public User(String userId, String userPhoneNumber) {
        this.userId = userId;
        this.userPhoneNumber = userPhoneNumber;
    }

    public User(String userId, String userName, String userPhoneNumber, String userAddress, String userGender, String userImageUrl, String userType) {
        this.userId = userId;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAddress = userAddress;
        this.userGender = userGender;
        this.userImageUrl = userImageUrl;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserType() {
        return userType;
    }

}
