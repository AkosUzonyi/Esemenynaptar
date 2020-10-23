package com.tisza.esemenynaptar

import android.content.*

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        scheduleNotifications(context)
    }
}
