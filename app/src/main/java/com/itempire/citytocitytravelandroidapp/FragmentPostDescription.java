package com.itempire.citytocitytravelandroidapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.google.firebase.auth.FirebaseAuth;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.squareup.picasso.Picasso;

import static com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass.GetCurrentFirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPostDescription extends Fragment {

    Context context;
    View view;

    ImageView image_post_description;
    TextView detail_post_description;
    Button btn_avail_offer;

    public FragmentPostDescription(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_fragment_post_description, container, false);

            image_post_description = (ImageView) view.findViewById(R.id.image_post_description);
            detail_post_description = (TextView) view.findViewById(R.id.detail_post_description);
            btn_avail_offer = (Button) view.findViewById(R.id.btn_avail_offer);


            loadData();
        }
        return view;
    }

    private void loadData() {
        Post post = (Post) (getArguments() != null ? getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) : null);
        if (post != null) {
            try {

                Picasso.get().load(post.getVehicleImage()).into(image_post_description);

            } catch (Exception e) {
                e.printStackTrace();
            }
            detail_post_description.setText(
                    post.getProviderFirebaseUId() + "\n" +
                            post.getTotalNoOfSeatsAvailable() + "\n" +
                            post.getArrivalLocation() + "\n" +
                            post.getDepartureCity() + "\n" +
                            post.getDepartureLocation() + "\n" +
                            post.getEstimatedArrivalTime()
            );
            setBtn_avail_offer(post);
        }
    }

    private void setBtn_avail_offer(final Post post) {
        btn_avail_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForOfferRequest(post);
            }
        });
    }

    private void dialogForOfferRequest(final Post post){

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
                if (no_of_seats.length() == 0){
                    no_of_seats.setError("Field is required!");
                    return;
                }

                new MyFirebaseDatabaseClass().getPostsOfferDBReference().child(post.getPostId()).setValue(
                        new AvailOffer(
                                GetCurrentFirebaseUser().getUid(),
                                post.getProviderFirebaseUId(),
                                no_of_seats.getText().toString(),
                                message.getText().toString(),
                                Constant.OFFER_PENDING_STATUS
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
