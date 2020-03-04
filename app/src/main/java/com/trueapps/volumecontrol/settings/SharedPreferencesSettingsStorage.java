package com.trueapps.volumecontrol.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSettingsStorage implements SettingsStorage {
    private static final String SHARED_PREF_NAME = "volume_control_settings";
    private static final String MIN_TIME_BEFORE_WARNING_NAME = "min_time_before_warning_hours";
    private static final String HOW_OFTEN_TO_CHECK_NAME = "how_often_to_check_hours";
    private static final String IS_REMINDING_ENABLED = "is_reminding_enabled";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesSettingsStorage(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getMinTimeBeforeWarningInHours(int defaultValue) {
        return sharedPreferences.getInt(MIN_TIME_BEFORE_WARNING_NAME, defaultValue);
    }

    @Override
    public int getHowOftenToCheckInHours(int defaultValue) {
        return sharedPreferences.getInt(HOW_OFTEN_TO_CHECK_NAME, defaultValue);
    }

    @Override
    public void putMinTimeBeforeWarningInHours(int value) {
        sharedPreferences.edit().putInt(MIN_TIME_BEFORE_WARNING_NAME, value).apply();
    }

    @Override
    public void putHowOftenToCheckInHours(int value) {
        sharedPreferences.edit().putInt(HOW_OFTEN_TO_CHECK_NAME, value).apply();
    }

    @Override
    public boolean isRemindingEnabled() {
        return sharedPreferences.getBoolean(IS_REMINDING_ENABLED, false);
    }

    @Override
    public void putIsRemindingEnabled(boolean value) {
        sharedPreferences.edit().putBoolean(IS_REMINDING_ENABLED, value).apply();
    }
}
