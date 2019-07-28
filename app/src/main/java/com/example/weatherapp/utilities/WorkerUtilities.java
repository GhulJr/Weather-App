package com.example.weatherapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Data;

import com.example.weatherapp.R;
import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.workers.UpdateNotifyWorker;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//TODO: class in progress.
public class WorkerUtilities {

    public static WorkRequest getPeriodicWorkRequest(Context context) {
        int time = SunshinePreferences.getPreferredRefreshTime(context);
        // Return null if user.
        if(time == -1){
            return null;
        }
        return new PeriodicWorkRequest
                .Builder(UpdateNotifyWorker.class, time, TimeUnit.HOURS)
                .build();
    }

    public static WorkRequest getPeriodicWorkRequest(Context context, Constraints constraints) {
        long time = SunshinePreferences.getPreferredRefreshTime(context);
        // Return null if user.
        if(time == -1){
            return null;
        }
        return new PeriodicWorkRequest
                .Builder(UpdateNotifyWorker.class, time, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(time, TimeUnit.HOURS)
                .build();
    }

    public static void cancelRequest(Context context, UUID requestId) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelWorkById(requestId);

    }

    /** Create input data for worker. */
    private static Data createInputDateUri(Context context) {
        Data.Builder builder = new Data.Builder();
        boolean isNotifying = SunshinePreferences.isNotifying(context);
        builder.putBoolean(String.valueOf(R.string.notifications_key), isNotifying);

        return builder.build();
    }


}
