package com.itempire.citytocitytravelandroidapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVehicleDescription extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentVehicleDescription.class.getName();
    Context context;
    View view;
    Vehicle vehicle;
    List<Post> myActivePosts;
    ImageView vehicleImage, profileImage;
    Button btnActiveInActiveVehicle, send_sms_to_owner, call_to_owner, btnRemoveVehicle;
    TextView vehicleModelPlace, vehicleNumberPlace, name, phoneNumber;
    LinearLayout layout_edit_remove_vehicle, layout_call_sms_vehicle;

    Bundle bundleArgument;
    private static final String VEHICLE_BUTTON_TEXT_ACTIVE = "Active";
    private static final String VEHICLE_BUTTON_TEXT_INACTIVE = "In-Active";

    public FragmentVehicleDescription() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_vehicle_description, container, false);
            getLayoutWidgets();
            setClickListeners();

            bundleArgument = getArguments();
            if (bundleArgument != null && bundleArgument.getSerializable(Constant.VEHICLE_DESCRIPTION_NAME) != null) {
                vehicle = (Vehicle) bundleArgument.getSerializable(Constant.VEHICLE_DESCRIPTION_NAME);
                if (vehicle != null) {

                    if (vehicle.getVehicleImage() != null)
                        try {
                            Picasso.get().load(vehicle.getVehicleImage()).placeholder(R.drawable.placeholder_photos).fit().into(vehicleImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    if (bundleArgument.getBoolean(Constant.VEHICLE_DESCRIPTION_ADMIN)) {
                        layout_call_sms_vehicle.setVisibility(View.VISIBLE);
                    }

                    if (bundleArgument.getBoolean(Constant.VEHICLE_DESCRIPTION_USER)) {
                        layout_edit_remove_vehicle.setVisibility(View.VISIBLE);
                        btnActiveInActiveVehicle.setVisibility(View.GONE);
                    }

                    MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(vehicle.getVehicleOwnerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {

                                try {

                                    User user = dataSnapshot.getValue(User.class);
                                    if (user != null) {
                                        if (user.getUserImageUrl() != null)
                                            try {
                                                Picasso.get().load(user.getUserImageUrl()).placeholder(R.drawable.user_avatar).fit().into(profileImage);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        name.setText(user.getUserName());
                                        phoneNumber.setText(user.getUserPhoneNumber());
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    vehicleModelPlace.setText(vehicle.getVehicleModel());
                    vehicleNumberPlace.setText(vehicle.getVehicleNumber());

                    setTextToButton();

                }
            }

        }
        return view;
    }

    private void getLayoutWidgets() {

        layout_edit_remove_vehicle = view.findViewById(R.id.layout_edit_remove_vehicle);
        layout_call_sms_vehicle = view.findViewById(R.id.layout_call_sms_vehicle);

        vehicleImage = view.findViewById(R.id.vehicleImage);
        profileImage = view.findViewById(R.id.profileImage);

        vehicleModelPlace = view.findViewById(R.id.vehicleModelPlace);
        vehicleNumberPlace = view.findViewById(R.id.vehicleNumberPlace);
        name = view.findViewById(R.id.name);
        phoneNumber = view.findViewById(R.id.phoneNumber);


        btnActiveInActiveVehicle = view.findViewById(R.id.btnActiveInActiveVehicle);
        send_sms_to_owner = view.findViewById(R.id.send_sms_to_owner);
        call_to_owner = view.findViewById(R.id.call_to_owner);
        btnRemoveVehicle = view.findViewById(R.id.btnRemoveVehicle);
    }

    private void setClickListeners() {

        btnActiveInActiveVehicle.setOnClickListener(this);
        send_sms_to_owner.setOnClickListener(this);
        call_to_owner.setOnClickListener(this);
        btnRemoveVehicle.setOnClickListener(this);
    }

    private void setTextToButton() {
        if (vehicle.getVehicleStatus().equals(Constant.VEHICLE_STATUS_INACTIVE)) {
            btnActiveInActiveVehicle.setText(VEHICLE_BUTTON_TEXT_ACTIVE);
        }
        if (vehicle.getVehicleStatus().equals(Constant.VEHICLE_STATUS_ACTIVE)) {
            btnActiveInActiveVehicle.setText(VEHICLE_BUTTON_TEXT_INACTIVE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnActiveInActiveVehicle:
                setBtnActiveInActiveVehicle();
                break;
            case R.id.btnRemoveVehicle:
                Log.e(TAG, "onClick: ");
                removeMyVehicle();
                break;
            case R.id.send_sms_to_owner:
                break;
            case R.id.call_to_owner:
                break;
        }

    }

    private void changeVehicleStatus(String hostelStatus) {

        MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(vehicle.getVehicleOwnerId()).child("vehicleStatus").setValue(hostelStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Status Changed", Toast.LENGTH_SHORT).show();
                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBtnActiveInActiveVehicle() {
        if (vehicle != null) {
            if (vehicle.getVehicleStatus().equals(Constant.VEHICLE_STATUS_INACTIVE)) {
                changeVehicleStatus(Constant.VEHICLE_STATUS_ACTIVE);
            }
            if (vehicle.getVehicleStatus().equals(Constant.VEHICLE_STATUS_ACTIVE)) {
                inactivePostForVehicle();
            }
        }
    }

    private void removeMyVehicle() {
        MyFirebaseDatabaseClass.POSTS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    getAllActivePosts(dataSnapshot);

                    Log.e(TAG, "onDataChange: POSTS : " + myActivePosts.size());

                    if (myActivePosts.size() > 0) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("You have " + myActivePosts.size() + " active uncompleted post, removing your vehicle will expire your post.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (final Post post : myActivePosts) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("postStatus", Constant.POST_BLOCKED_STATUS);
                                    MyFirebaseDatabaseClass.POSTS_REFERENCE.child(post.getPostId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            expireOtherRequests(post.getPostId());
                                        }
                                    });
                                }
                                if (vehicle != null)
                                    MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(vehicle.getVehicleOwnerId()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                                        }
                                    });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder.setCancelable(true);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        if (vehicle != null)
                            MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(vehicle.getVehicleOwnerId()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                                }
                            });
                    }

                } else {
                    if (vehicle != null)
                        MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(vehicle.getVehicleOwnerId()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getAllActivePosts(DataSnapshot dataSnapshot) {
        myActivePosts = new ArrayList<>();
        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
        for (DataSnapshot snapshot : snapshots) {
            try {
                Post post = snapshot.getValue(Post.class);
                if (post != null && post.getOwnerVehicleId().equals(MyFirebaseCurrentUserClass.mUser.getUid()) && post.getPostStatus().equals(Constant.POST_ACTIVE_STATUS)) {
                    myActivePosts.add(post);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inactivePostForVehicle() {
        MyFirebaseDatabaseClass.POSTS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    getAllActivePosts(dataSnapshot);

                    Log.e(TAG, "onDataChange: POSTS : " + myActivePosts.size());

                    if (myActivePosts.size() > 0) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("User have " + myActivePosts.size() + " active uncompleted post, inactivating this vehicle will BLOCK his posts and EXPIRE requests that user have received.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (final Post post : myActivePosts) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("postStatus", Constant.POST_BLOCKED_STATUS);
                                    MyFirebaseDatabaseClass.POSTS_REFERENCE.child(post.getPostId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            expireOtherRequests(post.getPostId());
                                        }
                                    });
                                }
                                changeVehicleStatus(Constant.VEHICLE_STATUS_INACTIVE);

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder.setCancelable(true);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else
                        changeVehicleStatus(Constant.VEHICLE_STATUS_INACTIVE);


                } else
                    changeVehicleStatus(Constant.VEHICLE_STATUS_INACTIVE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void expireOtherRequests(String postId) {
        MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshotIterable) {
                        try {
                            AvailOffer offer = snapshot.getValue(AvailOffer.class);
                            if (offer != null && offer.getOfferStatus().equals(Constant.OFFER_PENDING_STATUS)) {
                                snapshot.getRef().child("offerStatus").setValue(Constant.OFFER_EXPIRED_STATUS);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                } else {
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
