package com.tisza.esemenynaptar

import android.app.*
import android.content.*
import android.os.*
import android.text.*
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.preference.*
import com.tisza.esemenynaptar.database.*
import java.util.*

private const val NOTIFICATION_CHANNEL_ID = "event"
private const val NOTIFICATION_GROUP = "event"

fun scheduleNotifications(context: Context) {
    val time = PreferenceManager.getDefaultSharedPreferences(context).getInt("notification_time", 420)
    val calendar = Calendar.getInstance()
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar.add(Calendar.MINUTE, time)
    val triggerAtMillis = calendar.timeInMillis + if (calendar.timeInMillis < System.currentTimeMillis()) AlarmManager.INTERVAL_DAY else 0
    val pi = PendingIntent.getBroadcast(context, 0, Intent(context, DailyReceiver::class.java), 0)
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY, pi)
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val name = context.getString(R.string.notification_channel_name)
    val description = context.getString(R.string.notification_channel_description)
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
    channel.description = description
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

class DailyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enable_notifications", true))
            return

        initEventDatabase(context)
        val eventsLiveData = eventDatabase.eventDao().getEventsForDate(Calendar.getInstance())
        eventsLiveData.observeForever(EventObserver(context, eventsLiveData))
    }
}

private class EventObserver(private val context: Context, private val eventsLiveData: LiveData<List<Event>>) : Observer<List<Event?>?> {
    override fun onChanged(events: List<Event?>?) {
        if (events == null) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.putExtra("today", true)
        val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0)
        for (event in events) {
            if (event == null) continue
            val text = Html.fromHtml(event.text)
            val builder = Notification.Builder(context)
            builder.setSmallIcon(event.category.imageRes)
            builder.setContentTitle(context.getText(event.category.displayNameRes))
            builder.setContentText(text.toString().substring(0, 60) + "...")
            builder.setContentIntent(pendingIntent)
            builder.setAutoCancel(true)
            builder.style = Notification.BigTextStyle().bigText(text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) builder.setGroup(NOTIFICATION_GROUP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) builder.setCategory(Notification.CATEGORY_RECOMMENDATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) builder.setVisibility(Notification.VISIBILITY_PUBLIC)
            nm.notify(event.category.id, builder.build())
        }
        eventsLiveData.removeObserver(this)
    }
}
