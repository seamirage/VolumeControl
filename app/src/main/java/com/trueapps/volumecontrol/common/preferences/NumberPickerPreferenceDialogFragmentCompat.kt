package com.trueapps.volumecontrol.common.preferences

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.trueapps.volumecontrol.R

class NumberPickerPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    private lateinit var numberPicker: NumberPicker

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        numberPicker = view.findViewById<NumberPicker>(R.id.np_settings_number_picker)
                .apply {
                    descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

                    arguments?.let { args ->
                        if (args.containsKey(MIN_VALUE_KEY)) {
                            minValue = args.getInt(MIN_VALUE_KEY)
                        }
                        if (args.containsKey(MAX_VALUE_KEY)) {
                            maxValue = args.getInt(MAX_VALUE_KEY)
                        }
                    }
                }

        (preference as? NumberPickerPreference)?.let { pref ->
            numberPicker.value = pref.currentValue
        }

    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            (preference as? NumberPickerPreference)?.let {
                if (it.callChangeListener(numberPicker.value)) {
                    it.updatePersistentValue(numberPicker.value)
                }
            }
        }
    }

    companion object {
        private const val MIN_VALUE_KEY = "min_value"
        private const val MAX_VALUE_KEY = "max_value"

        fun newInstance(key: String, minValue: Int, maxValue: Int): NumberPickerPreferenceDialogFragmentCompat {
            return NumberPickerPreferenceDialogFragmentCompat().apply {
                arguments = Bundle().apply {
                    putInt(MIN_VALUE_KEY, minValue)
                    putInt(MAX_VALUE_KEY, maxValue)
                    putString(ARG_KEY, key)
                }
            }
        }
    }
}