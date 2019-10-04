package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterAllPosts;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterMyOffersRequests;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyPostsDescription extends Fragment {

    private static final String TAG = FragmentMyPostsDescription.class.getName();

    View view;
    Context context;

    ImageView image_my_post_description;
    TextView detail_my_post_description;
    RecyclerView recycler_offer_requests_for_my_post;
    Button btnCompletePost;
    private static ValueEventListener valueEventListener;
    private DatabaseReference postOfferDBRef;

    private FragmentInteractionListenerInterface mListener;

    public FragmentMyPostsDescription() {
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
            view = inflater.inflate(R.layout.fragment_my_posts_description, container, false);

            image_my_post_description = (ImageView) view.findViewById(R.id.image_my_post_description);
            detail_my_post_description = (TextView) view.findViewById(R.id.detail_my_post_description);
            btnCompletePost = (Button) view.findViewById(R.id.btnCompletePost);

            recycler_offer_requests_for_my_post = (RecyclerView) view.findViewById(R.id.recycler_offer_requests_for_my_post);
            recycler_offer_requests_for_my_post.setHasFixedSize(true);
            recycler_offer_requests_for_my_post.setLayoutManager(new LinearLayoutManager(context));

            view.findViewById(R.id.text_no_data_found).setVisibility(View.GONE);


            final Post post = (Post) (getArguments() != null ? getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) : null);
            if (post != null) {

                CommonFeaturesClass.loadPostImage(image_my_post_description, post);

                postOfferDBRef =  MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE.child(post.getPostId());

                if (post.getPostStatus().equals(Constant.POST_ACTIVE_STATUS)){
                    btnCompletePost.setVisibility(View.VISIBLE);
                    btnCompletePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            completeMyPost(post);
                        }
                    });
                }

                setValueEventListener(post);

            }
        }
        return view;
    }


    private void setValueEventListener(final Post post) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<AvailOffer> list = new ArrayList<>();

                Iterable<DataSnapshot> listSnapshots = dataSnapshot.getChildren();

                for (DataSnapshot snapshot : listSnapshots) {

                    try {
                        AvailOffer offer = snapshot.getValue(AvailOffer.class);

                        if (offer != null && offer.getOfferProviderUId().equals(MyFirebaseCurrentUserClass.mUser.getUid())) {


                            Log.e(TAG, "onDataChange: " +
                                    offer.getMessage() + "\n" +
                                    offer.getNumberOfSeats() + "\n" +
                                    offer.getOfferProviderUId() + "\n" +
                                    offer.getOfferStatus()
                            );

                            list.add(
                                    offer
                            );


                        } else {
                            Log.e(TAG, "onDataChange: NO_POST_EXIST");
                        }

                        recycler_offer_requests_for_my_post.setAdapter(new AdapterMyOffersRequests(context, list, post));
                        if (list.size() == 0) {
                            view.findViewById(R.id.text_no_data_found).setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postOfferDBRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeValueEventListener();
    }

    private void removeValueEventListener() {
        if (postOfferDBRef != null && valueEventListener != null)
            postOfferDBRef.removeEventListener(valueEventListener);
    }

    private void completeMyPost(final Post post){
        HashMap<String, Object> map = new HashMap<>();
        map.put("postStatus", Constant.POST_COMPLETED_STATUS);
        MyFirebaseDatabaseClass.POSTS_REFERENCE.child(post.getPostId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    expireOtherRequests(post.getPostId());
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
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                }else {
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        if (mListener != null) {
            mListener.onFragmentInteraction(Constant.TITLE_POST_DESCRIPTION);
        }
    }

}
