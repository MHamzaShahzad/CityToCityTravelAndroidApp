package com.itempire.citytocitytravelandroidapp.models;

public class AvailOffer {

    String offerAvailingUId, offerProviderUId, numberOfSeats, message, offerStatus;

    public AvailOffer(String offerAvailingUId,String offerProviderUId, String numberOfSeats, String message, String offerStatus) {
        this.offerAvailingUId = offerAvailingUId;
        this.offerProviderUId = offerProviderUId;
        this.numberOfSeats = numberOfSeats;
        this.message = message;
        this.offerStatus = offerStatus;
    }


    public String getOfferProviderUId() {
        return offerProviderUId;
    }

    public String getNumberOfSeats() {
        return numberOfSeats;
    }

    public String getMessage() {
        return message;
    }

    public String getOfferAvailingUId() {
        return offerAvailingUId;
    }

    public String getOfferStatus() {
        return offerStatus;
    }
}
