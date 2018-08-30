package com.tisza.esemenynaptar;

import java.io.*;
import java.util.*;

import android.content.*;

public class EventLoader
{
	public static final String rootDir = "events";
	public static final Category[] categories = new Category[]
	{
		new Category(0, "irodalom", R.drawable.irodalom, R.string.irodalom),
		new Category(1, "tortenelem", R.drawable.tortenelem, R.string.tortenelem),
		new Category(2, "zenetortenet", R.drawable.zenetortenet, R.string.zenetortenet),
		new Category(3, "vizualis_kultura", R.drawable.vizualis_kultura, R.string.vizualis_kultura),
	};
	
	private Context context;

	public EventLoader(Context context)
	{
		this.context = context;
	}

	public List<String> loadEvents(Calendar calendar)
	{
		List<String> events = new ArrayList<String>();
		for (Category category : categories)
		{
			String event = loadEventForCategory(calendar, category);
			if (event != null)
			{
				events.add(event);
			}
		}
		return events;
	}
	
	public String loadEventForCategory(Calendar calendar, Category category)
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
			return sb.toString();
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
