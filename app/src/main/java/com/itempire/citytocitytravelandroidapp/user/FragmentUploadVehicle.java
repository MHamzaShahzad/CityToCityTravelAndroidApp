package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseStorageClass;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUploadVehicle extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentUploadVehicle.class.getName();

    Context context;
    View view;

    ImageView vehicle_image, upload_vehicle;
    EditText vehicle_model, vehicle_alphabet, vehicle_digits;

    private static final int GALLERY_REQUEST_CODE = 1;
    private Bitmap imageBitmap;
    private Uri selectedImage;

    ProgressDialog progressDialog;

    private FragmentInteractionListenerInterface mListener;

    public FragmentUploadVehicle() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_UPLOAD_VEHICLE);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_upload_vehicle, container, false);

            initLayoutIds(view);
            initClickListeners();

        }
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == vehicle_image.getId()) {
            pickFromGallery();
        }
        if (id == upload_vehicle.getId()) {

            if (isVehicleFormValid()) {
                uploadVehicleToFB();
            }

        }

    }

    private void initLayoutIds(View view) {
        vehicle_image = view.findViewById(R.id.vehicle_image);
        upload_vehicle = view.findViewById(R.id.upload_vehicle);
        vehicle_model = view.findViewById(R.id.vehicle_model);
        vehicle_alphabet = view.findViewById(R.id.vehicle_alphabet);
        vehicle_digits = view.findViewById(R.id.vehicle_digits);
    }

    private void initClickListeners() {
        vehicle_image.setOnClickListener(this);
        upload_vehicle.setOnClickListener(this);
    }

    private boolean isVehicleFormValid() {

        boolean result = true;

        if (selectedImage == null) {
            Toast.makeText(context, "Please upload vehicle image!", Toast.LENGTH_LONG).show();
            result = false;
        }

        if (vehicle_model.length() == 0) {
            vehicle_model.setError("Field is required!");
            result = false;
        }
        if (vehicle_alphabet.length() == 0) {
            vehicle_alphabet.setError("Field is required!");
            result = false;
        }

        if (vehicle_digits.length() == 0) {
            vehicle_digits.setError("Field is required!");
            result = false;
        }

        if (vehicle_alphabet.length() > 0 && vehicle_alphabet.length() > 3) {
            vehicle_alphabet.setError("Invalid data!");
            result = false;
        }

        if (vehicle_digits.length() > 0 && vehicle_digits.length() > 4) {
            vehicle_digits.setError("Invalid data");
            result = false;
        }

        return result;
    }

    private void getDefaultUserData() {

    }

    private void uploadVehicleToFB() {

        progressDialog.show();

        String vehicleImageId = UUID.randomUUID().toString();

        MyFirebaseStorageClass.VEHICLE_IMAGES_REFERENCE.child(vehicleImageId).putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                MyFirebaseDatabaseClass.VEHICLES_REFERENCE.child(MyFirebaseCurrentUserClass.mUser.getUid()).setValue(new Vehicle(
                                        MyFirebaseCurrentUserClass.mUser.getUid(),
                                        uri.toString(),
                                        vehicle_model.getText().toString(),
                                        vehicle_alphabet.getText().toString() + "-" + vehicle_digits.getText().toString(),
                                        Constant.VEHICLE_STATUS_INACTIVE
                                )).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        CommonFeaturesClass.showCustomDialog(context, "Vehicle Authentication", "Your vehicle registration will be approved by admin shortly, then you will be able to upload your post.");
                                        ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                                        Toast.makeText(context, "Vehicle uploaded successfully!", Toast.LENGTH_LONG).show();
                                        //((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new FragmentCreatePost()).addToBackStack(null).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        CommonFeaturesClass.showCustomDialog(context, "", e.getMessage());
                                        Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Can't upload image, something went wrong , please try again!", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                        CommonFeaturesClass.showCustomDialog(context, "", exception.getMessage());
                        exception.printStackTrace();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });

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
                vehicle_image.setImageBitmap(imageBitmap);
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
            mListener.onFragmentInteraction(Constant.TITLE_UPLOAD_VEHICLE);
    }

}
