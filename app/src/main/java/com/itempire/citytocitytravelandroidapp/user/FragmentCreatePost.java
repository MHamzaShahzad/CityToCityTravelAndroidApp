package com.itempire.citytocitytravelandroidapp.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.MainActivity;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseStorageClass;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FragmentCreatePost extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentCreatePost.class.getName();
    private Context context;
    private View view;

    private ImageView image_view_dept_date_calender, image_view_arrival_date_calender, image_view_dept_time_pick, image_view_arrival_time_pick, image_view_dept_location, image_view_arrival_location;

    private TextView create_posts_dept_location, create_posts_arrival_location;

    private EditText create_posts_dept_date, create_posts_dept_time, create_posts_arrival_date,
            create_posts_arrival_time, create_posts_dept_city,
            create_posts_arrival_city, create_posts_total_no_of_seats_available, create_posts_max_no_of_seats_available,
            create_posts_min_no_of_seats_available, create_posts_cost_per_seat;


    private Button btn_submit_post;

    Calendar myCalendar;

    private static Bundle bundle;
    private static BroadcastReceiver broadcastReceiver;
    private String deptLatitude, deptLongitude, arrivalLatitude, arrivalLongitude;

    private FragmentInteractionListenerInterface mListener;

    public FragmentCreatePost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_CREATE_POST);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_create_post, container, false);

            getViewsByIds();
            initClickListeners();
            initDateTimeTexts();
            setLocationsReceiver();
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_submit_post:
                if (isAllFieldsValid()) {
                    Toast.makeText(context, "All fields are valid", Toast.LENGTH_LONG).show();
                    uploadPostToFBDatabase();
                }
                break;
            case R.id.image_view_dept_location:
                FragmentMapGetLocationForPost getDeptLocationForPost = new FragmentMapGetLocationForPost();
                bundle = new Bundle();
                bundle.putBoolean(Constant.DEPT_LOCATION_KEY, true);
                getDeptLocationForPost.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, getDeptLocationForPost).addToBackStack(null).commit();
                break;
            case R.id.image_view_arrival_location:
                FragmentMapGetLocationForPost getArrivalLocationForPost = new FragmentMapGetLocationForPost();
                bundle = new Bundle();
                bundle.putBoolean(Constant.ARRIVAL_LOCATION_KEY, true);
                getArrivalLocationForPost.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, getArrivalLocationForPost).addToBackStack(null).commit();
                break;
            case R.id.image_view_dept_time_pick:
                showSelectTimeDialog();
                break;
        }

    }


    private void getViewsByIds() {

        image_view_dept_date_calender = view.findViewById(R.id.image_view_dept_date_calender);
        image_view_dept_time_pick = view.findViewById(R.id.image_view_dept_time_pick);

        image_view_arrival_date_calender = view.findViewById(R.id.image_view_arrival_date_calender);
        image_view_arrival_time_pick = view.findViewById(R.id.image_view_arrival_time_pick);

        image_view_dept_location = view.findViewById(R.id.image_view_dept_location);
        image_view_arrival_location = view.findViewById(R.id.image_view_arrival_location);

        create_posts_dept_date = view.findViewById(R.id.create_posts_dept_date);
        create_posts_dept_time = view.findViewById(R.id.create_posts_dept_time);

        create_posts_arrival_date = view.findViewById(R.id.create_posts_arrival_date);
        create_posts_arrival_time = view.findViewById(R.id.create_posts_arrival_time);

        create_posts_dept_location = view.findViewById(R.id.create_posts_dept_location);
        create_posts_arrival_location = view.findViewById(R.id.create_posts_arrival_location);
        create_posts_dept_city = view.findViewById(R.id.create_posts_dept_city);
        create_posts_arrival_city = view.findViewById(R.id.create_posts_arrival_city);
        create_posts_total_no_of_seats_available = view.findViewById(R.id.create_posts_total_no_of_seats_available);
        create_posts_max_no_of_seats_available = view.findViewById(R.id.create_posts_max_no_of_seats_available);
        create_posts_min_no_of_seats_available = view.findViewById(R.id.create_posts_min_no_of_seats_available);
        create_posts_cost_per_seat = view.findViewById(R.id.create_posts_cost_per_seat);

        btn_submit_post = view.findViewById(R.id.btn_submit_post);

    }

    private void initClickListeners() {

        btn_submit_post.setOnClickListener(this);
        image_view_arrival_date_calender.setOnClickListener(this);
        image_view_dept_date_calender.setOnClickListener(this);
        image_view_dept_time_pick.setOnClickListener(this);
        image_view_arrival_time_pick.setOnClickListener(this);
        image_view_dept_location.setOnClickListener(this);
        image_view_arrival_location.setOnClickListener(this);

    }

    private boolean isAllFieldsValid() {
        boolean result = true;

        if (create_posts_dept_date.length() == 0) {
            create_posts_dept_date.setError("Field is required!");
            result = false;
        }

        if (create_posts_dept_time.length() == 0) {
            create_posts_dept_time.setError("Field is required!");
            result = false;
        }

        /*if (create_posts_arrival_date.length() == 0) {
            create_posts_arrival_date.setError("Field is required!");
            result = false;
        }

        if (create_posts_arrival_time.length() == 0) {
            create_posts_arrival_time.setError("Field is required!");
            result = false;
        }*/

        if (create_posts_dept_location.length() == 0) {
            create_posts_dept_location.setError("Field is required!");
            result = false;
        }

        if (create_posts_arrival_location.length() == 0) {
            create_posts_arrival_location.setError("Field is required!");
            result = false;
        }

        if (create_posts_dept_city.length() == 0) {
            create_posts_dept_city.setError("Field is required!");
            result = false;
        }

        if (create_posts_arrival_city.length() == 0) {
            create_posts_arrival_city.setError("Field is required!");
            result = false;
        }

        if (create_posts_total_no_of_seats_available.length() == 0) {
            create_posts_total_no_of_seats_available.setError("Field is required!");
            result = false;
        }

        if (create_posts_max_no_of_seats_available.length() == 0) {
            create_posts_max_no_of_seats_available.setError("Field is required!");
            result = false;
        }

        if (create_posts_min_no_of_seats_available.length() == 0) {
            create_posts_min_no_of_seats_available.setError("Field is required!");
            result = false;
        }

        if (create_posts_cost_per_seat.length() == 0) {
            create_posts_cost_per_seat.setError("Field is required!");
            result = false;
        }


        return result;
    }

    private void showSelectTimeDialog() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String amPm;
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        create_posts_dept_time.setText((hourOfDay - 12) + ":" + minute + " " + amPm);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void uploadPostToFBDatabase() {

        String postId = UUID.randomUUID().toString();
        Date date = new Date();

        MyFirebaseDatabaseClass.POSTS_REFERENCE.child(postId).setValue(
                new Post(
                        postId,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        create_posts_dept_date.getText().toString(),
                        create_posts_dept_time.getText().toString(),
                        create_posts_dept_location.getText().toString(),
                        create_posts_arrival_location.getText().toString(),
                        create_posts_dept_city.getText().toString(),
                        create_posts_arrival_city.getText().toString(),
                        deptLatitude,
                        deptLongitude,
                        arrivalLatitude,
                        arrivalLongitude,
                        create_posts_total_no_of_seats_available.getText().toString(),
                        create_posts_max_no_of_seats_available.getText().toString(),
                        create_posts_min_no_of_seats_available.getText().toString(),
                        date.toLocaleString(),
                        Constant.POST_ACTIVE_STATUS,
                        create_posts_cost_per_seat.getText().toString()
                )

        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Post uploaded on database", Toast.LENGTH_LONG).show();
                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Can't upload post data, something went wrong , please try again!", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void initDateTimeTexts() {
        myCalendar = Calendar.getInstance();
        //String date = new SimpleDateFormat("yyyy-MM-dd:hh-mm", Locale.getDefault()).format(new Date());
        textDateFromListener();
        textDateToListener();
    }

    public void textDateFromListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom();
            }

        };


        image_view_dept_date_calender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void textDateToListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelTo();
            }

        };


        image_view_arrival_date_calender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelFrom() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        create_posts_dept_date.setText(sdf.format(myCalendar.getTime()));
        create_posts_dept_date.requestFocus();
        create_posts_dept_date.setFocusable(true);
        create_posts_dept_date.setSelection(create_posts_dept_date.getText().toString().length());
    }

    private void updateLabelTo() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        create_posts_arrival_date.setText(sdf.format(myCalendar.getTime()));
        create_posts_arrival_date.requestFocus();
        create_posts_arrival_date.setFocusable(true);
        create_posts_arrival_date.setSelection(create_posts_arrival_date.getText().toString().length());
    }

    private void setLocationsReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(Constant.DEPT_LOCATION_KEY, false)) {
                    create_posts_dept_location.setText(intent.getStringExtra(Constant.LOCATION_ADDRESS));
                    create_posts_dept_city.setText(intent.getStringExtra(Constant.LOCATION_ADDRESS_CITY));
                    deptLatitude = intent.getStringExtra(Constant.LOCATION_LATITUDE);
                    deptLongitude = intent.getStringExtra(Constant.LOCATION_LONGITUDE);

                }
                if (intent.getBooleanExtra(Constant.ARRIVAL_LOCATION_KEY, false)) {
                    create_posts_arrival_location.setText(intent.getStringExtra(Constant.LOCATION_ADDRESS));
                    create_posts_arrival_city.setText(intent.getStringExtra(Constant.LOCATION_ADDRESS_CITY));
                    arrivalLatitude = intent.getStringExtra(Constant.LOCATION_LATITUDE);
                    arrivalLongitude = intent.getStringExtra(Constant.LOCATION_LONGITUDE);
                }

                Log.e(TAG, "onReceive: DEPT_DATA \n" + intent.getStringExtra(Constant.LOCATION_ADDRESS)
                        + "\n" + intent.getStringExtra(Constant.LOCATION_ADDRESS_CITY)
                        + "\n" + intent.getStringExtra(Constant.LOCATION_LATITUDE)
                        + "\n" + intent.getStringExtra(Constant.LOCATION_LONGITUDE));

            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(Constant.LOCATION_RECEIVING_FILTER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null)
            context.unregisterReceiver(broadcastReceiver);
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
            mListener.onFragmentInteraction(Constant.TITLE_CREATE_POST);
    }

}
