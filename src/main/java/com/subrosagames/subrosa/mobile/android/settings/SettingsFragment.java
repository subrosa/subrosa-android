package com.subrosagames.subrosa.mobile.android.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.subrosagames.subrosa.mobile.android.R;

/**
 * Fragment for application settings.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_API_SERVER = "pref_api_server";
    public static final String KEY_PREF_AUTH_SERVER = "pref_auth_server";
    public static final String KEY_PREF_IMAGE_SERVER = "pref_image_server";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_API_SERVER)
                || key.equals(KEY_PREF_AUTH_SERVER)
                || key.equals(KEY_PREF_IMAGE_SERVER))
        {
            Preference server = findPreference(key);
            server.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}