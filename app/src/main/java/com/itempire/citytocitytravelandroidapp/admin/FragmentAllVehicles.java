package com.itempire.citytocitytravelandroidapp.admin;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterVehiclesListAdmin;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllVehicles extends Fragment {

    private static final String TAG = FragmentAllVehicles.class.getName();

    Context context;
    View view;
    TabLayout tabLayout;
    RecyclerView recycler_vehicles_list;
    List<Vehicle> list, tempList;
    ValueEventListener valueEventListener;
    AdapterVehiclesListAdmin adapterVehiclesListAdmin;

    public FragmentAllVehicles() {
        // Required empty public constructor
        list = new ArrayList<>();
        tempList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_vehicles, container, false);

            recycler_vehicles_list = view.findViewById(R.id.recycler_vehicles_list);
            recycler_vehicles_list.setHasFixedSize(true);
            recycler_vehicles_list.setLayoutManager(new LinearLayoutManager(context));
            adapterVehiclesListAdmin = new AdapterVehiclesListAdmin(context, tempList);
            recycler_vehicles_list.setAdapter(adapterVehiclesListAdmin);


            initVehiclesListListener();


            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            Toast.makeText(context, "In-Active", Toast.LENGTH_LONG).show();
                            getInActiveVehicles();
                            break;
                        case 1:
                            Toast.makeText(context, "Active", Toast.LENGTH_LONG).show();
                            getActiveVehicles();
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

    private void initVehiclesListListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                Iterable<DataSnapshot> hostelsListSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot singleHostel : hostelsListSnapshot) {

                    try {
                        Vehicle vehicle = singleHostel.getValue(Vehicle.class);
                        if (vehicle != null) {
                            list.add(vehicle);
                            Log.e(TAG, "onDataChange: " + vehicle.getVehicleNumber());
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
        MyFirebaseDatabaseClass.VEHICLES_REFERENCE.addValueEventListener(valueEventListener);
    }

    private void initTabsLayout() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            getInActiveVehicles();
        }
        if (tabLayout.getSelectedTabPosition() == 1) {
            getActiveVehicles();
        }
    }

    private void getInActiveVehicles() {

        tempList.clear();

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVehicleStatus().equals(Constant.VEHICLE_STATUS_INACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterVehiclesListAdmin.notifyDataSetChanged();

    }

    private void getActiveVehicles() {

        tempList.clear();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVehicleStatus().equals(Constant.VEHICLE_STATUS_ACTIVE))
                    tempList.add(list.get(i));
            }

        }
        adapterVehiclesListAdmin.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            MyFirebaseDatabaseClass.VEHICLES_REFERENCE.removeEventListener(valueEventListener);
    }
}
