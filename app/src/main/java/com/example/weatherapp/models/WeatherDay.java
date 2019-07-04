package com.example.weatherapp.models;

public class WeatherDay {
    private int locationID;
    private int minTemp;
    private int maxTemp;
    private int locationName;


    /** Constructor */
    public WeatherDay(int locationID, int minTemp, int maxTemp) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.locationID = locationID;
    }

    /** Getters */
    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    /** Setters */
    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
}
