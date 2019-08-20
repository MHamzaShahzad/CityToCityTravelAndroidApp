package com.itempire.citytocitytravelandroidapp.models;

public class Post {

    String vehicleModel, vehicleNumber, vehicleImage, departureTime, estimatedArrivalTime, departureLocation,
    arrivalLocation, departureCity, arrivalCity, totalNoOfSeatsAvailable, maximumNoOfSeatsAvailable,
            minimumNoOfSeatsAvailable, postTime, postStatus, costPerHead, providerFirebaseUId;


    public Post() {
    }

    public Post(String vehicleModel, String vehicleNumber, String vehicleImage, String departureTime, String estimatedArrivalTime, String departureLocation, String arrivalLocation, String departureCity, String arrivalCity, String totalNoOfSeatsAvailable, String maximumNoOfSeatsAvailable, String minimumNoOfSeatsAvailable, String postTime, String postStatus, String costPerHead, String providerFirebaseUId) {
        this.vehicleModel = vehicleModel;
        this.vehicleNumber = vehicleNumber;
        this.vehicleImage = vehicleImage;
        this.departureTime = departureTime;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.totalNoOfSeatsAvailable = totalNoOfSeatsAvailable;
        this.maximumNoOfSeatsAvailable = maximumNoOfSeatsAvailable;
        this.minimumNoOfSeatsAvailable = minimumNoOfSeatsAvailable;
        this.postTime = postTime;
        this.postStatus = postStatus;
        this.costPerHead = costPerHead;
        this.providerFirebaseUId = providerFirebaseUId;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public String getTotalNoOfSeatsAvailable() {
        return totalNoOfSeatsAvailable;
    }

    public String getMaximumNoOfSeatsAvailable() {
        return maximumNoOfSeatsAvailable;
    }

    public String getMinimumNoOfSeatsAvailable() {
        return minimumNoOfSeatsAvailable;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public String getCostPerHead() {
        return costPerHead;
    }

    public String getProviderFirebaseUId() {
        return providerFirebaseUId;
    }
}
