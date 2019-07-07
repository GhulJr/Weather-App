package com.example.weatherapp.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weatherapp.models.WeatherData;

@Database(entities = {WeatherData.class}, version = 1)
public abstract class WeatherInfoDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "weather_info_db";

    public static WeatherInfoDatabase instance;

    public static WeatherInfoDatabase getInstance(final Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    WeatherInfoDatabase.class,
                    DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract WeatherInfoDao getWeatherInfoDao();

}
