package com.example.weatherapp.models;

import androidx.annotation.IntDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_information")
public class WeatherData {

    /** Constants for forecast type*/
    public static final int FORECAST_TYPE_CURRENT = 0;
    public static final int FORECAST_TYPE_HOURLY = 1;
    public static final int FORECAST_TYPE_DAILY = 2;


    @PrimaryKey(autoGenerate = true)
    private int weatherID;
    @ColumnInfo(name = "weather_condition")
    private int weatherConditionID;
    @ColumnInfo(name = "current_temperature")
    private double currTemp;
    @ColumnInfo(name = "minimum_temperature")
    private double minTemp;
    @ColumnInfo(name = "maximum_temperature")
    private double maxTemp;
    @ColumnInfo(name = "date")
    private long dateInMillis;
    @ColumnInfo(name = "forecast_type")
    private int forecastType;
    @ColumnInfo(name = "location_name")
    private String locationName;


    /** Denotes annotated forecast type to integer type */
    @IntDef({FORECAST_TYPE_CURRENT, FORECAST_TYPE_HOURLY, FORECAST_TYPE_DAILY})
    public @interface Forecast_Type {}

    public WeatherData(int weatherConditionID, double currTemp, double minTemp, double maxTemp,
                       long dateInMillis, @Forecast_Type int forecastType, String locationName) {
        this.weatherConditionID = weatherConditionID;
        this.currTemp = currTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.dateInMillis = dateInMillis;
        this.forecastType = forecastType;
        this.locationName = locationName;
    }

    /** Getters and setters*/
    public int getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(int weatherID) {
        this.weatherID = weatherID;
    }

    public int getWeatherConditionID() {
        return weatherConditionID;
    }

    public void setWeatherConditionID(int weatherConditionID) {
        this.weatherConditionID = weatherConditionID;
    }

    public double getCurrTemp() {
        return currTemp;
    }

    public void setCurrTemp(double currTemp) {
        this.currTemp = currTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    @Forecast_Type
    public int getForecastType() {
        return forecastType;
    }

    public void setForecastType(@Forecast_Type int forecastType) {
        this.forecastType = forecastType;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
