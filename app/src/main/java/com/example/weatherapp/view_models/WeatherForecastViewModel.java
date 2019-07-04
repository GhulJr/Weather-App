package com.example.weatherapp.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

public class WeatherForecastViewModel extends AndroidViewModel {

    public final WeatherLiveData data;

    public WeatherForecastViewModel(@NonNull Application application) {
        super(application);
        data = new WeatherLiveData(getApplication());
    }

    public LiveData getData() {
        return data;
    }
}
