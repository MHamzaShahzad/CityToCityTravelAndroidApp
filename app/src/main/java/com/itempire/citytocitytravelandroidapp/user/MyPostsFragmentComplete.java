package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterCategoriesList;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterMyPosts;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.OffersCategorise;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragmentComplete extends Fragment {

    private static final String TAG = MyPostsFragmentComplete.class.getName();
    Context context;
    View view;
    RecyclerView my_posts_cat_recycler, my_posts_recycler;
    List<OffersCategorise> categoriseList;
    BroadcastReceiver receiverSelectedCategory;
    static ValueEventListener valueEventListener;
    DatabaseReference postsDBRef;

    private FragmentInteractionListenerInterface mListener;

    public MyPostsFragmentComplete(Context context) {
        // Required empty public constructor
        this.context = context;
        categoriseList = new ArrayList<>();
        categoriseList.add(new OffersCategorise(
                Constant.POST_ACTIVE_STATUS,
                "Active"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.POST_COMPLETED_STATUS,
                "Completed"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.POST_EXPIRED_STATUS,
                "Expired"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.POST_BLOCKED_STATUS,
                "Blocked"
        ));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_MY_POSTS);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_posts_fragment_complete, container, false);

            my_posts_cat_recycler = view.findViewById(R.id.my_posts_cat_recycler);
            my_posts_cat_recycler.setHasFixedSize(true);
            my_posts_cat_recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

            my_posts_cat_recycler.setAdapter(new AdapterCategoriesList(context, categoriseList, Constant.ACTION_SELECTED_POST_TYPE));

            my_posts_recycler = view.findViewById(R.id.my_posts_recycler);
            my_posts_recycler.setHasFixedSize(true);
            my_posts_recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, true));

            postsDBRef = MyFirebaseDatabaseClass.POSTS_REFERENCE;


            setReceiverSelectedCategory();
            Intent intent = new Intent(Constant.ACTION_SELECTED_POST_TYPE);
            intent.putExtra(Constant.OFFER_OR_POST_STATUS, Constant.POST_ACTIVE_STATUS);
            context.sendBroadcast(intent);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegisterCustomReceiver();
        removeValueEventListener();

    }

    private void setReceiverSelectedCategory() {
        receiverSelectedCategory = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                removeValueEventListener();
                setValueEventListener(intent.getStringExtra(Constant.OFFER_OR_POST_STATUS));
            }
        };
        context.registerReceiver(receiverSelectedCategory, new IntentFilter(Constant.ACTION_SELECTED_POST_TYPE));
    }

    private void setValueEventListener(final String selectedCat) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Post> myPostsList = new ArrayList<>();

                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : snapshots){
                    if (snapshot != null && snapshot.getValue() != null ){
                        Post post = snapshot.getValue(Post.class);
                        if (post != null && post.getOwnerVehicleId() != null){
                            if (post.getOwnerVehicleId().equals(MyFirebaseCurrentUserClass.mUser.getUid()) && post.getPostStatus().equals(selectedCat)) {
                                Log.e(TAG, "onDataChange: " +

                                        post.getArrivalCity()


                                );

                                myPostsList.add(post);
                            }else {
                                if (post.getOwnerVehicleId().equals(MyFirebaseCurrentUserClass.mUser.getUid()) && !post.getPostStatus().equals(selectedCat))
                                    Log.e(TAG, "onDataChange: NO_POST_FOUND " );
                            }
                        }
                    }
                }

                my_posts_recycler.setAdapter(new AdapterMyPosts(context, myPostsList, selectedCat));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postsDBRef.addValueEventListener(valueEventListener);
    }

    private void removeValueEventListener() {
        if (postsDBRef != null && valueEventListener != null)
            postsDBRef.removeEventListener(valueEventListener);
    }

    private void unRegisterCustomReceiver() {
        if (receiverSelectedCategory != null)
            context.unregisterReceiver(receiverSelectedCategory);
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
            mListener.onFragmentInteraction(Constant.TITLE_MY_POSTS);
    }

}
