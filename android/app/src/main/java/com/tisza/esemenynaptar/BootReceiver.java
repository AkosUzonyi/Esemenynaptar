package com.tisza.esemenynaptar;

import android.content.*;

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		DailyReceiver.schedule(context);
	}
}
