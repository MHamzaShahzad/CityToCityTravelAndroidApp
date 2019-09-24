package com.itempire.citytocitytravelandroidapp.models;

public class OffersCategorise {

    String offerTypeId, offerTypeName;

    public OffersCategorise(String offerTypeId, String offerTypeName) {
        this.offerTypeId = offerTypeId;
        this.offerTypeName = offerTypeName;
    }

    public String getOfferTypeId() {
        return offerTypeId;
    }

    public String getOfferTypeName() {
        return offerTypeName;
    }
}
