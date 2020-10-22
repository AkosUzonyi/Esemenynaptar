package com.tisza.esemenynaptar

import android.content.*
import android.os.*
import android.text.*
import android.text.method.*
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import com.tisza.esemenynaptar.database.*

class EventListAdapter : BaseAdapter() {
    private var events: LiveData<List<Event>>? = null
    private lateinit var noEventsView: TextView
    private val observer = Observer<List<Event>> { notifyDataSetChanged() }

    fun setEvents(newEvents: LiveData<List<Event>>) {
        events?.removeObserver(observer)
        newEvents.observeForever(observer)
        events = newEvents
    }

    private val currentEvents: List<Event>
        get() = events?.value ?: emptyList()

    override fun getCount(): Int {
        return Math.max(1, currentEvents.size)
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        if (currentEvents.isEmpty()) {
            if (!::noEventsView.isInitialized)
                noEventsView = inflater.inflate(R.layout.no_events_view, parent, false) as TextView

            return noEventsView
        }
        val view: View
        val viewHolder: ViewHolder
        if (convertView != null && convertView !== noEventsView) {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        } else {
            view = inflater.inflate(R.layout.event_view, parent, false)
            viewHolder = ViewHolder()
            viewHolder.categoryTextView = view.findViewById(R.id.event_category_text)
            viewHolder.iconView = view.findViewById(R.id.event_icon)
            viewHolder.textView = view.findViewById(R.id.event_text)
            viewHolder.shareButton = view.findViewById(R.id.event_share)
            viewHolder.likeButton = view.findViewById(R.id.event_like)
            view.tag = viewHolder
        }
        val event = currentEvents[position]
        viewHolder.iconView.setImageResource(event.category.imageRes)
        viewHolder.categoryTextView.setText(event.category.displayNameRes)
        viewHolder.textView.text = Html.fromHtml(event.text)
        viewHolder.textView.movementMethod = LinkMovementMethod.getInstance()
        viewHolder.shareButton.setOnClickListener { v: View ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, event.text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            v.context.startActivity(shareIntent)
        }
        viewHolder.likeButton.setAlpha(if (event.isLiked) 1 else 0.3f)
        viewHolder.likeButton.setOnClickListener { v: View? ->
            object : AsyncTask<Void?, Void?, Void>() {
                protected override fun doInBackground(vararg voids: Void): Void {
                    event.isLiked = !event.isLiked
                    EventDatabase.instance.eventDao().updateEvent(event)
                    return@setOnClickListener null
                }
            }.execute()
        }
        return view
    }

    private class ViewHolder {
        lateinit var iconView: ImageView
        lateinit var categoryTextView: TextView
        lateinit var textView: TextView
        lateinit var likeButton: ImageButton
        lateinit var shareButton: ImageButton
    }
}
