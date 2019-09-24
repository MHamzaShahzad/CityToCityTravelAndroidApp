package com.itempire.citytocitytravelandroidapp.models;

import java.io.Serializable;

public class AvailOffer {

    String postId, offerProviderUId,offerRequestingUId, numberOfSeats, message, offerStatus, date, time;

    public AvailOffer() {
    }

    public AvailOffer(String postId, String offerProviderUId,String offerRequestingUId, String numberOfSeats, String message, String offerStatus, String date, String time) {
        this.postId = postId;
        this.offerProviderUId = offerProviderUId;
        this.offerRequestingUId = offerRequestingUId;
        this.numberOfSeats = numberOfSeats;
        this.message = message;
        this.offerStatus = offerStatus;
        this.date = date;
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public String getOfferProviderUId() {
        return offerProviderUId;
    }

    public String getOfferRequestingUId() {
        return offerRequestingUId;
    }

    public String getNumberOfSeats() {
        return numberOfSeats;
    }

    public String getMessage() {
        return message;
    }

    public String getOfferStatus() {
        return offerStatus;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
