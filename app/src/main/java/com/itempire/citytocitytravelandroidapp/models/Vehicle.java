package com.itempire.citytocitytravelandroidapp.models;

import java.io.Serializable;

public class Vehicle implements Serializable {

    String vehicleOwnerId, vehicleImage, vehicleModel, vehicleNumber, vehicleStatus ;

    public Vehicle() {
    }

    public Vehicle(String vehicleOwnerId, String vehicleImage, String vehicleModel, String vehicleNumber, String vehicleStatus) {
        this.vehicleOwnerId = vehicleOwnerId;
        this.vehicleImage = vehicleImage;
        this.vehicleModel = vehicleModel;
        this.vehicleNumber = vehicleNumber;
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleOwnerId() {
        return vehicleOwnerId;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

}
