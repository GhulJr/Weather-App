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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.recycler_views.WeatherAdapter;
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

    private RecyclerView recyclerView;
    private WeatherAdapter mAdapter;
    private WeatherForecastViewModel viewModel;
    private WorkManager workManager;
    private PeriodicWorkRequest workRequest;

    /** onCreate method. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: find out why it was present
        // Hide home button.
        setUpActionBar();

        // Set up all values related to shared preferences.
        setUpSharedPreferences();

        // Set up recycler view for hourly forecast information.
        setUpHourlyRecyclerView();

        // Set up view model.
        setUpViewModel();

        // Set up workers.
        setUpWorkers();
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
            TextView tv = findViewById(R.id.weather_temp);
            String[] s = tv.getText().toString().split(String.valueOf((char) 0x00B0));
            String updatedTemp = SunshineWeatherUtils
                    .formatTemperature(getApplicationContext(), Double.parseDouble(s[0]));
            tv.setText(updatedTemp);
            // Update hourly forecast unit.
            mAdapter.notifyDataSetChanged();
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

    private void setUpHourlyRecyclerView() {
        // Get recycler view layout.
        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Provide layout manager for recycler view.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Create adapter for recycler view with listener, that starts weather details activity.
        mAdapter = new WeatherAdapter(getApplicationContext(), new WeatherAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                Intent intent = new Intent(
                        MainActivity.this, WeatherDetailsActivity.class);
                startActivity(intent);
            }
        });
        // Set adapter to recycler view.
        recyclerView.setAdapter(mAdapter);
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
                inflateDailyWeatherLayout(weatherData);
                inflateHourlyForecastLayout(weatherData);
            }
        });
    }

    private void setUpWorkers() {
        workManager = WorkManager.getInstance(getApplicationContext());
        Constraints constraints = new Constraints.Builder()
                .build();

        workRequest = (PeriodicWorkRequest) WorkerUtilities
                .getPeriodicWorkRequest(getApplicationContext());
        if(workRequest != null) {
            workManager.enqueue(workRequest);
        }
    }

    private void inflateDailyWeatherLayout(List<WeatherData> weatherData) {
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

    private void inflateHourlyForecastLayout(List<WeatherData> weatherData) {

        List<WeatherData> forecastWeatherData = new ArrayList<>();

        for(WeatherData wd : weatherData) {
            if(wd.getForecastType() == WeatherData.FORECAST_TYPE_HOURLY) {
                forecastWeatherData.add(wd);
            }
        }

        mAdapter.setWeatherData(forecastWeatherData);
    }
}

//TODO: Na chwilę lista co mam zrobić:
//- automatyczne updaty
//- powiadomienia (w końcu serwisy, jeeeej :D)
//-(ficzerek) wiele lokalizacji
//-(ficzerek) widget
//- zamienić listę na hashmapę (w LiveData)
//- zmienić format datowy nieco
//- no i na końcu porawanie juajki, dodawanie animacji, powiadomień o braku połączenia itp. itd.
//- może użyć card layout?
//- animowane ikony!
//- dodać odpowiednie weather conditions
//- przepisać async tasks na coś innego
//- zacząć korzystać z trello :CCCCCCCCC
//- poprawić wszystko co działa na wątkach
//- jeżeli baza jest pusta daj o tym znać
//- klikowalne notificationy
//- Albo uda mi się jakoś obserwować repo przez live datę i wtedy nie będzie problemu z serwisem,
//  albo może stworze własną livedatę, która będzie updatowała się gdy serwis zakończy działanie (wolę pierwsze).

//- co do notyfikacji to: żeby uruchamiało się tylko przy pierwszym odpaleniu apki, dopracować UI,
//  włączyć apkę jeśli kliknie się na notyfikacje, przenieść wszystko do klasy utills, poprawić w samym workerze
//  zwracane wyniki w doWork, dodać opcje wyłączenia powiadomień (trzeba updatować dane dołączone do requesta),
//  poprawić obserwowanie zmian w danych (livedata w repo i viewmodel obserwuje).

//- manualne updaty też //////////////////////////////////////////////ZROBIONE
//- zrobić observa (albo zwykły callback) view modelu na repo żeby po insercie tworzyło live data
//- zapewnić dobre przechowywanie na dane ///////////////////////////////ZROBIONE
//- poprawić list item ///////////////////////zrobione
//- pokombinować z wieloma forcastami i wgl ////////////////Zrobione
//- generalnie ogarnąć jak przetwarzać dane z tego api ///////////////////////////////////ZROBIONE
//- dodać preferencje i ogarnąć, w jaki sposób wybierać miasta //////////////////ZROBIONE

