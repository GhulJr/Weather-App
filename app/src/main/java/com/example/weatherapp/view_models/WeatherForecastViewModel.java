package com.example.weatherapp.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

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
