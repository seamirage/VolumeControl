package com.nata.volumecontrol;

import android.content.ContentResolver;
import android.provider.Settings;

public class VolumeControlService {
    private static final String UNSAFE_VOLUME_MUSIC_ACTIVE_MS = "unsafe_volume_music_active_ms";
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = (20 * 3600 * 1000); // 20 hours

    public int getUnsafeMilliseconds(ContentResolver contentResolver) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(contentResolver, UNSAFE_VOLUME_MUSIC_ACTIVE_MS);
    }

    public void putTotalUnsafeMilliseconds(ContentResolver contentResolver, int value) {
        Settings.Secure.putInt(contentResolver, UNSAFE_VOLUME_MUSIC_ACTIVE_MS, value);
    }

    public int getMaxUnsafeMusicPlayDuration() {
        return UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX;
    }
}
