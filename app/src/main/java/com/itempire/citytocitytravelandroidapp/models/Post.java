package com.itempire.citytocitytravelandroidapp.models;

import java.io.Serializable;

public class Post implements Serializable {

    String postId, ownerVehicleId, departureDate, departureTime, departureLocation,
            arrivalLocation, departureCity, arrivalCity, departureLatitude, departureLongitude, arrivalLatitude, arrivalLongitude, totalNoOfSeatsAvailable, maximumNoOfSeatsAvailable,
            minimumNoOfSeatsAvailable, postTime, postStatus, costPerHead;


    public Post() {
    }


    public Post(String postId, String ownerVehicleId, String departureDate, String departureTime, String departureLocation, String arrivalLocation, String departureCity, String arrivalCity, String departureLatitude, String departureLongitude, String arrivalLatitude, String arrivalLongitude, String totalNoOfSeatsAvailable, String maximumNoOfSeatsAvailable, String minimumNoOfSeatsAvailable, String postTime, String postStatus, String costPerHead) {
        this.postId = postId;
        this.ownerVehicleId = ownerVehicleId;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureLatitude = departureLatitude;
        this.departureLongitude = departureLongitude;
        this.arrivalLatitude = arrivalLatitude;
        this.arrivalLongitude = arrivalLongitude;
        this.totalNoOfSeatsAvailable = totalNoOfSeatsAvailable;
        this.maximumNoOfSeatsAvailable = maximumNoOfSeatsAvailable;
        this.minimumNoOfSeatsAvailable = minimumNoOfSeatsAvailable;
        this.postTime = postTime;
        this.postStatus = postStatus;
        this.costPerHead = costPerHead;
    }

    public String getPostId() {
        return postId;
    }

    public String getOwnerVehicleId() {
        return ownerVehicleId;
    }




    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
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

    public String getDepartureLatitude() {
        return departureLatitude;
    }

    public String getDepartureLongitude() {
        return departureLongitude;
    }

    public String getArrivalLatitude() {
        return arrivalLatitude;
    }

    public String getArrivalLongitude() {
        return arrivalLongitude;
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
}
