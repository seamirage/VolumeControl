package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.trueapps.volumecontrol.R
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreference
import com.trueapps.volumecontrol.common.preferences.NumberPickerPreferenceDialogFragmentCompat


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_new)
    }

    companion object {
        fun makeStartIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null
        if (preference is NumberPickerPreference) {
            dialogFragment = NumberPickerPreferenceDialogFragmentCompat
                    .newInstance(preference.key, preference.minPreferenceValue, preference.maxPreferenceValue)
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
}

