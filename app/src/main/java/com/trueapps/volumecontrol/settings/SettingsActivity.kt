package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.trueapps.volumecontrol.R
import com.trueapps.volumecontrol.VolumeControlApplication
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreference
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreferenceDialogFragmentCompat
import com.trueapps.volumecontrol.notifications.NotificationsScheduler


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    companion object {
        fun makeStartIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}

class SettingsFragment
    : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private lateinit var notificationsService: NotificationsScheduler
    private lateinit var settingsStorage: SettingsStorage
    private var showNotificationsTogglePref: SwitchPreferenceCompat? = null
    private var hoursBeforeWarningPref: NumberPickerPreference? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        notificationsService = VolumeControlApplication.Instance.dependenciesProvider.notificationsScheduler
        settingsStorage = VolumeControlApplication.Instance.dependenciesProvider.preferencesStorage
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)

        showNotificationsTogglePref = findPreference<SwitchPreferenceCompat>(getString(R.string.prefs_show_notifications_key))?.apply {
            onPreferenceChangeListener = this@SettingsFragment
        }
        hoursBeforeWarningPref = findPreference(getString(R.string.prefs_hours_left_before_warning_key))
        updateHoursBeforeWarningView()
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
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
        updateHoursBeforeWarningView()
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

    private fun updateHoursBeforeWarningView() {
        hoursBeforeWarningPref?.apply {
            isEnabled = settingsStorage.showNotifications
            summary = settingsStorage.minTimeBeforeWarningInHours.toString()
        }
    }
}