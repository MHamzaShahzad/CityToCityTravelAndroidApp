package com.itempire.citytocitytravelandroidapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.MainActivity;
import com.itempire.citytocitytravelandroidapp.MainDrawerActivity;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseStorageClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyPrefLocalStorage;
import com.itempire.citytocitytravelandroidapp.controllers.MyServicesControllerClass;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.itempire.citytocitytravelandroidapp.models.UserDeviceToken;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    Context context;

    CircleImageView profileImage;
    EditText userName, userAddress;
    RadioGroup radioGroupGender;
    RadioButton radioButtonMale, radioButtonFemale, radioButtonOthers;
    Button btnSubmitUser;

    Bitmap profileImageBitmap;
    Uri profileImageUri;

    private static final int GALLERY_REQUEST_CODE = 1;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        initLayoutWidgets();


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        btnSubmitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFormValid()) {
                    uploadUserToFBDatabase();
                }

            }
        });


    }

    private void initLayoutWidgets() {

        profileImage = findViewById(R.id.profileImage);

        userName = findViewById(R.id.userName);
        userAddress = findViewById(R.id.userAddress);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioButtonOthers = findViewById(R.id.radioButtonOthers);

        btnSubmitUser = findViewById(R.id.btnSubmitUser);
    }

    private boolean isFormValid() {
        boolean result = true;

        if (profileImageBitmap == null) {
            Toast.makeText(context, "Select your image first!", Toast.LENGTH_LONG).show();
            result = false;
        }

        if (userName.length() == 0) {
            userName.setError("Field is required");
            result = false;
        }

        if (userAddress.length() == 0) {
            userAddress.setError("Field is required");
            result = false;
        }

        if (getSelectedGender() == null) {
            Toast.makeText(context, "Select your gender first!", Toast.LENGTH_LONG).show();
            result = false;
        }


        return result;
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
            profileImageUri = data.getData();
            //------------1
            try {
                profileImageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), profileImageUri);
                profileImage.setImageBitmap(profileImageBitmap);
                Log.e("bitmap", "onActivityResult: " + profileImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //-----------2
            //imageBitmap = (Bitmap) data.getExtras().get("data");

            //------------3
            //imageBitmap = BitmapFactory.decodeFile(selectedImage.getEncodedPath());

        }
    }

    private void uploadUserToFBDatabase() {

        progressDialog.show();

        String vehicleImageId = UUID.randomUUID().toString();

        MyFirebaseStorageClass.VEHICLE_IMAGES_REFERENCE.child(vehicleImageId).putFile(profileImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        final Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    User newUser = new User(
                                            user.getUid(),
                                            userName.getText().toString(),
                                            user.getPhoneNumber(),
                                            userAddress.getText().toString(),
                                            getSelectedGender(),
                                            uri.toString(),
                                            Constant.USER_TYPE_NON_ADMIN);
                                    MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(user.getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            MyFirebaseDatabaseClass.USERS_TOKEN_REFERENCE.child(user.getUid()).setValue(new UserDeviceToken(user.getUid(), new MyPrefLocalStorage(context).getDeviceToken(), null, null));
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_LONG).show();
                                            startMainDrawer();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            MyFirebaseCurrentUserClass.SignOut(context);
                                        }
                                    });
                                }else {
                                    Toast.makeText(context, "Something went wrong , please try again!", Toast.LENGTH_LONG).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                CommonFeaturesClass.showCustomDialog(context, "", e.getMessage());
                                Toast.makeText(context, "Can't upload image, something went wrong , please try again!", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
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

    private String getSelectedGender() {
        int id = radioGroupGender.getCheckedRadioButtonId();
        switch (id) {
            case R.id.radioButtonMale:
                return Constant.USER_GENDER_MALE;
            case R.id.radioButtonFemale:
                return Constant.USER_GENDER_FEMALE;
            case R.id.radioButtonOthers:
                return Constant.USER_GENDER_OTHERS;
            default:
                return null;
        }
    }

    private void startMainDrawer() {
        MyServicesControllerClass.startCustomBackgroundService(context.getApplicationContext());
        startActivity(new Intent(SignUpActivity.this, MainDrawerActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyFirebaseCurrentUserClass.SignOut(context);
    }

}
