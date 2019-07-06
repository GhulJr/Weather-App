package com.example.weatherapp.models;

import androidx.annotation.IntDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Weather Information")
public class WeatherData {

    /** Constants for forecast type*/
    public static final int FORECAST_TYPE_CURRENT = 0;
    public static final int FORECAST_TYPE_HOURLY = 1;
    public static final int FORECAST_TYPE_DAILY = 2;


    //TODO: provide types of specific columns (if necessary)
    @PrimaryKey(autoGenerate = true)
    private int weatherID;
    @ColumnInfo(name = "Weather Condition")
    private int weatherConditionID;
    @ColumnInfo(name = "Current Temperature")
    private int currTemp;
    @ColumnInfo(name = "Minimum Temperature")
    private int minTemp;
    @ColumnInfo(name = "Maximum Temperature")
    private int maxTemp;
    @ColumnInfo(name = "Date")
    private int dateInSec;
    @ColumnInfo(name = "Forecast Type")
    private int forecastType;


    /** Denotes annotated forecast type to integer type */
    @IntDef({FORECAST_TYPE_CURRENT, FORECAST_TYPE_HOURLY, FORECAST_TYPE_DAILY})
    public @interface Forecast_Type {}

    /** Constructors */
    public WeatherData(int i, int i1, int i2) { //TODO: Temporary constructor
    }

    public WeatherData(int weatherConditionID, int currTemp, int minTemp, int maxTemp,
                       int dateInSec,@Forecast_Type int forecastType) {
        this.weatherConditionID = weatherConditionID;
        this.currTemp = currTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.dateInSec = dateInSec;
        this.forecastType = forecastType;
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

    public int getCurrTemp() {
        return currTemp;
    }

    public void setCurrTemp(int currTemp) {
        this.currTemp = currTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getDateInSec() {
        return dateInSec;
    }

    public void setDateInSec(int dateInSec) {
        this.dateInSec = dateInSec;
    }

    @Forecast_Type
    public int getForecastType() {
        return forecastType;
    }

    public void setForecastType(@Forecast_Type int forecastType) {
        this.forecastType = forecastType;
    }
}
