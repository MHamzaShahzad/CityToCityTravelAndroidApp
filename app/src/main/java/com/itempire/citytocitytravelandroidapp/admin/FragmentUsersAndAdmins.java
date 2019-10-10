package com.itempire.citytocitytravelandroidapp.admin;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterUsersAndAdmins;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUsersAndAdmins extends Fragment {

    private static final String TAG = FragmentUsersAndAdmins.class.getName();

    View view;
    Context context;

    RecyclerView recycler_admins_users_list;
    AdapterUsersAndAdmins adapterUsersAndAdmins;
    TabLayout tabLayout;
    List<User> list, tempList;
    ValueEventListener valueEventListener;

    private FragmentInteractionListenerInterface mListener;

    public FragmentUsersAndAdmins() {
        // Required empty public constructor
        list = new ArrayList<>();
        tempList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_USERS_AND_ADMINS);

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_users_and_admins, container, false);

            recycler_admins_users_list = view.findViewById(R.id.recycler_admins_users_list);
            recycler_admins_users_list.setHasFixedSize(true);
            recycler_admins_users_list.setLayoutManager(new LinearLayoutManager(context));
            adapterUsersAndAdmins = new AdapterUsersAndAdmins(context, tempList);
            recycler_admins_users_list.setAdapter(adapterUsersAndAdmins);

            initAdminsUsersListListener();

            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            Toast.makeText(context, "Users", Toast.LENGTH_LONG).show();
                            getUsers();
                            break;
                        case 1:
                            Toast.makeText(context, "Admins", Toast.LENGTH_LONG).show();
                            getAdmins();
                            break;
                        default:
                            Toast.makeText(context, "unKnown", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
        return view;
    }

    private void initAdminsUsersListListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> usersListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleUser : usersListSnapshot) {

                    try {
                        User user = singleUser.getValue(User.class);
                        if (user != null) {
                            list.add(user);
                            Log.e(TAG, "onDataChange: " + user.getUserName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                initTabsLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.addValueEventListener(valueEventListener);
    }

    private void initTabsLayout() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            getUsers();
        }
        if (tabLayout.getSelectedTabPosition() == 1) {
            getAdmins();
        }
    }

    private void getUsers() {

        tempList.clear();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUserType() != null && list.get(i).getUserType().equals(Constant.USER_TYPE_NON_ADMIN))
                    tempList.add(list.get(i));
            }

        }
        adapterUsersAndAdmins.notifyDataSetChanged();

    }

    private void getAdmins() {

        tempList.clear();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUserType() != null && list.get(i).getUserType().equals(Constant.USER_TYPE_ADMIN))
                    tempList.add(list.get(i));
            }

        }
        adapterUsersAndAdmins.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.removeEventListener(valueEventListener);
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
            mListener.onFragmentInteraction(Constant.TITLE_USERS_AND_ADMINS);
        }
    }

}
