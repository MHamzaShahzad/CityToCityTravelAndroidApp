package com.itempire.citytocitytravelandroidapp.models;

public class UserDeviceToken {

    String userId, deviceFBToken, isGeneralNotificationReceivingEnable , isSpecificNotificationReceivingEnable;

    public UserDeviceToken() {
    }

    public UserDeviceToken(String userId, String deviceFBToken, String isGeneralNotificationReceivingEnable, String isSpecificNotificationReceivingEnable) {
        this.userId = userId;
        this.deviceFBToken = deviceFBToken;
        this.isGeneralNotificationReceivingEnable = isGeneralNotificationReceivingEnable;
        this.isSpecificNotificationReceivingEnable = isSpecificNotificationReceivingEnable;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceFBToken() {
        return deviceFBToken;
    }

    public String getIsGeneralNotificationReceivingEnable() {
        return isGeneralNotificationReceivingEnable;
    }

    public String getIsSpecificNotificationReceivingEnable() {
        return isSpecificNotificationReceivingEnable;
    }
}
