package com.tisza.esemenynaptar;

import android.content.*;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;

import java.util.*;

public class EventListAdapter extends BaseAdapter
{
	private List<Event> events = new ArrayList<>();
	private TextView noEventsView = null;

	public void setEvents(List<Event> events)
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

		if (events.isEmpty())
		{
			if (noEventsView == null)
				noEventsView = (TextView)inflater.inflate(R.layout.no_events_view, parent, false);

			return noEventsView;
		}

		View view;
		ViewHolder viewHolder;
		if (convertView != null && convertView != noEventsView)
		{
			view = convertView;
			viewHolder = (ViewHolder)convertView.getTag();
		}
		else
		{
			view = inflater.inflate(R.layout.event_view, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.categoryTextView = view.findViewById(R.id.event_category_text);
			viewHolder.iconView = view.findViewById(R.id.event_icon);
			viewHolder.textView = view.findViewById(R.id.event_text);
			viewHolder.shareButton = view.findViewById(R.id.event_share);
			view.setTag(viewHolder);
		}

		final Event event = events.get(position);
		viewHolder.iconView.setImageResource(event.getCategory().getImageRes());
		viewHolder.categoryTextView.setText(event.getCategory().getDisplayNameRes());
		viewHolder.textView.setText(Html.fromHtml(event.getText()));
		viewHolder.textView.setMovementMethod(LinkMovementMethod.getInstance());
		viewHolder.shareButton.setOnClickListener(v ->
		{
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, event.getText());
			sendIntent.setType("text/plain");

			Intent shareIntent = Intent.createChooser(sendIntent, null);
			v.getContext().startActivity(shareIntent);
		});

		return view;
	}

	private static class ViewHolder
	{
		ImageView iconView;
		TextView categoryTextView;
		TextView textView;
		ImageButton shareButton;
	}
}
