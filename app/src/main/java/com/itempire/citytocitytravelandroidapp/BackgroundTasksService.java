package com.itempire.citytocitytravelandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;

public class BackgroundTasksService extends Service {
    public BackgroundTasksService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyFirebaseCurrentUserClass.initAndSetUserValueEventListener(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyFirebaseCurrentUserClass.removeUserValueEventListener(getApplicationContext());
    }
}
