package com.tisza.esemenynaptar

import android.os.*
import androidx.preference.*

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        findPreference<TimePreference>("notification_time")?.onPreferenceChangeListener = this
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePreference) {
            val dialog = TimePreferenceDialog(preference.key)
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, null)
            return
        }

        super.onDisplayPreferenceDialog(preference)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference?.key == "notification_time")
            scheduleNotifications(requireContext())

        return true
    }
}
