package com.tisza.esemenynaptar

import android.content.*
import android.os.*
import android.text.*
import android.text.method.*
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import com.tisza.esemenynaptar.database.*

class EventListAdapter(private val context: Context, private val eventRes: Int, private val noEventRes: Int) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val noEventsView = inflater.inflate(noEventRes, null, false)
    private val observer = Observer<List<Event>> { notifyDataSetChanged() }

    var events: LiveData<List<Event>>? = null
        set(value) {
            field?.removeObserver(observer)
            field = value
            field?.observeForever(observer)
        }

    var onEventClickedListener: ((Event) -> Unit)? = null

    private val currentEvents: List<Event>
        get() = events?.value ?: emptyList()

    override fun getCount(): Int {
        return currentEvents.size.coerceAtLeast(1)
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (currentEvents.isEmpty())
            return noEventsView

        val view: View
        val viewHolder: ViewHolder
        if (convertView != null && convertView !== noEventsView) {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        } else {
            view = inflater.inflate(eventRes, parent, false)
            viewHolder = ViewHolder()
            viewHolder.categoryTextView = view.findViewById(R.id.event_category_text)
            viewHolder.iconView = view.findViewById(R.id.event_icon)
            viewHolder.dateTextView = view.findViewById(R.id.event_date)
            viewHolder.textView = view.findViewById(R.id.event_text)
            viewHolder.shareButton = view.findViewById(R.id.event_share)
            viewHolder.likeButton = view.findViewById(R.id.event_like)
            view.tag = viewHolder
        }
        val event = currentEvents[position]

        view.setOnClickListener {
            onEventClickedListener?.invoke(event)
        }
        viewHolder.iconView.setImageResource(event.category.imageRes)
        viewHolder.categoryTextView.setText(event.category.displayNameRes)
        viewHolder.dateTextView.text = dateFormat.format(event.calendar.time)
        viewHolder.textView.text = Html.fromHtml(event.text)
        viewHolder.textView.movementMethod = if (onEventClickedListener == null) LinkMovementMethod.getInstance() else null
        viewHolder.shareButton.setOnClickListener { v: View ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, event.text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            v.context.startActivity(shareIntent)
        }
        viewHolder.likeButton.alpha = if (event.isLiked) 1.0f else 0.3f
        viewHolder.likeButton.setOnClickListener {
            object : AsyncTask<Unit, Unit, Unit>() {
                override fun doInBackground(vararg p0: Unit) {
                    event.isLiked = !event.isLiked
                    eventDatabase.eventDao().updateEvent(event)
                }
            }.execute()
        }
        return view
    }

    private class ViewHolder {
        lateinit var iconView: ImageView
        lateinit var categoryTextView: TextView
        lateinit var dateTextView: TextView
        lateinit var textView: TextView
        lateinit var likeButton: ImageButton
        lateinit var shareButton: ImageButton
    }
}
