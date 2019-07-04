package com.example.weatherapp.view_models;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class WeatherLiveData extends LiveData<String[]> {

    Context context;

    public WeatherLiveData(Context context) {
        this.context = context;
        loadData();
    }

    /** Fetch JSON with weather information using AsyncTask*/
    private void loadData() {
        new  AsyncTask<String, Void, String[]>() {
            public final String TAG = WeatherForecastViewModel.class.getSimpleName();

            @Override
            protected String[] doInBackground(String... location) {

                // Check if data exist.
                if(location.length == 0)
                    return null;

                // Get JSON string from url.
                URL url = NetworkUtils.buildUrl(location[0]);
                String jsonResponse = null;
                try {
                    jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to make HTTP request.");
                }

                // Get objects as strings from JSONObject string.
                String dataString[] = new String[0];
                try {
                    dataString = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(context, jsonResponse);
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to extract json object.");
                }

                return dataString;
            }

            @Override
            protected void onPostExecute(String[] s) {
                //TODO: provide weather objects to be bind
                setValue(s);
            }
        }.execute(SunshinePreferences.getPreferredWeatherLocation(context));
    }
}
