package com.tisza.esemenynaptar

import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.preference.*

class TimePreferenceDialog(key: String) : PreferenceDialogFragmentCompat() {
    init {
        arguments = Bundle(1).apply { putString(ARG_KEY, key) }
    }

    private lateinit var timepicker: TimePicker

    private val timePreference: TimePreference
        get() = preference as TimePreference

    override fun onCreateDialogView(context: Context?): View {
        timepicker = TimePicker(context)
        timepicker.setIs24HourView(true)
        return timepicker
    }

    override fun onBindDialogView(view: View?) {
        timepicker.currentHour = timePreference.time / 60
        timepicker.currentMinute = timePreference.time % 60
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult)
            return

        timePreference.time = timepicker.currentHour * 60 + timepicker.currentMinute
    }
}
