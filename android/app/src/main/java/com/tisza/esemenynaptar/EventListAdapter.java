package com.tisza.esemenynaptar;

import java.util.*;

import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;

public class EventListAdapter extends BaseAdapter
{
	private List<String> events = new ArrayList<String>();
	private TextView noEventsView = null;

	public void setEvents(List<String> events)
	{
		this.events = events;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		return Math.max(1, events.size());
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		TextView view;
		if (events.isEmpty())
		{
			if (noEventsView == null)
			{
				noEventsView = (TextView)inflater.inflate(R.layout.no_events_view, parent, false);
			}
			view = noEventsView;
		}
		else
		{
			view = (TextView)(convertView != null && convertView != noEventsView ? convertView : inflater.inflate(R.layout.event_view, parent, false));
			view.setText(Html.fromHtml(events.get(position)));
			view.setMovementMethod(LinkMovementMethod.getInstance());
		}
		return view;
	}
}
