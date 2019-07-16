package com.example.weatherapp.repositries;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.weatherapp.R;
import com.example.weatherapp.async_tasks.ClearThenInsertTask;
import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.persistence.WeatherInfoDao;
import com.example.weatherapp.persistence.WeatherInfoDatabase;
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
    public static WeatherInfoRepository instance;

    public WeatherInfoRepository(Context context) {
        WeatherInfoDatabase weatherInfoDatabase = WeatherInfoDatabase.getInstance(context);
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

    public void ClearThenInsertTask(WeatherData... weatherData) {
        Thread thread =new Thread(new ClearThenInsertTask(weatherInfoDao, weatherData));
        thread.start();
    }

    public void updateWeatherData(WeatherData weatherData) {
    }

    public void deleteWeatherDataTask(WeatherData weatherData) {
    }

    public void deletaAllWeatherData(WeatherData... weatherData) {

    }

    public LiveData<List<WeatherData>> getWeatherDataTask() {
        return weatherInfoDao.getWeatherData();
    }

    public void fetchData() {
            new  AsyncTask<String, Void, WeatherData[]>() {
                final String TAG = WeatherForecastViewModel.class.getSimpleName();

                @Override
                protected WeatherData[] doInBackground(String... location) {
                    // Check if data exist.
                    if (location.length == 0)
                        return null;

                    // Get JSON string from url.
                    URL dailyUrl = NetworkUtils.buildUrl(
                            location[0], WeatherData.FORECAST_TYPE_CURRENT);
                    URL hourlyUrl = NetworkUtils.buildUrl(
                            location[0], WeatherData.FORECAST_TYPE_HOURLY);
                    String jsonDailyResponse = null;
                    String jsonHourlyResponse = null;
                    try {
                        jsonDailyResponse = NetworkUtils.getResponseFromHttpUrl(dailyUrl);
                        jsonHourlyResponse = NetworkUtils.getResponseFromHttpUrl(hourlyUrl);
                    } catch (IOException e) {
                        Log.e(TAG, "Unable to make HTTP request.");
                        return null;
                    }

                    // Get objects as array from JSONObject string.
                    WeatherData currWeatherData;
                    WeatherData[] forecastWeatherData;
                    WeatherData[] weatherData = null;
                    try {
                        currWeatherData = OpenWeatherJsonUtils
                                .getCurrentWeatherDataFromJson(context, jsonDailyResponse);
                        forecastWeatherData = OpenWeatherJsonUtils
                                .getForecastWeatherDataFromJson(context, jsonHourlyResponse);

                        if(currWeatherData == null || forecastWeatherData == null) {
                            throw new JSONException("JSONObject is empty or equals null.");
                        }

                        weatherData = new WeatherData[forecastWeatherData.length+1];
                        weatherData[0] = currWeatherData;
                        System.arraycopy(forecastWeatherData, 0,
                                weatherData, 1, forecastWeatherData.length);

                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to extract json object.");
                    }

                    // Return array of all weather information.
                    return weatherData;
                }

                @Override
                protected void onPostExecute(WeatherData[] weatherData) {
                    if(weatherData != null) {
                        ClearThenInsertTask(weatherData);
                    } else {
                        Toast.makeText(context, R.string.fetching_data_failed, Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }.execute(SunshinePreferences.getPreferredWeatherLocation(context));

    }
}
