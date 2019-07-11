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

import android.net.Uri;
import android.util.Log;

import com.example.weatherapp.models.WeatherData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String DAILY_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";

    private static final String API_KEY = "e5613d30ae570fde815617ff3b9fcac8";

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";
    /* The number of days we want our API to return */
    private static final int numDays = 40;

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";
    final static String APPID_PARAM= "appid";

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String locationQuery, int forecastType) {
        // Provide URL based on given forecast type.
        final String URL;
        if(forecastType == WeatherData.FORECAST_TYPE_CURRENT) {
            URL = DAILY_BASE_URL;
        } else {
            URL = FORECAST_BASE_URL;
        }
        Uri builtUri = Uri.parse(URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build();
        URL url = null;

        try {
            //Log.e(TAG, "URL looks like this: " + builtUri.toString());
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with creating URL object.");
        }
        return url;
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrl(Double lat, Double lon) {
        Uri.Builder builtUri = Uri.parse(DAILY_BASE_URL)
                .buildUpon()
                .appendQueryParameter(LAT_PARAM, String.valueOf(lat))
                .appendQueryParameter(LON_PARAM, String.valueOf(lon));
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with creating URL object.");
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        String jsonResponse = null;
        // Check if url is not empty.
        if(url == null) return null;

        // Creating url connection and setting basic parameters.
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(/*In millis*/10000);
        urlConnection.setConnectTimeout(/*in millis*/15000);
        // Establishing connection.
        urlConnection.connect();

        // Check if connection was successful. Then read JSON string from stream.
        try {
            if(urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                jsonResponse = readFromStream(in);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
            return jsonResponse;
        }
    }

    public static String readFromStream(InputStream in) {
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\A");

        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }
}

//TODO: add and use progress bar.
//TODO: provide messages for failed/no connection.
//TODO: read about collections to choose the best one for containing weather data.
//TODO: provide searching by id if needed.