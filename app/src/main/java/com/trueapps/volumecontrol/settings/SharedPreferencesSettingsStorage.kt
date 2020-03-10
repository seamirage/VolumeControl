package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import com.trueapps.volumecontrol.R

class SharedPreferencesSettingsStorage(private val context: Context) : SettingsStorage {
    private val sharedPreferences: SharedPreferences
    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override val minTimeBeforeWarningInHours: Int
    get() {
        return sharedPreferences.getInt(
                context.getString(R.string.prefs_hours_left_before_warning_key),
                HOURS_BEFORE_WARNING_DEFAULT_VALUE
        )
    }

    override val showNotifications: Boolean
    get() {
        return sharedPreferences.getBoolean(
                context.getString(R.string.prefs_show_notifications_key),
                SHOW_NOTIFICATIONS_DEFAULT_VALUE
        )
    }

    companion object {
        private const val SHARED_PREF_NAME = "volume_control_settings"
        private const val SHOW_NOTIFICATIONS_DEFAULT_VALUE = true
        private const val HOURS_BEFORE_WARNING_DEFAULT_VALUE = 3
    }
}