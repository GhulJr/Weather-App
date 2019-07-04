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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.weatherapp.data.WeatherDay;
import com.example.weatherapp.recycler_views.WeatherAdapter;
import com.example.weatherapp.settings.SettingsActivity;
import com.example.weatherapp.view_models.WeatherForecastViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    /** onCreate method. */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: find out why it was present
        /**Hide return action button*/
        getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon

        /**Set up all values related to shared preferences*/
        setUpSharedPreferences();

        //TODO: It's temporary, later on i will use LiveData+ViewModel.
        /** List of all weather items. */
        List<WeatherDay> weatherDayList = new ArrayList<>();

        // Temporary placeholder data
        for(int i = 0; i < 14; ++i) {
            weatherDayList.add(new WeatherDay(i,i-7, i+5));
        }

        /** Get recycler view layout. */
        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);

        /** Provide layout manager for recycler view. */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //TODO: Temporary create adapter here to check if recycler view works correctly.
        mAdapter = new WeatherAdapter(weatherDayList, new WeatherAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                Intent intent = new Intent(MainActivity.this, WeatherDetailsActivity.class);
                startActivity(intent);
            }
        });

        /** Create ViewModel. */
        WeatherForecastViewModel model = ViewModelProviders
                                        .of(this)
                                        .get(WeatherForecastViewModel.class);

        /** Set observer to LiveData. */
        model.getData().observe(this, new Observer() {
           @Override
           public void onChanged(@Nullable Object o) {
               //TODO: Change UI here.
               //Set adapter to recycler view
               recyclerView.setAdapter(mAdapter);
           }
        });
    }

    @Override
    /** onDestroy method. */
    protected void onDestroy() {
        super.onDestroy();
        //Unregister shared preferences from activity
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    /** Inflate menu when created. */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    /** Set an actions, that will be taken when item is clicked. */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:{
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       /* if(key.equals(R.string.auto_refresh_key)) {
            //sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.default_refresh));
        }*/

        //TODO: Provide update mechanics when shared preferences are changed (refresh and so on),
        // refresh should be in onStart() method.
    }

    //TODO: use this
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
  /*  private void loadWeatherData() {
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }
*/

  /** Get all the values from shared preferences to set it up. */
  public void setUpSharedPreferences() {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      sharedPreferences.registerOnSharedPreferenceChangeListener(this);
      //TODO: set it when shared preferences will be ready
  }
}

//TODO: Na chwilę lista co mam zrobić:
//- zapewnić dobre przechowywanie na dane
//- poprawić list item
//- pokombinować z wieloma forcastami i wgl
//- generalnie ogarnąć jak przetwarzać dane z tego api xD
//- dodać preferencje i ogarnąć, w jaki sposób wybierać miasta
//- automatyczne updaty
//- manualne updaty też
//- powiadomienia (w końcu serwisy, jeeeej :D)
//-(ficzerek) wiele lokalizacji
//-(ficzerek) widget
//- no i na końcu porawanie juajki, dodawanie animacji, powiadomień o braku połączenia itp. itd.