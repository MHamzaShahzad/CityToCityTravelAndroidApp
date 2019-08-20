package com.itempire.citytocitytravelandroidapp.models;

public class User {

    String userName, userPhoneNumber, userAddress, userGender, userImageUrl, userAccountStatus;

    public User() {
    }

    public User(String userName, String userPhoneNumber, String userAddress, String userGender, String userImageUrl, String userAccountStatus) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAddress = userAddress;
        this.userGender = userGender;
        this.userImageUrl = userImageUrl;
        this.userAccountStatus = userAccountStatus;
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

    public String getUserAccountStatus() {
        return userAccountStatus;
    }

}
