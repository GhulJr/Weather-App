package com.example.weatherapp.view_models;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherLiveData extends LiveData<List<WeatherData>> {

    Context context;

    public WeatherLiveData(final Context context) {
        this.context = context;
        loadData();
    }

    /** Fetch JSON with weather information using AsyncTask*/
    private void loadData() {

    }
}
