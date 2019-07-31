/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.weatherapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.settings.SettingsActivity;
import com.example.weatherapp.utilities.SunshineDateUtils;
import com.example.weatherapp.utilities.SunshineWeatherUtils;
import com.example.weatherapp.utilities.WorkerUtilities;
import com.example.weatherapp.view_models.WeatherForecastViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private WeatherForecastViewModel viewModel;
    private WorkManager workManager;
    private PeriodicWorkRequest workRequest;

    /** onCreate method. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup vg = findViewById(R.id.hourly_forecast_layout);

        // TODO: find out why it was present
        // Hide home button.
        setUpActionBar();

        // Set up all values related to shared preferences.
        setUpSharedPreferences();

        // Set up recycler view for hourly forecast information.
        //setUpHourlyRecyclerView();

        // Set up view model.
        setUpViewModel();

        // Set up workers if it is first launch.
        if(SunshinePreferences.isWorkerSetFirstTime(getApplicationContext())){
            setUpWorkers();
        }
    }

    /** onDestroy method. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister shared preferences from activity
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /** Inflate menu when created. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    /** Set an actions, that will be taken when item is clicked. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:{
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.reload: {
                viewModel.loadData();
            }
            default: {
                return false;
            }
        }
        return true;
    }

    /** Update UI when preferences are changed. */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.location_key))) {
            // Load new data if location has been changed.
            viewModel.loadData();
        } else if(key.equals(getString(R.string.unit_key))) {
            // TODO: temporary solution.
            // Update daily weather unit.
            List<WeatherData> wd = (List<WeatherData>) viewModel.getData().getValue();
            if(wd != null){
                inflateDailyWeatherLayout(wd);
            } else {
                Log.e(TAG, "Unable to change units.");
            }

            // Update widget units.
            updateWidgets();

        } else if(key.equals(getString(R.string.refresh_key))) {
            if(workRequest != null){
                WorkerUtilities.cancelRequest(getApplicationContext(), workRequest.getId());
            }
            setUpWorkers();
        }
    }


    /** Get all the values from shared preferences to set it up. */
    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setUpActionBar() {
        // Hide return action button.
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
    }

    private void setUpViewModel() {
        // Create ViewModel.
        viewModel = ViewModelProviders
                .of(this)
                .get(WeatherForecastViewModel.class);

        // Set observer to LiveData.
        viewModel.getData().observe(this, new Observer<List<WeatherData>>() {
            @Override
            public void onChanged(@Nullable List<WeatherData> weatherData) {
                //TODO: provide information about no data
                inflateDailyWeatherLayout(weatherData);
                inflateForecastLayouts(weatherData);
                updateWidgets();
            }
        });
    }

    private void setUpWorkers() {
        workManager = WorkManager.getInstance(getApplicationContext());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        workRequest = (PeriodicWorkRequest) WorkerUtilities
                .getPeriodicWorkRequest(getApplicationContext(), constraints);
        if(workRequest != null) {
            workManager.enqueue(workRequest);
        }
    }

    private void inflateDailyWeatherLayout(@NonNull List<WeatherData> weatherData) {

        if(weatherData.isEmpty()) {
            return;
        }
        /* Get necessary views. */
        TextView weatherLocationView = findViewById(R.id.weather_location);
        TextView weatherDateView = findViewById(R.id.weather_date);
        TextView currTempView = findViewById(R.id.weather_temp);
        TextView minMaxTempView = findViewById(R.id.max_min_temp);
        TextView descriptionView = findViewById(R.id.weather_description);

        /*Get daily weather data object*/
        WeatherData currWeatherData = null;
        for(WeatherData wd : weatherData) {
            if(wd.getForecastType() == WeatherData.FORECAST_TYPE_CURRENT) {
                currWeatherData = wd;
                break;
            }
        }

        if(currWeatherData == null) {
            Log.e(TAG, "Unable to fetch current weather data from database!");
            return;
        }

        /* Get and format values. */

        // Used values.
        double high = currWeatherData.getMaxTemp();
        double low = currWeatherData.getMinTemp();


        // Location.
        String location = currWeatherData.getLocationName();

        // Date.
        String date = SunshineDateUtils.getFriendlyDateString(
                getApplicationContext(),
                currWeatherData.getDateInMillis(),
                true);

        // Current temperature.
        String currTemp = SunshineWeatherUtils
                .formatTemperature(
                        getApplicationContext(),
                        currWeatherData.getCurrTemp());

        // Icon.
        int weatherConditionRes = SunshineWeatherUtils
                .getIconResourceForWeatherCondition(
                        currWeatherData.getWeatherConditionID());

        // High Lows
        String minMaxTemp = SunshineWeatherUtils
                .formatHighLows(getApplicationContext(), high, low);

        // Description.
        String description = SunshineWeatherUtils
                .getStringForWeatherCondition(
                        getApplicationContext(),
                        currWeatherData.getWeatherConditionID());

        /* Setting views. */
        weatherLocationView.setText(location);
        weatherDateView.setText(date);
        currTempView.setText(currTemp);
        currTempView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                weatherConditionRes, 0, 0, 0);
        minMaxTempView.setText(minMaxTemp);
        descriptionView.setText(description);

    }

    private void inflateForecastLayouts(@NonNull List<WeatherData> weatherData) {
        if(weatherData.isEmpty()) {
            return;
        }
        List<WeatherData> forecastWeatherData = new ArrayList<>();
        for(WeatherData wd : weatherData) {
            if(wd.getForecastType() == WeatherData.FORECAST_TYPE_HOURLY) {
                forecastWeatherData.add(wd);
            }
        }
        ViewGroup hourlyVg = findViewById(R.id.hourly_forecast_layout);
        inflateForecastLayout(hourlyVg, forecastWeatherData);
    }

    //TODO: chane it's name
    private void inflateForecastLayout(@NonNull ViewGroup viewGroup,@NonNull List<WeatherData> weatherData) {

        ViewGroup linearLayout = (ViewGroup) viewGroup.getChildAt(0);

        int length = linearLayout.getChildCount();

        for(int i = 0; i < length; ++i) {
            // Get data.
            ViewGroup item = (ViewGroup) ((ViewGroup) linearLayout.getChildAt(i)).getChildAt(0);
            WeatherData wd = weatherData.get(i);

            // Get views.
            TextView timeView = (TextView) item.getChildAt(0);
            ImageView imageView = (ImageView) item.getChildAt(1);
            TextView temperatureView = (TextView) item.getChildAt(2);

            // Get values.
            String time =  (String) android.text.format.DateFormat
                    .format("H:mm", wd.getDateInMillis());

            int conditionId = wd.getWeatherConditionID();
            int imageRes = SunshineWeatherUtils.getIconResourceForWeatherCondition(conditionId);
            String temperature = SunshineWeatherUtils
                    .formatTemperature(this, wd.getCurrTemp());

            // Inflate layout.
            timeView.setText(time);
            imageView.setImageResource(imageRes);
            temperatureView.setText(temperature);
        }
    }

    //TODO: When i call intent to update widget I get nulls because object doesn't exist (it exist).
    private void updateWidgets() {
        Intent intentUpdate = new Intent(getApplicationContext(), WeatherWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] widgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(getApplicationContext(), WeatherWidget.class));

        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        getApplicationContext().sendBroadcast(intentUpdate);
    }
}


