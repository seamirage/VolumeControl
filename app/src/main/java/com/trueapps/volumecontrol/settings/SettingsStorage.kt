package com.trueapps.volumecontrol.settings

interface SettingsStorage {
    val minTimeBeforeWarningInHours: Int
    val showNotifications: Boolean
}