package com.itempire.citytocitytravelandroidapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;
import com.squareup.picasso.Picasso;

public class CommonFeaturesClass {

    public static void setSend_sms_to_owner(Context context, String phoneNumber){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body","Body of Message");
        ((Activity) context).startActivity(smsIntent);
    }

    public static void setCall_to_owner(Context context, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        // Send phone number to intent as data
        intent.setData(Uri.parse("tel:" + phoneNumber));
        // Start the dialer app activity with number
        ((Activity)context).startActivity(intent);
    }

    public static String getMyPostsOfferRequestsStatus(String code) {
        String stringStatus;
        switch (code) {
            case Constant.OFFER_ACCEPTED_STATUS:
                stringStatus = "Accepted";
                break;
            case Constant.OFFER_PENDING_STATUS:
                stringStatus = "Pending";
                break;
            case Constant.OFFER_REJECTED_STATUS:
                stringStatus = "Rejected";
                break;
            case Constant.OFFER_EXPIRED_STATUS:
                stringStatus = "Expired";
                break;
            default:
                stringStatus = "";
        }
        return stringStatus;
    }

    public static void loadPostImage(final ImageView imageView, Post post){
        MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(post.getOwnerVehicleId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                        if (vehicle != null)
                            if (vehicle.getVehicleImage() != null)
                                Picasso.get().load(vehicle.getVehicleImage()).into(imageView);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void showCustomDialog(Context context,String title, String description){
        AlertDialog dialog = null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

}
