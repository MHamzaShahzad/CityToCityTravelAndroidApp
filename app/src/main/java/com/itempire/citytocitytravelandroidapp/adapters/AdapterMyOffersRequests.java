package com.itempire.citytocitytravelandroidapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.List;

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

        holder.text_offers_data.setText(
                offer.getMessage() + "\n" +
                        offer.getNumberOfSeats() + "\n" +
                        CommonFeaturesClass.getMyPostsOfferRequestsStatus(offer.getOfferStatus())
        );

        if (offer.getOfferStatus().equals(Constant.POST_ACTIVE_STATUS)){
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

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView card_offers_list;
        RelativeLayout layout_accept_reject_request;
        TextView text_offers_data;
        Button btn_accept_offer_request, btn_reject_offer_request;

        public Holder(@NonNull View itemView) {
            super(itemView);

            card_offers_list = itemView.findViewById(R.id.card_offers_list);
            layout_accept_reject_request = itemView.findViewById(R.id.layout_accept_reject_request);
            text_offers_data = itemView.findViewById(R.id.text_offers_data);
            btn_accept_offer_request = itemView.findViewById(R.id.btn_accept_offer_request);
            btn_reject_offer_request = itemView.findViewById(R.id.btn_reject_offer_request);

        }
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
                            if (remainingSeats == 0){
                                expireOtherRequests(offer.getPostId());
                            }
                        }
                    });

                    holder.btn_reject_offer_request.setVisibility(View.GONE);
                    holder.btn_accept_offer_request.setEnabled(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Can't Applied Successfully!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void expireOtherRequests(String postId){
        MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot : snapshotIterable){
                        try {
                            AvailOffer offer = snapshot.getValue(AvailOffer.class);
                            if (offer != null && offer.getOfferStatus().equals(Constant.OFFER_PENDING_STATUS)){
                                snapshot.getRef().child("offerStatus").setValue(Constant.OFFER_EXPIRED_STATUS);
                            }
                        }catch (Exception e){
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
