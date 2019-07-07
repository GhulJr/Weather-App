package com.example.weatherapp.persistence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.weatherapp.async_tasks.InsertAllDataAsyncTask;
import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.utilities.OpenWeatherJsonUtils;
import com.example.weatherapp.view_models.WeatherForecastViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class WeatherInfoRepository {

    private Context context;
    private WeatherInfoDao weatherInfoDao;
    private WeatherInfoDatabase weatherInfoDatabase;
    public static WeatherInfoRepository instance;

    public WeatherInfoRepository(Context context) {
        this.weatherInfoDatabase = WeatherInfoDatabase.getInstance(context);
        this.weatherInfoDao = weatherInfoDatabase.getWeatherInfoDao();
        this.context = context;
    }

    public static WeatherInfoRepository getInstance(Context context) {
        if(instance == null) {
            instance = new WeatherInfoRepository(context);
        }
        return instance;
    }

    public void insertWeatherDataTask(WeatherData weatherData) {

    }

    public void insertAllWeatherDataTask(WeatherData... weatherData) {
        InsertAllDataAsyncTask insert = new InsertAllDataAsyncTask(weatherInfoDao);
        insert.execute(weatherData);
    }

    public void updateWeatherData(WeatherData weatherData) {

    }

    public void deleteWeatherData(WeatherData weatherData) {

    }

    public LiveData<List<WeatherData>> getWeatherDataTask() {
        return weatherInfoDao.getWeatherData();
    }

    public void fetchData() {
            new AsyncTask<String, Void, WeatherData[]>() {
                final String TAG = WeatherForecastViewModel.class.getSimpleName();

                // TODO: Provide two http request for both daily and forecast weather.
                @Override
                protected WeatherData[] doInBackground(String... location) {

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

                    // Get objects as array from JSONObject string.
                    WeatherData currWeatherData;
                    WeatherData[] weatherData = null;
                    try {
                        currWeatherData = OpenWeatherJsonUtils.getCurrentWeatherDataFromJson(context, jsonResponse);
                        weatherData = new WeatherData[] {currWeatherData};
                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to extract json object.");
                    }

                    // Return array of all weather information.
                    return weatherData;
                }

                @Override
                protected void onPostExecute(WeatherData[] weatherData) {
                    if(weatherData != null) {
                        insertAllWeatherDataTask(weatherData);
                    } else {
                        //TODO: provide feedback about fail
                    }
                }
            }.execute(SunshinePreferences.getPreferredWeatherLocation(context));

    }
}
