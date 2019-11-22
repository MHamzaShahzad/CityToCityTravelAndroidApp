package com.itempire.citytocitytravelandroidapp;

public class Constant {

    public static final String SHARED_PREF_NAME = "AppInfo";
    public static final String SHARED_PREF_USER_KEY = "userObject";

    public static final String USER_TYPE_ADMIN = "0";
    public static final String USER_TYPE_NON_ADMIN = "1";

    public static final String VEHICLE_STATUS_INACTIVE = "0";
    public static final String VEHICLE_STATUS_ACTIVE= "1";

    public static final String VEHICLE_DESCRIPTION_NAME = "vehicle";

    public static final String VEHICLE_DESCRIPTION_ADMIN = "vehicle_description_admin";
    public static final String VEHICLE_DESCRIPTION_USER = "vehicle_description_user";

    public static final String POST_ACTIVE_STATUS = "0";
    public static final String POST_COMPLETED_STATUS = "1";
    public static final String POST_BLOCKED_STATUS = "2";
    public static final String POST_EXPIRED_STATUS = "3";

    public static final String ACTION_SELECTED_POST_TYPE= "android.action.selected.post.type";
    public static final String POST_OBJECT_DESCRIPTION = "object";

    public static final String OFFER_PENDING_STATUS = "0";
    public static final String OFFER_ACCEPTED_STATUS = "1";
    public static final String OFFER_REJECTED_STATUS = "2";
    public static final String OFFER_EXPIRED_STATUS = "3";
    public static final String ACTION_SELECTED_OFFER = "android.action.selected.offer.type";

    public static final String OFFER_OR_POST_STATUS = "offer_or_post_status";

    public static final String USER_GENDER_MALE = "0";
    public static final String USER_GENDER_FEMALE = "1";
    public static final String USER_GENDER_OTHERS = "2";

    public static final String LOCATION_RECEIVING_FILTER = "android.intent.action.receive.location.broadcast";
    public static final String LOCATION_ADDRESS = "locationAddress";
    public static final String LOCATION_ADDRESS_CITY = "locationAddressCity";
    public static final String LOCATION_LATITUDE = "latitude";
    public static final String LOCATION_LONGITUDE= "longitude";
    public static final String DEPT_LOCATION_KEY = "getDeptLocationForPost";
    public static final String ARRIVAL_LOCATION_KEY = "getArrivalLocationForPost";


    public static final String VIEW_ON_MAP = "viewOnMap";


    public static final String TITLE_HOME = "Home";
    public static final String TITLE_USERS_AND_ADMINS = "Users and Admins";
    public static final String TITLE_VEHICLES_LIST = "Vehicles List";
    public static final String TITLE_MY_REQUESTS = "My Requests";
    public static final String TITLE_MY_POSTS = "My Posts";
    public static final String TITLE_VEHICLE_DESCRIPTION = "Vehicle Description";
    public static final String TITLE_UPLOAD_VEHICLE = "Upload Vehicle";
    public static final String TITLE_PROFILE = "Profile";
    public static final String TITLE_EDIT_PROFILE = "Edit Profile";
    public static final String TITLE_CREATE_POST = "Create Post";
    public static final String TITLE_SELECT_LOCATION = "Select Location";
    public static final String TITLE_POST_DESCRIPTION = "Description";
    public static final String TITLE_REQUEST_POST_DESCRIPTION = "Request Description";
    public static final String TITLE_CONTACT_US = "Contact Us";

    public static final String STRING_COMPLETE_MY_POST_REF = "postStatus";
    public static final String STRING_OFFER_STATUS_REF = "offerStatus";
}
