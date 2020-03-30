package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.trueapps.volumecontrol.R
import com.trueapps.volumecontrol.VolumeControlApplication
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreference
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreferenceDialogFragmentCompat
import com.trueapps.volumecontrol.notifications.NotificationsScheduler

class SettingsFragment
    : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {

    private lateinit var notificationsService: NotificationsScheduler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        notificationsService = VolumeControlApplication.Instance.dependenciesProvider.notificationsScheduler
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null

        if (preference is NumberPickerPreference) {
            dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                    preference.key,
                    preference.minPreferenceValue,
                    preference.maxPreferenceValue
            )
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            fragmentManager?.let {
                dialogFragment.show(it, dialogFragment.javaClass.canonicalName)
            }
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val notificationPrefKey = getString(R.string.prefs_show_notifications_key)
        if (key == notificationPrefKey) {
            setNotificationSettingsEnabled(sharedPreferences.getBoolean(notificationPrefKey, false))
        }
    }

    private fun setNotificationSettingsEnabled(enabled: Boolean) {
        preferenceScreen.findPreference<NumberPickerPreference>(getString(R.string.prefs_hours_left_before_warning_key))?.apply {
            isEnabled = enabled
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val notificationPrefKey = getString(R.string.prefs_show_notifications_key)
        if (preference.key == notificationPrefKey) {
            when (newValue) {
                true -> notificationsService.enableNotifications()
                false -> notificationsService.disableNotifications()
            }
        }

        return true
    }
}