package com.tisza.esemenynaptar;

import android.app.*;
import android.content.*;

import java.util.*;
import java.util.regex.*;

public class DailyReceiver extends BroadcastReceiver
{
	private static final Pattern contentTextPattern = Pattern.compile("(.{0,50})[,. \\s].*");
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (!context.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE).getBoolean(MainActivity.NOTIFICATIONS_ENABLED, true))
			return;
		
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		EventLoader eventLoader = new EventLoader(context);
		Calendar calendar = Calendar.getInstance();
		
		Intent activityIntent = new Intent(context, MainActivity.class);
		activityIntent.putExtra("today", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
		
		for (Category category : EventLoader.categories)
		{
			String event = eventLoader.loadEventForCategory(calendar, category);
			if (event != null)
			{
				Matcher matcher = contentTextPattern.matcher(event);
				
				Notification.Builder builder = new Notification.Builder(context);
				builder.setSmallIcon(category.getImageRes());
				builder.setContentTitle(context.getText(category.getDisplayNameRes()));
				builder.setContentText(event.substring(0, 30) + "...");
				builder.setContentIntent(pendingIntent);
				builder.setAutoCancel(true);
				nm.notify(category.getID(), builder.build());
			}
		}
	}
	
	public static void schedule(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
		int time = sp.getInt(MainActivity.NOTIFICATION_TIME, 420);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MINUTE, time);
		long triggerAtMillis = calendar.getTimeInMillis() + (calendar.getTimeInMillis() < System.currentTimeMillis() ? AlarmManager.INTERVAL_DAY : 0);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, DailyReceiver.class), 0);
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY, pi);
	}
}
