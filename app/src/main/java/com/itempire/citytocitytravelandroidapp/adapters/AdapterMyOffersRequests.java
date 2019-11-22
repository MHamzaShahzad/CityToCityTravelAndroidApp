package com.itempire.citytocitytravelandroidapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.itempire.citytocitytravelandroidapp.CommonFeaturesClass.getMyPostsOfferRequestsStatus;

public class AdapterMyOffersRequests extends RecyclerView.Adapter<AdapterMyOffersRequests.Holder> {

    List<AvailOffer> list;
    Context context;
    Post post;

    private static AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;


    public AdapterMyOffersRequests(Context context, List<AvailOffer> list, Post post) {
        this.post = post;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMyOffersRequests.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_posts_offer_request_recycler_design, null);
        return new AdapterMyOffersRequests.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMyOffersRequests.Holder holder, int position) {

        AvailOffer offer = list.get(position);

        holder.text_offer_request_status.setText(CommonFeaturesClass.getMyPostsOfferRequestsStatus(offer.getOfferStatus()));
        holder.place_offers_date.setText(offer.getDate());
        holder.place_offers_message.setText(offer.getMessage());
        holder.place_offers_seats.setText(offer.getNumberOfSeats());
        holder.place_offers_time.setText(offer.getTime());


        holder.layout_accept_reject_request.setVisibility(View.GONE);
        if (offer.getOfferStatus().equals(Constant.OFFER_PENDING_STATUS)) {
            holder.layout_accept_reject_request.setVisibility(View.VISIBLE);
        }


        holder.btn_accept_offer_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "You accepted " + list.get(holder.getAdapterPosition()).getMessage(), Toast.LENGTH_LONG).show();
                dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setMessage("Are you sure ?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AcceptOrRejectOfferRequest(holder, list.get(holder.getAdapterPosition()), Constant.OFFER_ACCEPTED_STATUS);
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }
        });

        holder.btn_reject_offer_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "You rejected " + list.get(holder.getAdapterPosition()).getMessage(), Toast.LENGTH_LONG).show();
                dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setMessage("Are you sure ?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AcceptOrRejectOfferRequest(holder, list.get(holder.getAdapterPosition()), Constant.OFFER_REJECTED_STATUS);
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        getRequestingUserDetails(holder, offer.getOfferRequestingUId());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView card_offers_list;
        LinearLayout layout_accept_reject_request;
        TextView text_offer_request_status, nameRequestingUser, messageRequestingUser, callRequestingUser,
                place_offers_message, place_offers_seats, place_offers_time, place_offers_date;
        Button btn_accept_offer_request, btn_reject_offer_request;

        CircleImageView profileImageRequestingUser;

        public Holder(@NonNull View itemView) {
            super(itemView);

            card_offers_list = itemView.findViewById(R.id.card_offers_list);
            layout_accept_reject_request = itemView.findViewById(R.id.layout_accept_reject_request);

            place_offers_date = itemView.findViewById(R.id.place_offers_date);
            place_offers_time = itemView.findViewById(R.id.place_offers_time);
            place_offers_seats = itemView.findViewById(R.id.place_offers_seats);
            place_offers_message = itemView.findViewById(R.id.place_offers_message);

            text_offer_request_status = itemView.findViewById(R.id.text_offer_request_status);

            btn_accept_offer_request = itemView.findViewById(R.id.btn_accept_offer_request);
            btn_reject_offer_request = itemView.findViewById(R.id.btn_reject_offer_request);

            nameRequestingUser = itemView.findViewById(R.id.nameRequestingUser);
            messageRequestingUser = itemView.findViewById(R.id.messageRequestingUser);
            callRequestingUser = itemView.findViewById(R.id.callRequestingUser);
            profileImageRequestingUser = itemView.findViewById(R.id.profileImageRequestingUser);

        }
    }

    private void getRequestingUserDetails(final Holder holder, String requestingUserId) {
        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(requestingUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        final User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            holder.nameRequestingUser.setText(user.getUserName());
                            holder.messageRequestingUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommonFeaturesClass.setSend_sms_to_owner(context, user.getUserPhoneNumber());
                                }
                            });
                            holder.callRequestingUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommonFeaturesClass.setCall_to_owner(context, user.getUserPhoneNumber());
                                }
                            });

                            if (user.getUserImageUrl() != null && !user.getUserImageUrl().equals("") && !user.getUserImageUrl().equals("null"))
                                Picasso.get()
                                        .load(user.getUserImageUrl())
                                        .error(R.drawable.placeholder_photos)
                                        .placeholder(R.drawable.placeholder_photos)
                                        .centerInside().fit()
                                        .into(holder.profileImageRequestingUser);

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
    }

    private void AcceptOrRejectOfferRequest(final Holder holder, final AvailOffer offer, final String status) {
        MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(offer.getPostId()).child(offer.getOfferRequestingUId()).setValue(
                new AvailOffer(
                        offer.getPostId(),
                        offer.getOfferProviderUId(),
                        offer.getOfferRequestingUId(),
                        offer.getNumberOfSeats(),
                        offer.getMessage(),
                        status,
                        offer.getDate(),
                        offer.getTime()
                )
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Successfully Applied!", Toast.LENGTH_LONG).show();
                if (status.equals(Constant.OFFER_ACCEPTED_STATUS)) {
                    final int remainingSeats = Integer.parseInt(post.getMaximumNoOfSeatsAvailable()) - Integer.parseInt(offer.getNumberOfSeats());
                    HashMap<String, Object> map = new HashMap<>();

                    if (remainingSeats == 0) {
                        map.put("minimumNoOfSeatsAvailable", String.valueOf(remainingSeats));
                        map.put("postStatus", Constant.POST_COMPLETED_STATUS);
                    }
                    map.put("maximumNoOfSeatsAvailable", String.valueOf(remainingSeats));
                    map.put("totalNoOfSeatsAvailable", String.valueOf(remainingSeats));

                    MyFirebaseDatabaseClass.POSTS_REFERENCE.child(offer.getPostId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (remainingSeats == 0) {
                                expireOtherRequests(offer.getPostId());
                            }
                        }
                    });

                    holder.layout_accept_reject_request.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Can't Applied Successfully!", Toast.LENGTH_LONG).show();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
