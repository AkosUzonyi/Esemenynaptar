package com.tisza.esemenynaptar

import android.app.*
import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.*
import java.util.*

private const val SAVED_DATE = "date"

class CalendarFragment(private val initialDate: Calendar) : Fragment() {
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: MyPagerAdapter

    constructor() : this(Calendar.getInstance())

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        pagerAdapter.setDate(year, monthOfYear, dayOfMonth)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)

        setHasOptionsMenu(true)

        val date = if (savedInstanceState == null) {
            initialDate
        } else {
            Calendar.getInstance().apply {
                timeInMillis = savedInstanceState.getLong(SAVED_DATE)
            }
        }

        pager = view.findViewById(R.id.pager)
        pagerAdapter = MyPagerAdapter(requireContext(), pager)
        pager.adapter = pagerAdapter
        pagerAdapter.date = date
        return view
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        val now = Calendar.getInstance()
        bundle.putLong(SAVED_DATE, pagerAdapter.date.timeInMillis)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.today -> {
                pagerAdapter.date = Calendar.getInstance()
                true
            }
            R.id.pick_date -> {
                val date = pagerAdapter.date
                val dateDialog = DatePickerDialog(requireContext(), dateSetListener, date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH])
                dateDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
