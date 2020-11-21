package com.tisza.esemenynaptar

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.*
import java.util.*

private const val SAVED_DATE = "date"

class CalendarFragment(val initialDate: Calendar) : Fragment() {
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        pagerAdapter.setDate(year, monthOfYear, dayOfMonth)
    }

    private val nofificationTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        val editor = sharedPreferences.edit()
        editor.putInt(SP_NOTIFICATION_TIME, hourOfDay * 60 + minute)
        editor.commit()
        scheduleNotifications(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)

        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        val setTimeItem = menu.findItem(R.id.set_notification_time)
        val enableNotificationItem = menu.findItem(R.id.enable_notification)
        setTimeItem.isEnabled = enableNotificationItem.isChecked
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
            R.id.enable_notification -> {
                val enable = !item.isChecked
                item.isChecked = enable
                val editor = sharedPreferences.edit()
                editor.putBoolean(SP_NOTIFICATIONS_ENABLED, enable)
                editor.commit()
                requireActivity().supportInvalidateOptionsMenu()
                true
            }
            R.id.set_notification_time -> {
                val time = sharedPreferences.getInt(SP_NOTIFICATION_TIME, 420)
                val timeDialog = TimePickerDialog(requireContext(), nofificationTimeSetListener, time / 60, time % 60, true)
                timeDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
