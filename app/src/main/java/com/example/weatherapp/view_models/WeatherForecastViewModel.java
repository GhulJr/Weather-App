package com.example.weatherapp.view_models;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.weatherapp.interfaces.UpdateCallback;
import com.example.weatherapp.repositries.WeatherInfoRepository;

public class WeatherForecastViewModel extends AndroidViewModel {

    private final Context context;
    private WeatherInfoRepository weatherInfoRepository;
    private LiveData data;

    public WeatherForecastViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        init(context);
    }

    private void init(Context context) {
        weatherInfoRepository = WeatherInfoRepository.getInstance(context);
        data = weatherInfoRepository.getWeatherDataTask();
        //TODO: temporary init
        loadData();
    }

    public LiveData getData() {
        return data;
    }

    public void loadData() {
        weatherInfoRepository.fetchData();
        data = weatherInfoRepository.getWeatherDataTask();

    }
}
