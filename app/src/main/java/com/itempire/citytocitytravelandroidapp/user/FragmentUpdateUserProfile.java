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
import android.widget.Button;
import android.widget.EditText;
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
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseStorageClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyPrefLocalStorage;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.itempire.citytocitytravelandroidapp.models.UserDeviceToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateUserProfile extends Fragment {

    private Context context;
    private View view;

    private CircleImageView profileImage;
    private EditText userName, userAddress;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale, radioButtonOthers;
    private Button btnUpdateUser;

    private Bundle bundleArgument;

    private Bitmap profileImageBitmap;
    private Uri profileImageUri;

    private static final int GALLERY_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;

    private FirebaseUser user;
    private User oldUser;

    private FragmentInteractionListenerInterface mListener;

    public FragmentUpdateUserProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        bundleArgument = getArguments();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_EDIT_PROFILE);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_update_user_profile, container, false);
            initLayoutWidgets();


            setDefaultUserData();
        }
        return view;
    }

    private void initLayoutWidgets() {

        profileImage = view.findViewById(R.id.profileImage);

        userName = view.findViewById(R.id.userName);
        userAddress = view.findViewById(R.id.userAddress);

        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        radioButtonMale = view.findViewById(R.id.radioButtonMale);
        radioButtonFemale = view.findViewById(R.id.radioButtonFemale);
        radioButtonOthers = view.findViewById(R.id.radioButtonOthers);

        btnUpdateUser = view.findViewById(R.id.btnUpdateUser);

        initProgressDialog();
        setBtnUpdateUser();
        setProfileImageClickListener();
    }

    private void setProfileImageClickListener() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });
    }

    private void setBtnUpdateUser() {
        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFormValid())
                    if (profileImageUri != null) {
                        uploadImageToStorage();
                    } else {
                        updateUserInFirebaseDatabase(buildUserInstance(null));
                    }

            }
        });
    }

    private void pickImageFromGallery() {
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

    private void uploadImageToStorage() {

        progressDialog.show();

        MyFirebaseStorageClass.VEHICLE_IMAGES_REFERENCE.child(profileImageUri.getLastPathSegment() + ".jpg").putFile(profileImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        final Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                updateUserInFirebaseDatabase(buildUserInstance(uri.toString()));

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

    private void updateUserInFirebaseDatabase(User newUser) {

        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(user.getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(context, "Account Updated successfully!", Toast.LENGTH_LONG).show();
                ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

    private User buildUserInstance(String imageUrl) {
        return new User(
                user.getUid(),
                userName.getText().toString(),
                user.getPhoneNumber(),
                userAddress.getText().toString(),
                getSelectedGender(),
                (imageUrl != null) ? imageUrl : oldUser.getUserImageUrl(),
                oldUser.getUserType());
    }

    private boolean isFormValid() {
        boolean result = true;

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

    private void setDefaultUserData() {
        if (bundleArgument != null && bundleArgument.getSerializable(Constant.SHARED_PREF_USER_KEY) != null) {
            try {
                oldUser = (User) bundleArgument.getSerializable(Constant.SHARED_PREF_USER_KEY);
                if (oldUser != null) {
                    if (oldUser.getUserImageUrl() != null)
                        try {
                            Picasso.get().load(oldUser.getUserImageUrl()).placeholder(R.drawable.user_avatar).fit().into(profileImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    userName.setText(oldUser.getUserName());
                    userAddress.setText(oldUser.getUserAddress());

                    switch (oldUser.getUserGender()) {
                        case Constant.USER_GENDER_MALE:
                            radioButtonMale.setChecked(true);
                            break;
                        case Constant.USER_GENDER_FEMALE:
                            radioButtonFemale.setChecked(true);
                            break;
                        case Constant.USER_GENDER_OTHERS:
                            radioButtonOthers.setChecked(true);
                            break;

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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
            mListener.onFragmentInteraction(Constant.TITLE_EDIT_PROFILE);
    }

}
