package com.example.weatherapp.utilities;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.workers.UpdateNotifyWorker;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

//TODO: class in progress.
public class WorkerUtilities {

    public static WorkRequest getPeriodicWorkRequest(Context context) {
        int time = SunshinePreferences.getPreferredRefreshTime(context);
        if(time == -1){
            return null;
        }
        return new PeriodicWorkRequest
                .Builder(UpdateNotifyWorker.class, time, TimeUnit.HOURS)
                .build();
    }

    public static WorkRequest getPeriodicWorkRequest(Context context, Constraints constraints) {
        long time = SunshinePreferences.getPreferredRefreshTime(context);
        if(time == -1){
            return null;
        }
        return new PeriodicWorkRequest
                .Builder(UpdateNotifyWorker.class, time, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
    }

    public static void cancelRequest(Context context, UUID requestId) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelWorkById(requestId);

    }


}
