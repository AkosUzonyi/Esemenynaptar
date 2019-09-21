package com.tisza.esemenynaptar;

import android.content.*;

import java.io.*;
import java.util.*;

public class EventLoader
{
	public static final String rootDir = "events";

	private Context context;

	public EventLoader(Context context)
	{
		this.context = context;
	}

	public List<Event> loadEvents(Calendar calendar)
	{
		List<Event> events = new ArrayList<>();
		for (Category category : Category.values())
		{
			Event event = loadEventForCategory(calendar, category);
			if (event != null)
				events.add(event);
		}
		return events;
	}
	
	private Event loadEventForCategory(Calendar calendar, Category category)
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (month < 7) year--;
		try
		{
			StringBuilder sb = new StringBuilder();
			Reader r = new InputStreamReader(context.getAssets().open(rootDir + "/" + year + "/" + category.getDirectoryName() + "/" + month + "/" + day + ".txt"));
			char[] buf = new char[1024];
			int read;
			while ((read = r.read(buf)) > 0)
			{
				sb.append(buf, 0, read);
			}
			r.close();
			return new Event(category, sb.toString());
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
