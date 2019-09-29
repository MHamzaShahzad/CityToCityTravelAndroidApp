package com.itempire.citytocitytravelandroidapp;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPostDescription extends Fragment {

    Context context;
    View view;

    ImageView image_post_description;
    TextView detail_post_description;
    Button btn_avail_offer, send_sms_to_owner, call_to_owner;

    public FragmentPostDescription() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_post_description, container, false);

            image_post_description = (ImageView) view.findViewById(R.id.image_post_description);
            detail_post_description = (TextView) view.findViewById(R.id.detail_post_description);
            btn_avail_offer = (Button) view.findViewById(R.id.btn_avail_offer);
            send_sms_to_owner = view.findViewById(R.id.send_sms_to_owner);
            call_to_owner = view.findViewById(R.id.call_to_owner);


            loadData();
        }
        return view;
    }

    private void loadData() {
        Post post = (Post) (getArguments() != null ? getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) : null);
        if (post != null) {
            CommonFeaturesClass.loadPostImage(image_post_description, post);
            detail_post_description.setText(
                    post.getOwnerVehicleId() + "\n" +
                            post.getTotalNoOfSeatsAvailable() + "\n" +
                            post.getArrivalLocation() + "\n" +
                            post.getDepartureCity() + "\n" +
                            post.getDepartureLocation() + "\n"
            );

            getPostOwnerData(post.getOwnerVehicleId());

            if (post.getOwnerVehicleId().equals(MyFirebaseCurrentUserClass.mUser.getUid()))
                btn_avail_offer.setVisibility(View.GONE);
            else
                setBtn_avail_offer(post);
        }
    }

    private void getPostOwnerData(String ownerId) {
        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(ownerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null)
                            setCallSmsListeners(user.getUserPhoneNumber());
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

    private void setCallSmsListeners(final String phoneNumber) {
        call_to_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFeaturesClass.setCall_to_owner(context, phoneNumber);
            }
        });
        send_sms_to_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFeaturesClass.setSend_sms_to_owner(context, phoneNumber);
            }
        });
    }

    private void setBtn_avail_offer(final Post post) {
        btn_avail_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForOfferRequest(post);
            }
        });
    }

    private void dialogForOfferRequest(final Post post) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.layout_request_offer_dialog, null);
        final EditText no_of_seats = (EditText) view.findViewById(R.id.no_of_seats);
        final EditText message = (EditText) view.findViewById(R.id.message);
        final Button btn_send_request = (Button) view.findViewById(R.id.btn_send_request);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (no_of_seats.length() == 0) {
                    no_of_seats.setError("Field is required!");
                    return;
                }
                Date date = new Date();
                MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(post.getPostId()).child(MyFirebaseCurrentUserClass.mUser.getUid()).setValue(
                        new AvailOffer(
                                post.getPostId(),
                                post.getOwnerVehicleId(),
                                MyFirebaseCurrentUserClass.mUser.getUid(),
                                no_of_seats.getText().toString(),
                                message.getText().toString(),
                                Constant.OFFER_PENDING_STATUS,
                                new SimpleDateFormat("HH:mm:ss aa").format(date),
                                new SimpleDateFormat("yyyy-MM-dd").format(date)

                        )
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Offer request has been sent successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                dialog.dismiss();
            }
        });

    }

}
