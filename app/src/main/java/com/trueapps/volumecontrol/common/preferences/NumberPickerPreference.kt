package com.trueapps.volumecontrol.common.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.trueapps.volumecontrol.R


class NumberPickerPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    var minPreferenceValue: Int? = null
        private set

    var maxPreferenceValue: Int? = null
        private set

    var currentValue: Int = defaultValue
        private set

    init {
        if (attrs.hasAttribute(attributeNamespace, minValueAttrName)) {
            minPreferenceValue = attrs.getAttributeIntValue(attributeNamespace, minValueAttrName, defaultValue)
        }

        if (attrs.hasAttribute(attributeNamespace, maxValueAttrName)) {
            maxPreferenceValue = attrs.getAttributeIntValue(attributeNamespace, maxValueAttrName, defaultValue)
        }
    }

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

    private fun AttributeSet.hasAttribute(namespace: String, attribute:String): Boolean {
        return getAttributeValue(namespace, attribute) != null
    }

    companion object {
        private const val attributeNamespace = "http://schemas.android.com/apk/res-auto"
        private const val minValueAttrName = "min_value"
        private const val maxValueAttrName = "max_value"
        private const val defaultValue: Int = 0
    }
}