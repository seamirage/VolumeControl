package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import com.trueapps.volumecontrol.R

class SharedPreferencesSettingsStorage(private val context: Context) : SettingsStorage {
    private val sharedPreferences: SharedPreferences
    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun getMinTimeBeforeWarningInHours(defaultValue: Int): Int {
        return sharedPreferences.getInt(context.getString(R.string.prefs_hours_left_before_warning_key), defaultValue)
    }

    override fun notificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(SHOW_NOTIFICATIONS, false)
    }

    companion object {
        private const val SHARED_PREF_NAME = "volume_control_settings"
        private const val SHOW_NOTIFICATIONS = "show_notifications"

    }
}