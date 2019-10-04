package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.squareup.picasso.Picasso;


public class FragmentUserProfile extends Fragment {

    private Context context;
    private View view;

    private ImageView userProfileImage;
    private TextView userName, userAddress, userPhoneNumber;
    private Button btnEditMyProfile;

    private ValueEventListener userProfileEventListener;

    private FragmentInteractionListenerInterface mListener;

    public FragmentUserProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_PROFILE);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_profile, container, false);
            initLayoutWidgets();


            initUserProfile();
        }
        return view;
    }

    private void initLayoutWidgets(){

        userProfileImage = view.findViewById(R.id.userProfileImage);

        userName = view.findViewById(R.id.userName);
        userAddress = view.findViewById(R.id.userAddress);
        userPhoneNumber = view.findViewById(R.id.userPhoneNumber);

        btnEditMyProfile = view.findViewById(R.id.btnEditMyProfile);

    }

    private void initUserProfile(){

         userProfileEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    try {

                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            if (user.getUserImageUrl() != null)
                                try {
                                    Picasso.get().load(user.getUserImageUrl()).placeholder(R.drawable.user_avatar).fit().into(userProfileImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            userName.setText(user.getUserName());
                            userPhoneNumber.setText(user.getUserPhoneNumber());
                            userAddress.setText(user.getUserAddress());

                            setBtnEditMyProfile(user);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).addValueEventListener(userProfileEventListener);

    }


    private void setBtnEditMyProfile(final User user){
        btnEditMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentUpdateUserProfile updateUserProfile = new FragmentUpdateUserProfile();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.SHARED_PREF_USER_KEY, user);
                updateUserProfile.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, updateUserProfile).addToBackStack(null).commit();
            }
        });
    }

    private void removeValueEventListener(){
        if (userProfileEventListener != null)
            MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).removeEventListener(userProfileEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeValueEventListener();
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
            mListener.onFragmentInteraction(Constant.TITLE_PROFILE);
    }

}
