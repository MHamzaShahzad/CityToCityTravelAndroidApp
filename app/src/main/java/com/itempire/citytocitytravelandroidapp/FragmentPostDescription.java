package com.itempire.citytocitytravelandroidapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
import com.itempire.citytocitytravelandroidapp.controllers.SendPushNotificationFirebase;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.itempire.citytocitytravelandroidapp.user.FragmentMapGetLocationForPost;
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
    TextView place_post_desc_post_time, place_post_departure_date, place_post_departure_time, place_post_departure_city,
            place_post_departure_location, place_post_arrival_city, place_post_arrival_location, place_post_totalNoOfSeatsAvailable,
            place_post_maximumNoOfSeatsAvailable, place_post_minimumNoOfSeatsAvailable;
    Button btn_avail_offer, send_sms_to_owner, call_to_owner, btn_view_on_map;

    private FragmentInteractionListenerInterface mListener;

    public FragmentPostDescription() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_POST_DESCRIPTION);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_post_description, container, false);

            image_post_description = (ImageView) view.findViewById(R.id.image_post_description);

            place_post_desc_post_time = (TextView) view.findViewById(R.id.place_post_desc_post_time);
            place_post_departure_date = (TextView) view.findViewById(R.id.place_post_departure_date);
            place_post_departure_time = (TextView) view.findViewById(R.id.place_post_departure_time);
            place_post_departure_city = (TextView) view.findViewById(R.id.place_post_departure_city);
            place_post_departure_location = (TextView) view.findViewById(R.id.place_post_departure_location);
            place_post_arrival_city = (TextView) view.findViewById(R.id.place_post_arrival_city);
            place_post_arrival_location = (TextView) view.findViewById(R.id.place_post_arrival_location);
            place_post_totalNoOfSeatsAvailable = (TextView) view.findViewById(R.id.place_post_totalNoOfSeatsAvailable);
            place_post_maximumNoOfSeatsAvailable = (TextView) view.findViewById(R.id.place_post_maximumNoOfSeatsAvailable);
            place_post_minimumNoOfSeatsAvailable = (TextView) view.findViewById(R.id.place_post_minimumNoOfSeatsAvailable);

            btn_avail_offer = (Button) view.findViewById(R.id.btn_avail_offer);
            btn_view_on_map = (Button) view.findViewById(R.id.btn_view_on_map);
            send_sms_to_owner = view.findViewById(R.id.send_sms_to_owner);
            call_to_owner = view.findViewById(R.id.call_to_owner);


            loadData();
        }
        return view;
    }

    private void loadData() {
        final Post post = (Post) (getArguments() != null ? getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) : null);
        if (post != null) {
            CommonFeaturesClass.loadPostImage(image_post_description, post);

            place_post_desc_post_time.setText(post.getPostTime());

            place_post_departure_date.setText(post.getDepartureDate());
            place_post_departure_time.setText(post.getDepartureTime());
            place_post_departure_city.setText(post.getDepartureCity());
            place_post_departure_location.setText(post.getDepartureLocation());

            place_post_arrival_city.setText(post.getArrivalCity());
            place_post_arrival_location.setText(post.getArrivalLocation());

            place_post_totalNoOfSeatsAvailable.setText(post.getTotalNoOfSeatsAvailable());
            place_post_maximumNoOfSeatsAvailable.setText(post.getMaximumNoOfSeatsAvailable());
            place_post_minimumNoOfSeatsAvailable.setText(post.getMinimumNoOfSeatsAvailable());

            btn_view_on_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentMapGetLocationForPost mapGetLocationForPost = new FragmentMapGetLocationForPost();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.POST_OBJECT_DESCRIPTION, post);
                    bundle.putBoolean(Constant.VIEW_ON_MAP, true);
                    mapGetLocationForPost.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, mapGetLocationForPost).addToBackStack(null).commit();
                }
            });

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
                        SendPushNotificationFirebase.buildAndSendNotification(context,post.getOwnerVehicleId(),"Offer Request","A user is requesting "+ no_of_seats.getText().toString() + " and saying, " + message.getText().toString());
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement FragmentInteractionListenerInterface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_POST_DESCRIPTION);
    }

}
