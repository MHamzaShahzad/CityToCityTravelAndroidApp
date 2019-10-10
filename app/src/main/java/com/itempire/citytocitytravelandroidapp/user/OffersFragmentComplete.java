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
import com.itempire.citytocitytravelandroidapp.adapters.AdapterMyRequests;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterCategoriesList;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.OffersCategorise;

import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class OffersFragmentComplete extends Fragment {

    private static final String TAG = OffersFragmentComplete.class.getName();
    Context context;
    View view;
    RecyclerView offers_categories_recycler, offers_recycler;
    List<OffersCategorise> categoriseList;
    BroadcastReceiver receiverSelectedCategory;
    static ValueEventListener valueEventListener;
    DatabaseReference postOfferDBRef;

    private FragmentInteractionListenerInterface mListener;

    public OffersFragmentComplete(Context context) {
        // Required empty public constructor
        this.context = context;
        categoriseList = new ArrayList<>();
        categoriseList.add(new OffersCategorise(
                Constant.OFFER_PENDING_STATUS,
                "Pending"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.OFFER_ACCEPTED_STATUS,
                "Accepted"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.OFFER_REJECTED_STATUS,
                "Rejected"
        ));
        categoriseList.add(new OffersCategorise(
                Constant.OFFER_EXPIRED_STATUS,
                "Expired"
        ));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_MY_REQUESTS);
        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_offers_fragment_complete, container, false);

            offers_categories_recycler = view.findViewById(R.id.offers_categories_recycler);
            offers_categories_recycler.setHasFixedSize(true);
            offers_categories_recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            offers_categories_recycler.setAdapter(new AdapterCategoriesList(context, categoriseList, Constant.ACTION_SELECTED_OFFER));

            offers_recycler = view.findViewById(R.id.offers_recycler);
            offers_recycler.setHasFixedSize(true);
            offers_recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, true));

            postOfferDBRef =  MyFirebaseDatabaseClass.POSTS_OFFERS_REFERENCE;
            setReceiverSelectedCategory();

            Intent intent = new Intent(Constant.ACTION_SELECTED_OFFER);
            intent.putExtra(Constant.OFFER_OR_POST_STATUS, Constant.OFFER_PENDING_STATUS);
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
        context.registerReceiver(receiverSelectedCategory, new IntentFilter(Constant.ACTION_SELECTED_OFFER));
    }

    private void setValueEventListener(final String selectedCat) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<AvailOffer> list = new ArrayList<>();

                Iterable<DataSnapshot> listSnapshotsOuter = dataSnapshot.getChildren();

                for (DataSnapshot snapshotOuter : listSnapshotsOuter) {

                    Iterable<DataSnapshot> listSnapshotsInner = snapshotOuter.getChildren();

                    for (DataSnapshot snapshotInner : listSnapshotsInner) {

                        if (snapshotInner.getKey() != null && snapshotInner.getKey().equals(MyFirebaseCurrentUserClass.mUser.getUid())) {

                            AvailOffer offer = snapshotInner.getValue(AvailOffer.class);

                            if (offer != null && offer.getOfferStatus().equals(selectedCat)) {

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
                        }
                    }
                    offers_recycler.setAdapter(new AdapterMyRequests(context, list, selectedCat));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postOfferDBRef.addValueEventListener(valueEventListener);
    }

    private void removeValueEventListener() {
        if (postOfferDBRef != null && valueEventListener != null)
            postOfferDBRef.removeEventListener(valueEventListener);
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
            mListener.onFragmentInteraction(Constant.TITLE_MY_REQUESTS);
    }

}
