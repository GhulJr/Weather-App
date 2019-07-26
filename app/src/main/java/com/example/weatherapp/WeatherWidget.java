package com.example.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.repositries.WeatherInfoRepository;
import com.example.weatherapp.utilities.SunshineDateUtils;
import com.example.weatherapp.utilities.SunshineWeatherUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Implementation of App Widget functionality.
 */
//TODO: make widget scalable.
//TODO: implement OnSharedPreferencesChanged.
//TODO: implement refresh button.
//TODO: update UI when notification fetches data.

public class WeatherWidget extends AppWidgetProvider {
    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        new AsyncTask<Void, Void, RemoteViews>() {

            @Override
            protected RemoteViews doInBackground(Void... voids) {
                // Construct the RemoteViews object
                WeatherData weatherData = WeatherInfoRepository.getInstance(context)
                        .getWeatherDataByForecastType(WeatherData.FORECAST_TYPE_CURRENT);
                CharSequence location = weatherData.getLocationName();
                CharSequence temperature = SunshineWeatherUtils
                        .formatTemperature(context, weatherData.getCurrTemp());
                CharSequence date = SunshineDateUtils
                        .getFriendlyDateString(context, weatherData.getDateInMillis(), false);
                int weatherConditionRes = SunshineWeatherUtils
                        .getIconResourceForWeatherCondition(
                                weatherData.getWeatherConditionID());

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

                // Updating views.
                views.setTextViewText(R.id.appwidget_location, location);
                views.setTextViewText(R.id.appwidget_temp, temperature);
                views.setTextViewCompoundDrawables(
                        R.id.appwidget_temp, weatherConditionRes,0,0,0);
                views.setTextViewText(R.id.appwidget_date, date);

                // Setting up clickable intent.
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

                return views;
            }

            @Override
            protected void onPostExecute(RemoteViews views) {
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }.execute();



    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

