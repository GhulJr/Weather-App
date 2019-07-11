package com.example.weatherapp.async_tasks;

import android.os.AsyncTask;

import com.example.weatherapp.interfaces.UpdateCallback;
import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.persistence.WeatherInfoDao;
import com.example.weatherapp.persistence.WeatherInfoDatabase;

public class ClearThenInsertAllAsyncTask extends AsyncTask<WeatherData, Void, Void> {

    private WeatherInfoDao weatherInfoDao;

    public ClearThenInsertAllAsyncTask(WeatherInfoDao weatherInfoDao ) {
        this.weatherInfoDao = weatherInfoDao;
    }

    @Override
    protected Void doInBackground(WeatherData... weatherData) {
        // Clear all tables because we are interested only in latest data.
        WeatherInfoDatabase.instance.clearAllTables(); //TODO: It should be separated.
        // Insert given data.
        weatherInfoDao.insertWeatherData(weatherData);
        return null;
    }
}
