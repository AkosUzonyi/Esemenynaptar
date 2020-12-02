package com.tisza.esemenynaptar

import android.app.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.*
import java.util.*


class CalendarFragment(private val initialDate: Calendar) : Fragment(), DatePickerDialog.OnDateSetListener {
    companion object {
        private const val SI_SAVED_DATE = "date"
        private const val SI_SAVING_DATE = "savingdate"
    }

    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: CalendarPagerAdapter

    constructor() : this(Calendar.getInstance())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)

        setHasOptionsMenu(true)

        pager = view.findViewById(R.id.pager)
        pagerAdapter = CalendarPagerAdapter(requireContext(), pager)
        pager.adapter = pagerAdapter
        pagerAdapter.date = getRestoredDate(savedInstanceState)
        return view
    }

    private fun getRestoredDate(savedInstanceState: Bundle?) : Calendar {
        if (savedInstanceState == null)
            return initialDate

        val today = Calendar.getInstance().timeInMillis / MILLIS_PER_DAY
        val savedDay = savedInstanceState.getLong(SI_SAVING_DATE) / MILLIS_PER_DAY
        if (today != savedDay)
            return initialDate

        val cal = Calendar.getInstance()
        cal.timeInMillis = savedInstanceState.getLong(SI_SAVED_DATE)
        return cal
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putLong(SI_SAVED_DATE, pagerAdapter.date.timeInMillis)
        bundle.putLong(SI_SAVING_DATE, Calendar.getInstance().timeInMillis)
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
                val dateDialog = DatePickerDialog(requireContext(), this, date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH])
                dateDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        pagerAdapter.setDate(year, monthOfYear, dayOfMonth)
    }
}
