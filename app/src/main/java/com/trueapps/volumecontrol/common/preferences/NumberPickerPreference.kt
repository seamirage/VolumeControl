package com.trueapps.volumecontrol.common.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.trueapps.volumecontrol.R

class NumberPickerPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    val minPreferenceValue: Int = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "min_value", 1)
    val maxPreferenceValue: Int = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "max_value", 1)
    private val defaultValue: Int = 1
    var currentValue: Int = defaultValue
        private set

    override fun getDialogLayoutResource(): Int {
        return R.layout.dialog_number_picker
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getInt(index, defaultValue)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValueAttr: Any?) {
        if (restorePersistedValue) {
            currentValue = getPersistedInt(defaultValue)
        } else {
            updatePersistentValue(defaultValueAttr as Int)
        }
    }

    fun updatePersistentValue(newValue: Int) {
        currentValue = newValue
        persistInt(newValue)
    }
}