package com.example.weatherapp.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.WeatherWidget;
import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.repositries.WeatherInfoRepository;
import com.example.weatherapp.utilities.SunshineWeatherUtils;

public class UpdateNotifyWorker extends Worker {


    public UpdateNotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Fetch data
        WeatherInfoRepository r =  WeatherInfoRepository.getInstance(getApplicationContext());
        r.fetchData();
        WeatherData wd = r.getWeatherDataByForecastType(WeatherData.FORECAST_TYPE_CURRENT);

        // Return failure if data is empty
        if(wd == null) {
            return Result.retry();
        }
        // Get data used in notification
        double temp = wd.getCurrTemp();
        int condition = wd.getWeatherConditionID();
        String location = wd.getLocationName();
        // Get uri data from input
        boolean isNotifying = SunshinePreferences.isNotifying(getApplicationContext());

        // Make notification if user choose to do it so.
        if(isNotifying) {
            makeNotification(temp, condition, location);
        }

        return Result.success(); //TODO provide two other options.
    }

    private void makeNotification(double temp, int weatherCondition, String location) {
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "weather_channel";
        String channelName = "weather_current";


        // Check if device require channels.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        // Values used in notification.
        Context appContext = getApplicationContext();
        String formatTemp = SunshineWeatherUtils
                .formatTemperature(appContext, temp);
        String description = SunshineWeatherUtils
                .getStringForWeatherCondition(appContext, weatherCondition);
        int iconRes = SunshineWeatherUtils
                .getIconResourceForWeatherCondition(weatherCondition);

        // Set up and launch notification.
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), iconRes);
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channelId)
                .setContentIntent(createPendingIntent())
                .setContentTitle(location)
                .setContentText(formatTemp + " " + description)
                .setLargeIcon(icon)
                .setSmallIcon(R.mipmap.ic_launcher);

        //TODO: create resources
        manager.notify(1, builder.build());
    }

    private PendingIntent createPendingIntent() {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return resultPendingIntent;
    }
}
