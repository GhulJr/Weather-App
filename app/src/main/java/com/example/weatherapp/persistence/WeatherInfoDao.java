package com.example.weatherapp.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherapp.models.WeatherData;
import com.example.weatherapp.models.WeatherData.Forecast_Type;

import java.util.List;

@Dao
public interface WeatherInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertWeatherData(WeatherData... weatherData);

    @Query("SELECT * FROM weather_information")
    LiveData<List<WeatherData>> getWeatherData();

    @Query("SELECT * FROM weather_information WHERE forecast_type = :forecastType")
    LiveData<List<WeatherData>> getWeatherDataByForecastType(@Forecast_Type int forecastType);

    @Delete
    int deleteWeatherData(WeatherData... weatherData);

    @Update
    int updateWeatherData(WeatherData... weatherData);

}
