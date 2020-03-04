package com.trueapps.volumecontrol.settings;

public interface SettingsStorage {
    int getMinTimeBeforeWarningInHours(int defaultValue);
    int getHowOftenToCheckInHours(int defaultValue);
    void putMinTimeBeforeWarningInHours(int value);
    void putHowOftenToCheckInHours(int value);
    boolean isRemindingEnabled();
    void putIsRemindingEnabled(boolean value);
}
