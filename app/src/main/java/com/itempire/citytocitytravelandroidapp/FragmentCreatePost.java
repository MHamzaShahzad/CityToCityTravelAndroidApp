package com.itempire.citytocitytravelandroidapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseStorageClass;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass.GetCurrentFirebaseUser;

public class FragmentCreatePost extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentCreatePost.class.getName();
    private static final int GALLERY_REQUEST_CODE = 1;
    private Context context;
    private View view;
    private Bitmap imageBitmap;
    private Uri selectedImage;

    private ImageView create_posts_image, image_view_dept_time_calander, image_view_arrival_time_calander, image_view_dept_location, image_view_arrival_location;

    public static EditText create_posts_name, create_posts_phone, create_posts_vehicle_model,
            create_posts_vehicle_number, create_posts_dept_date_time, create_posts_arrival_date_time,
            create_posts_dept_location, create_posts_arrival_location, create_posts_dept_city,
            create_posts_arrival_city, create_posts_total_no_of_seats_available, create_posts_max_no_of_seats_available,
            create_posts_min_no_of_seats_available, create_posts_cost_per_seat;

    private Button btn_submit_post;

    Calendar myCalendar;

    public FragmentCreatePost(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_fragment_create_post, container, false);

            getViewsByIds();
            initClickListeners();
            initDateTimeTexts();
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_submit_post:
                if (isAllFieldsValid()) {
                    Toast.makeText(context, "All fields are valid", Toast.LENGTH_LONG).show();
                    uploadPostImageToFBStorage();
                }
                break;
            case R.id.image_view_dept_location:
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentMapGetLocationForPost(context)).addToBackStack(null).commit();
                break;
            case R.id.image_view_arrival_location:
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentMapGetLocationForPost(context)).addToBackStack(null).commit();
                break;
            case R.id.create_posts_image:
                pickFromGallery();
                break;
        }

    }

    private void uploadPostToFBDatabase(Date date, String imageUrl) {

        new MyFirebaseDatabaseClass().getUsersPostsDBReference().child(GetCurrentFirebaseUser().getUid()+date.toLocaleString()).setValue(
                new Post(create_posts_vehicle_model.getText().toString(),
                        create_posts_vehicle_number.getText().toString(),
                        imageUrl,
                        create_posts_dept_date_time.getText().toString(),
                        create_posts_arrival_date_time.getText().toString(),
                        create_posts_dept_location.getText().toString(),
                        create_posts_arrival_location.getText().toString(),
                        create_posts_dept_city.getText().toString(),
                        create_posts_arrival_city.getText().toString(),
                        create_posts_total_no_of_seats_available.getText().toString(),
                        create_posts_max_no_of_seats_available.getText().toString(),
                        create_posts_min_no_of_seats_available.getText().toString(),
                        date.toLocaleString(),
                        Constant.POST_ACTIVE_STATUS,
                        create_posts_cost_per_seat.getText().toString(),
                        GetCurrentFirebaseUser().getUid())

        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Post uploaded on database", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Can't upload post data, something went wrong , please try again!",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void uploadPostImageToFBStorage(){
        final Date date = new Date();

        new MyFirebaseStorageClass().getPostsImagesReference().child(GetCurrentFirebaseUser().getUid()+date.toLocaleString()).putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadPostToFBDatabase(date, uri.toString());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context,"Can't upload image, something went wrong , please try again!",Toast.LENGTH_LONG).show();
                            }
                        });

                        /*if (downloadUrl.isSuccessful())
                        else
                            Toast.makeText(context,"Something went wrong , please try again!",Toast.LENGTH_LONG).show();
                    */}
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        exception.printStackTrace();
                    }
                });

    }

    private void getViewsByIds() {

        create_posts_image = view.findViewById(R.id.create_posts_image);
        image_view_dept_time_calander = view.findViewById(R.id.image_view_dept_time_calander);
        image_view_arrival_time_calander = view.findViewById(R.id.image_view_arrival_time_calander);
        image_view_dept_location = view.findViewById(R.id.image_view_dept_location);
        image_view_arrival_location = view.findViewById(R.id.image_view_arrival_location);

        create_posts_name = view.findViewById(R.id.create_posts_name);
        create_posts_phone = view.findViewById(R.id.create_posts_phone);
        create_posts_vehicle_model = view.findViewById(R.id.create_posts_vehicle_model);
        create_posts_vehicle_number = view.findViewById(R.id.create_posts_vehicle_number);
        create_posts_dept_date_time = view.findViewById(R.id.create_posts_dept_date_time);
        create_posts_arrival_date_time = view.findViewById(R.id.create_posts_arrival_date_time);
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
        image_view_arrival_time_calander.setOnClickListener(this);
        image_view_dept_time_calander.setOnClickListener(this);
        image_view_dept_location.setOnClickListener(this);
        image_view_arrival_location.setOnClickListener(this);
        create_posts_image.setOnClickListener(this);

    }

    private void initDateTimeTexts() {
        myCalendar = Calendar.getInstance();
        //String date = new SimpleDateFormat("yyyy-MM-dd:hh-mm", Locale.getDefault()).format(new Date());
        textTransactionDateFromListener();
        textTransactionDateToListener();
    }

    private boolean isAllFieldsValid() {
        boolean result = true;
        if (create_posts_name.length() == 0) {
            create_posts_name.setError("Field is required!");
            result = false;
        }

        if (create_posts_phone.length() == 0) {
            create_posts_phone.setError("Field is required!");
            result = false;
        }

        if (create_posts_phone.length() > 0 && create_posts_phone.length() != 11) {
            create_posts_phone.setError("Invalid Phone Number!");
            result = false;
        }

        if (create_posts_vehicle_model.length() == 0) {
            create_posts_vehicle_model.setError("Field is required!");
            result = false;
        }
        if (create_posts_vehicle_number.length() == 0) {
            create_posts_vehicle_number.setError("Field is required!");
            result = false;
        }

        if (create_posts_dept_date_time.length() == 0) {
            create_posts_dept_date_time.setError("Field is required!");
            result = false;
        }

        if (create_posts_arrival_date_time.length() == 0) {
            create_posts_arrival_date_time.setError("Field is required!");
            result = false;
        }

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

        if (imageBitmap == null){
            Toast.makeText(context, "Select an image", Toast.LENGTH_LONG).show();
            result = false;
        }


        return result;
    }

    public void textTransactionDateFromListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom();
            }

        };


        image_view_dept_time_calander.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void textTransactionDateToListener() {
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


        image_view_arrival_time_calander.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelFrom() {
        String myFormat = "yyyy-MM-dd:hh-mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        create_posts_cost_per_seat.requestFocus();
        create_posts_dept_date_time.setText(sdf.format(myCalendar.getTime()));
        create_posts_dept_date_time.setSelection(create_posts_dept_date_time.getText().length());
    }

    private void updateLabelTo() {
        String myFormat = "yyyy-MM-dd:hh-mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        create_posts_cost_per_seat.requestFocus();
        create_posts_arrival_date_time.setText(sdf.format(myCalendar.getTime()));
        create_posts_arrival_date_time.setSelection(create_posts_arrival_date_time.getText().length());
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            //------------1
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                create_posts_image.setImageBitmap(imageBitmap);
                Log.e("bitmap", "onActivityResult: " + imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //-----------2
            //imageBitmap = (Bitmap) data.getExtras().get("data");

            //------------3
            //imageBitmap = BitmapFactory.decodeFile(selectedImage.getEncodedPath());

        }
    }

}
