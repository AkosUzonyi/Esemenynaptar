package com.tisza.esemenynaptar;

import android.content.*;
import android.view.*;
import android.widget.*;
import androidx.viewpager.widget.*;

import java.util.*;

public class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener
{
	private static final int CHILD_COUNT = 5;
	private static final int MIDDLE_CHILD = CHILD_COUNT / 2;
	
	private EventLoader eventLoader;
	private ViewPager pager;
	private Calendar date = Calendar.getInstance();
	private ListView[] childs = new ListView[CHILD_COUNT];
	
	public MyPagerAdapter(Context context, ViewPager pager, EventLoader eventLoader)
	{
		this.eventLoader = eventLoader;
		this.pager = pager;

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			childs[i] = (ListView)layoutInflater.inflate(R.layout.event_list_view, pager, false);
			childs[i].setAdapter(new EventListAdapter());
		}
	}
	
	public void setDate(Calendar date)
	{
		setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
	}
	
	public void setDate(int year, int month, int day)
	{
		date.set(year, month, day);
		updateEventViewsContent();
		notifyDataSetChanged(); //just for the titles
	}
	
	public Calendar getDate()
	{
		return date;
	}
	
	private void updateEventViewsContent()
	{
		Calendar cal = (Calendar)date.clone();
		cal.add(Calendar.DAY_OF_MONTH, -MIDDLE_CHILD);
		
		for (ListView child : childs)
		{
			((EventListAdapter)child.getAdapter()).setEvents(eventLoader.loadEvents(cal));
			child.setSelection(0);
			
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		pager.setCurrentItem(MIDDLE_CHILD, false);
	}
	
	@Override
	public CharSequence getPageTitle(int position)
	{
		Calendar c = (Calendar)date.clone();
		c.add(Calendar.DAY_OF_MONTH, position - MIDDLE_CHILD);
		return MainActivity.dateFormat.format(c.getTime());
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		View v = childs[position];
		container.addView(v);
		return v;
	 }

	@Override
	public int getCount()
	{
		return CHILD_COUNT;
	}

	@Override
	public boolean isViewFromObject(View v, Object o)
	{
		return v == o;
	}

	@Override
	public void onPageScrollStateChanged(int state)
	{
		if (state == ViewPager.SCROLL_STATE_IDLE)
		{
			int diff = pager.getCurrentItem() - MIDDLE_CHILD;
			if (diff != 0)
			{
				date.add(Calendar.DAY_OF_MONTH, diff);
				updateEventViewsContent();
			}
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
	}

	@Override
	public void onPageSelected(int pos)
	{
		
	}

}
