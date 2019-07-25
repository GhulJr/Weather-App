package com.example.weatherapp.async_tasks;

import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.persistence.WeatherInfoDao;
import com.example.weatherapp.persistence.WeatherInfoDatabase;

//TODO: this class is temporary, in future it might be relaced with executor, future task or Rx.
public class ClearThenInsertTask implements Runnable {

    private WeatherInfoDao weatherInfoDao;
    private WeatherData[] weatherData;

    public ClearThenInsertTask(WeatherInfoDao weatherInfoDao, WeatherData[] weatherData) {
        this.weatherInfoDao = weatherInfoDao;
        this.weatherData = weatherData;
    }

    @Override
    public void run() {
        // Clear all tables because we are interested only in latest data.
        WeatherInfoDatabase.instance.clearAllTables(); //TODO: It should be separated.
        // Insert given data.
        weatherInfoDao.insertWeatherData(weatherData);
    }
}
