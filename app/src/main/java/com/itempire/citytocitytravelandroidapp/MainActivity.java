package com.itempire.citytocitytravelandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyPrefLocalStorage;
import com.itempire.citytocitytravelandroidapp.controllers.MyServicesControllerClass;
import com.itempire.citytocitytravelandroidapp.user.SignUpActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private Context context;

    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startMainDrawer();
        }

        findViewById(R.id.btn_login_phone).setOnClickListener(this);

    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initProviders() {
        // Choose authentication providers

        providers = Collections.singletonList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        /*providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );*/


    }

    private void showSignInOptions() {

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.MyTheme)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "onActivityResult: VALID_REQUEST_CODE");
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.e(TAG, "onActivityResult: VALID_RESULT_CODE");
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            MyFirebaseCurrentUserClass.initFirebaseUser();
                            if (dataSnapshot.getValue() == null) {

                                startSignUpActivity();


                            }else {

                                HashMap<String, Object> userTokensMap = new HashMap<>();
                                userTokensMap.put("userId", user.getUid());
                                userTokensMap.put("deviceFBToken", new MyPrefLocalStorage(context).getDeviceToken());
                                MyFirebaseDatabaseClass.USERS_TOKEN_REFERENCE.child(user.getUid()).updateChildren(userTokensMap);

                                Toast.makeText(context, "LoggedIn Successfully!",Toast.LENGTH_LONG).show();
                                startMainDrawer();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response != null && response.getError() != null)
                    Log.e(TAG, "onActivityResult: Error" + response.getError().getErrorCode());
                //showSignInOptions();
            }
        }
    }

    private void startMainDrawer() {
        MyServicesControllerClass.startCustomBackgroundService(context.getApplicationContext());
        startActivity(new Intent(MainActivity.this, MainDrawerActivity.class));
        finish();
    }

    private void startSignUpActivity() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login_phone) {
            initProviders();
            showSignInOptions();
        }
    }

}
