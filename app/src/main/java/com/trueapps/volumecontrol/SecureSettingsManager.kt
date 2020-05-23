package com.trueapps.volumecontrol

import android.content.ContentResolver
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException

class SecureSettingsManager {

    val unsafeVolumeMusicActiveMsMax = UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX

    @Throws(SettingNotFoundException::class)
    fun readUnsafeMilliseconds(contentResolver: ContentResolver?): Int {
        return Settings.Secure.getInt(contentResolver, UNSAFE_VOLUME_MUSIC_ACTIVE_MS_SETTING_NAME)
    }

    fun updateTotalUnsafeMilliseconds(contentResolver: ContentResolver?, value: Int) {
        Settings.Secure.putInt(contentResolver, UNSAFE_VOLUME_MUSIC_ACTIVE_MS_SETTING_NAME, value)
    }

    companion object {
        private const val UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 20 * 3600 * 1000 // 20 hours
        private const val UNSAFE_VOLUME_MUSIC_ACTIVE_MS_SETTING_NAME = "unsafe_volume_music_active_ms"
    }
}