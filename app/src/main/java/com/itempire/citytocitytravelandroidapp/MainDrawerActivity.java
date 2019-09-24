package com.itempire.citytocitytravelandroidapp;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.admin.FragmentAllVehicles;
import com.itempire.citytocitytravelandroidapp.admin.FragmentUsersAndAdmins;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyPrefLocalStorage;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;
import com.itempire.citytocitytravelandroidapp.user.FragmentAllActivePosts;
import com.itempire.citytocitytravelandroidapp.user.FragmentCreatePost;
import com.itempire.citytocitytravelandroidapp.user.FragmentUploadVehicle;
import com.itempire.citytocitytravelandroidapp.user.FragmentUserProfile;
import com.itempire.citytocitytravelandroidapp.user.OffersFragmentComplete;
import com.itempire.citytocitytravelandroidapp.user.MyPostsFragmentComplete;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainDrawerActivity.class.getName();

    private Context context;
    private static DrawerLayout drawer;
    public static ValueEventListener userTypeEventListener;

    public static ImageView userNavHeaderImage;
    public static TextView userNavHeaderName, headerPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        initHeaderWidgets(navigationView.getHeaderView(0));
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentAllActivePosts()).commit();

    }


    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        userTypeEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getValue());
                    switch (dataSnapshot.getValue().toString()) {
                        case Constant.USER_TYPE_ADMIN:
                            getMenuInflater().inflate(R.menu.main_drawer, menu);
                            break;
                        case Constant.USER_TYPE_NON_ADMIN:
                            menu.clear();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).child("userType").addValueEventListener(userTypeEventListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_users_admins) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentUsersAndAdmins()).addToBackStack(null).commit();
            return true;
        }else if (id == R.id.action_vehicles) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentAllVehicles()).addToBackStack(null).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        clearFragmentBackStack();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_my_posts) {

            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new MyPostsFragmentComplete(context)).addToBackStack(null).commit();

        } else if (id == R.id.nav_my_requests) {

            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new OffersFragmentComplete(context)).addToBackStack(null).commit();

        } else if (id == R.id.nav_my_vehicle) {

            MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        try{
                            Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                            if (vehicle != null){
                                FragmentVehicleDescription fragmentVehicleDescription = new FragmentVehicleDescription();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constant.VEHICLE_DESCRIPTION_NAME, vehicle);
                                bundle.putBoolean(Constant.VEHICLE_DESCRIPTION_USER, true);
                                fragmentVehicleDescription.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, fragmentVehicleDescription).addToBackStack(null).commit();

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (id == R.id.nav_create_post) {

            checkIfVehicleAlreadyUploaded();

        } else if (id == R.id.nav_logout) {
            MyFirebaseCurrentUserClass.SignOut(context);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {


        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void initHeaderWidgets(View view){
        if (view != null) {
            userNavHeaderImage = view.findViewById(R.id.headerImageView);
            userNavHeaderName = view.findViewById(R.id.headerUserName);
            headerPhoneNumber = view.findViewById(R.id.headerPhoneNumber);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentUserProfile()).addToBackStack(null).commit();
                }
            });
        }
    }

    public static void updateUserInNavHeader(User user){
        if (user != null){
            if (user.getUserImageUrl() != null)
                try {
                    Picasso.get().load(user.getUserImageUrl()).placeholder(R.drawable.user_avatar).fit().into(userNavHeaderImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            userNavHeaderName.setText(user.getUserName());
            headerPhoneNumber.setText(user.getUserPhoneNumber());
        }
    }

    private void checkIfVehicleAlreadyUploaded() {
        MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentUploadVehicle()).addToBackStack(null).commit();
                } else {
                    try {
                        Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                        if (vehicle != null) {
                            if (vehicle.getVehicleStatus().equals(Constant.VEHICLE_STATUS_ACTIVE))
                                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentCreatePost()).addToBackStack(null).commit();
                            else
                                CommonFeaturesClass.showCustomDialog(context, "Vehicle Authentication", "Vehicle not registered yet, please try later.");
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

    private void clearFragmentBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public static void removeUserValueEventListener() {
        if (userTypeEventListener != null)
            MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).child("userType").removeEventListener(userTypeEventListener);

    }

}
