package com.tisza.esemenynaptar

import android.content.*
import android.util.*
import androidx.preference.*

class TimePreference(ctxt: Context?, attrs: AttributeSet?) : DialogPreference(ctxt, attrs) {
    var time: Int
        get() = super.getPersistedInt(DEFAULT_TIME)
        set(value) {
            super.persistInt(value)
            summary = "${time / 60}:${time % 60}"
            notifyChanged()
        }

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)
        summary = "${time / 60}:${time % 60}"
    }

    companion object {
        const val DEFAULT_TIME = 9 * 60
    }
}
