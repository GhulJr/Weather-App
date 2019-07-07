package com.example.weatherapp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import android.widget.Toast;

import com.example.weatherapp.R;


public class SettingsFragment extends PreferenceFragmentCompat
implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    /** Triggered when SettingsFragment is created. */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Inflate preferences from xml file.
        addPreferencesFromResource(R.xml.preferences);

        // Register listener to preference changes.
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        // Register listener to check if preferences are correct.
        Preference preference = findPreference(getString(R.string.location_key));
        preference.setOnPreferenceChangeListener(this);

        // Get shared preferences and preference screen.
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        // Get amount of preferences.
        int count = preferenceScreen.getPreferenceCount();

        // Loop that iterate through all preferences to set summary (if necessary).
        for(int i = 0; i < count; ++i) {
            Preference p = preferenceScreen.getPreference(i);
            if(!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    /** Update preference summary whenever it's changed. */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if(p != null) {
            if(!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    //TODO: It's temporary solution for finding location, in final version maps will be attached
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        Toast error = Toast.makeText(getContext(), "Choose correct location!", Toast.LENGTH_SHORT);

        String locationKey = getString(R.string.location_key);
        if(preference.getKey().equals(locationKey)) {
            String location = (String) o;
            if(location.isEmpty()){
                error.show();
                return false;
            }
        }
        return true;
    }

    /** Setting preference summary (if only it's not a checkbox preference).*/
    private void setPreferenceSummary(Preference preference, String value) {
        if(preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            int prefIndex = listPref.findIndexOfValue(value);
            if(prefIndex >= 0) {
                listPref.setSummary(listPref.getEntries()[prefIndex]);
            }
        } else if(preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }
}
