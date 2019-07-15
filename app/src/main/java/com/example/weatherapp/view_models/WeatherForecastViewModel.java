package com.example.weatherapp.view_models;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.weatherapp.data.SunshinePreferences;
import com.example.weatherapp.interfaces.UpdateCallback;
import com.example.weatherapp.repositries.WeatherInfoRepository;

public class WeatherForecastViewModel extends AndroidViewModel {

    private static final String TAG = WeatherForecastViewModel.class.getSimpleName();
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
        if(SunshinePreferences.isFirstRun(context)){
            loadData();
        }
    }

    public LiveData getData() {
        return data;
    }

    public void loadData() {
        weatherInfoRepository.fetchData();
        data = weatherInfoRepository.getWeatherDataTask();

    }
}
