/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.weatherapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.weatherapp.models.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherJsonUtils {

    public static final String TAG = OpenWeatherJsonUtils.class.getSimpleName();
    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getCurrentWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String OWM_LIST = "list";

        /* All temperatures are children of the "temp" object */
        final String OWM_TEMPERATURE = "temp";

        /* Max temperature for the day */
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new String[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
        long startDay = SunshineDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highAndLow;

            /* These are the values that will be collected */
            long dateTimeMillis;
            double high;
            double low;
            String description;

            /* Get the JSON object representing the day */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary and is just a bad variable name.
             */
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = SunshineWeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
        }

        return parsedWeatherData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into WeatherData.
     *
     * @return Single WeatherData parsed from the JSON.
     */
    public static WeatherData getCurrentWeatherDataFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        final String WD_WEATHER = "weather";
        final String WD_CONDITION_ID = "id";
        final String WD_MAIN = "main";
        final String WD_TEMP_CURR = "temp";
        final String WD_TEMP_MIN = "temp_min";
        final String WD_TEMP_MAX = "temp_max";
        final String WD_TIME = "dt";
        final String WD_LOCATION_NAME = "name";
        final String WD_HTTP_CODE = "cod";

        JSONObject currentWeatherJson = new JSONObject(forecastJsonStr);

        WeatherData weatherData = null;

        if(currentWeatherJson.has(WD_HTTP_CODE)) {
            int httpResponseCode = currentWeatherJson.getInt(WD_HTTP_CODE);
            switch (httpResponseCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Log.e(TAG, "Invalid URL!");
                    return null;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    Log.e(TAG, "Invalid api key!");
                    return null;
            }
        }

        // Values used for creating WeatherData object.
        int weatherCondition;
        int currTemp;
        int minTemp;
        int maxTemp;
        long date;
        int forecastType;
        String locationName;

        // JSON object, that contains info about weather condition.
        JSONArray weatherConditionArrayJson = currentWeatherJson.getJSONArray(WD_WEATHER);
        JSONObject weatherConditionJson = weatherConditionArrayJson.getJSONObject(0);
        // JSON object, that contains info about temperature.
        JSONObject mainInfoJson = currentWeatherJson.getJSONObject(WD_MAIN);

        /* Getting values. */

        // Weather condition.
        weatherCondition = weatherConditionJson.getInt(WD_CONDITION_ID);
        // Current temperature.
        currTemp = mainInfoJson.getInt(WD_TEMP_CURR);
        // Minimum temperature.
        minTemp = mainInfoJson.getInt(WD_TEMP_MIN);
        // Maximum temperature.
        maxTemp = mainInfoJson.getInt(WD_TEMP_MAX);
        // Date.
        date = SunshineDateUtils.getLocalDateFromUTC(1000L*currentWeatherJson.getInt(WD_TIME));
        // Forecast type.
        forecastType = WeatherData.FORECAST_TYPE_CURRENT;
        // Location.
        locationName = currentWeatherJson.getString(WD_LOCATION_NAME);

        // Instantiating new object with received values.
        weatherData = new WeatherData(weatherCondition, currTemp, minTemp, maxTemp,
                date, forecastType, locationName);
        return weatherData;
    }
    public static WeatherData[] getForecastWeatherDataFromJson(Context context, String forecastJsonStr)
            throws JSONException {
        /** This will be implemented in a future lesson **/
        return null;
    }
}