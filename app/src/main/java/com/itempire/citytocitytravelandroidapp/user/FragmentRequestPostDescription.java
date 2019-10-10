package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRequestPostDescription extends Fragment {

    private static final String TAG = FragmentRequestPostDescription.class.getName();

    View view;
    Context context;

    ImageView image_post_description;
    TextView detail_post_description;
    Button btn_avail_offer, btn_cancel_offer;
    RelativeLayout layout_btn_avail_cancel_request;

    private FragmentInteractionListenerInterface mListener;


    public FragmentRequestPostDescription() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_REQUEST_POST_DESCRIPTION);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_request_post_description, container, false);

            layout_btn_avail_cancel_request = (RelativeLayout) view.findViewById(R.id.layout_btn_avail_cancel_request);
            image_post_description = (ImageView) view.findViewById(R.id.image_post_description);
            detail_post_description = (TextView) view.findViewById(R.id.detail_post_description);
            btn_avail_offer = (Button) view.findViewById(R.id.btn_avail_offer);
            btn_cancel_offer = (Button) view.findViewById(R.id.btn_cancel_offer);


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

            if (getArguments().getString(Constant.OFFER_OR_POST_STATUS) != null && !getArguments().getString(Constant.OFFER_OR_POST_STATUS).equals(Constant.OFFER_PENDING_STATUS))
                layout_btn_avail_cancel_request.setVisibility(View.GONE);

            setBtn_avail_offer(post);
            setBtn_cancel_offer(post);

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

    private void setBtn_cancel_offer(final Post post) {
        btn_cancel_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForCancelRequest(post);
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

    private void dialogForCancelRequest(final Post post) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your request for this post wil be deleted");
        builder.setCancelable(false);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(post.getPostId()).child(MyFirebaseCurrentUserClass.mUser.getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request cancelled successfully!", Toast.LENGTH_LONG).show();
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
            mListener.onFragmentInteraction(Constant.TITLE_REQUEST_POST_DESCRIPTION);
    }

}
