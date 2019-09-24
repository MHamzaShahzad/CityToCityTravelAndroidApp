package com.itempire.citytocitytravelandroidapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackgroundTasksJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        MyFirebaseCurrentUserClass.initAndSetUserValueEventListener(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public void onDestroy() {
        MyFirebaseCurrentUserClass.removeUserValueEventListener(getApplicationContext());
        super.onDestroy();
    }
}
