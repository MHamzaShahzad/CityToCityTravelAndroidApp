package com.itempire.citytocitytravelandroidapp.user;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FragmentMapGetLocationForPost extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private static final String TAG = FragmentMapGetLocationForPost.class.getName();
    Context context;
    View view;
    TextView text_location_address;
    ImageView btn_submit_location;
    RelativeLayout locationAddressLayout;

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private LatLng latLng;
    private static MarkerOptions markerOptions;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private boolean shouldMoveToCurrentLocation = true;

    private String cityName;

    private FragmentInteractionListenerInterface mListener;

    public FragmentMapGetLocationForPost() {
        // Required empty public constructor
        mLocationRequest = new LocationRequest();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_SELECT_LOCATION);
        // Inflate the layout for this fragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_map_get_location_for_post, container, false);

            locationAddressLayout = (RelativeLayout) view.findViewById(R.id.locationAddressLayout);
            text_location_address = (TextView) view.findViewById(R.id.text_location_address);
            btn_submit_location = (ImageView) view.findViewById(R.id.btn_submit_location);


            if (getArguments() != null && getArguments().getBoolean(Constant.VIEW_ON_MAP) && getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) != null) {
                locationAddressLayout.setVisibility(View.INVISIBLE);
            }

            initMap();
            initClickListeners();

        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: ");
        mMap = googleMap;

        if (getArguments() != null && getArguments().getBoolean(Constant.VIEW_ON_MAP) && getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION) != null) {
            locationAddressLayout.setVisibility(View.INVISIBLE);
            try {
                Post post = (Post) getArguments().getSerializable(Constant.POST_OBJECT_DESCRIPTION);
                if (post != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(post.getDepartureLatitude()), Double.valueOf(post.getDepartureLongitude()))).title(post.getDepartureCity()).snippet(post.getDepartureLocation())).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(post.getArrivalLatitude()), Double.valueOf(post.getArrivalLongitude()))).title(post.getArrivalCity()).snippet(post.getArrivalLocation())).showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setGoogleClientForMap();

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(context, "Could not get Location", Toast.LENGTH_SHORT).show();
        } else {
            /*markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your current Location!");
            mMap.addMarker(markerOptions);*/
            float zoomLevel = 16.0f; //This goes up to 21
            if (shouldMoveToCurrentLocation) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                shouldMoveToCurrentLocation = false;
                text_location_address.setText(getCompleteAddressString(context, latLng.latitude, latLng.longitude));

            }
            /*mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(100)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(1f)
                    .fillColor(0x550000FF));*/
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onConnected : Permission not granted!");
            //Permission not granted by user so cancel the further execution.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, FragmentMapGetLocationForPost.this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_location_address:
                setSelect_location();
                break;
            case R.id.btn_submit_location:
                if (getArguments() != null) {
                    Intent intent = new Intent(Constant.LOCATION_RECEIVING_FILTER);
                    if (getArguments().getBoolean(Constant.DEPT_LOCATION_KEY)) {
                        intent.putExtra(Constant.DEPT_LOCATION_KEY, true);
                    }
                    if (getArguments().getBoolean(Constant.ARRIVAL_LOCATION_KEY)) {
                        intent.putExtra(Constant.ARRIVAL_LOCATION_KEY, true);
                    }
                    intent.putExtra(Constant.LOCATION_ADDRESS_CITY, cityName);
                    intent.putExtra(Constant.LOCATION_ADDRESS, text_location_address.getText().toString().trim());
                    intent.putExtra(Constant.LOCATION_LATITUDE, String.valueOf(latLng.latitude));
                    intent.putExtra(Constant.LOCATION_LONGITUDE, String.valueOf(latLng.longitude));
                    context.sendBroadcast(intent);
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                    break;
                }
        }
    }


    private void initClickListeners() {
        text_location_address.setOnClickListener(this);
        btn_submit_location.setOnClickListener(this);
    }

    private void initMap() {
        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void setGoogleClientForMap() {
        mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();
    }

    private void setSelect_location() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build((Activity) context), PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                cityName = returnedAddress.getLocality();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("@LocationAddress", "My Current loction address" + strReturnedAddress.toString());
            } else {
                Log.e("@AddressNotFound", "My Current loction address No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@ErrinInAAddress", "My Current loction address Canont get Address!");
        }
        return strAdd;
    }

    private void removeLocationUpdates() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
            mLocationClient.disconnect();
        }
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
        removeLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_SELECT_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, context);
                if (place != null) {

                    mMap.clear();

                    if (place.getLatLng() != null) {

                        Log.e(TAG, "onActivityResult: " + place.getLatLng());

                        latLng = place.getLatLng();
                        getCompleteAddressString(context, place.getLatLng().latitude, place.getLatLng().longitude);
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f));

                    }
                    text_location_address.setText(place.getName() + "-" + place.getAddress());
                }

            }
        }
    } // onActivityResult Ended...

}
