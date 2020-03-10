package com.trueapps.volumecontrol.settings;

public interface SettingsStorage {
    int getMinTimeBeforeWarningInHours(int defaultValue);
    boolean notificationsEnabled();
}
