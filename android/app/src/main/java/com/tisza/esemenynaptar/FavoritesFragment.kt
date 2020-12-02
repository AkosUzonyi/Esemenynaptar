package com.tisza.esemenynaptar

import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import com.tisza.esemenynaptar.database.*
import java.util.*

class FavoritesFragment : Fragment() {
    private lateinit var eventListAdapter: EventListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventListAdapter = EventListAdapter(requireContext(), R.layout.event_view_small, R.layout.no_favorites_view)
        eventListAdapter.events = eventDatabase.eventDao().getLikedEvents()
        eventListAdapter.onEventClickedListener = { event ->
            val calendarFragment = CalendarFragment(event.calendar)
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, calendarFragment)
                    .addToBackStack(null)
                    .commit()
        }

        val view = inflater.inflate(R.layout.favorites_fragment, container, false) as ListView
        view.adapter = eventListAdapter
        return view
    }
}
