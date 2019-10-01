package com.tisza.esemenynaptar;

import android.content.*;
import android.view.*;
import android.widget.*;
import androidx.viewpager.widget.*;
import com.tisza.esemenynaptar.database.*;

import java.util.*;

public class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener
{
	private static final int CHILD_COUNT = 5;
	private static final int MIDDLE_CHILD = CHILD_COUNT / 2;
	
	private ViewPager pager;
	private Calendar date = Calendar.getInstance();
	private ListView[] childs = new ListView[CHILD_COUNT];
	private EventListAdapter[] adapters = new EventListAdapter[CHILD_COUNT];

	public MyPagerAdapter(Context context, ViewPager pager)
	{
		this.pager = pager;

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			childs[i] = (ListView)layoutInflater.inflate(R.layout.event_list_view, pager, false);
			adapters[i] = new EventListAdapter();
			childs[i].setAdapter(adapters[i]);
		}
	}
	
	public void setDate(Calendar date)
	{
		setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
	}
	
	public void setDate(int year, int month, int day)
	{
		date.set(year, month, day);
		for (int i = 0; i < CHILD_COUNT; i++)
			loadDataForChild(i);
		pager.setCurrentItem(MIDDLE_CHILD, false);
		notifyDataSetChanged(); //just for the titles
	}
	
	public Calendar getDate()
	{
		return date;
	}

	private void loadDataForChild(int childPos)
	{
		Calendar cal = (Calendar)date.clone();
		cal.add(Calendar.DAY_OF_MONTH, childPos - MIDDLE_CHILD);
		adapters[childPos].setEvents(EventDatabase.getInstance().eventDao().getEventsForDate(cal));
		childs[childPos].setSelection(0);
	}

	private void shift(int diff)
	{
		if (diff <= -CHILD_COUNT || diff >= CHILD_COUNT)
			throw new IllegalArgumentException("cannot shift with values larger than CHILD_COUNT: " + diff);

		date.add(Calendar.DAY_OF_MONTH, diff);

		EventListAdapter[] oldAdapters = adapters.clone();
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			adapters[i] = oldAdapters[(i + diff + CHILD_COUNT) % CHILD_COUNT];
			childs[i].setAdapter(adapters[i]);
			if (i + diff < 0 || i + diff >= CHILD_COUNT)
				loadDataForChild(i);
		}
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
			shift(pager.getCurrentItem() - MIDDLE_CHILD);
			pager.setCurrentItem(MIDDLE_CHILD, false);
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
