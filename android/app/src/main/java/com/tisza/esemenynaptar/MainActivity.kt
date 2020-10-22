package com.tisza.esemenynaptar

import android.app.*
import android.app.DatePickerDialog.*
import android.app.TimePickerDialog.*
import android.content.*
import android.os.*
import android.view.*
import androidx.appcompat.app.*
import androidx.appcompat.widget.*
import androidx.viewpager.widget.*
import androidx.viewpager.widget.ViewPager.*
import com.tisza.esemenynaptar.database.*
import java.text.*
import java.util.*

class MainActivity : AppCompatActivity(), OnPageChangeListener {
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var inflater: LayoutInflater
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var savedDate: Calendar

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        pagerAdapter.setDate(year, monthOfYear, dayOfMonth)
    }

    private val nofificationTimeSetListener = OnTimeSetListener { view, hourOfDay, minute ->
        val editor = sharedPreferences.edit()
        editor.putInt(NOTIFICATION_TIME, hourOfDay * 60 + minute)
        editor.commit()
        DailyReceiver.schedule(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = layoutInflater
        EventDatabase.init(this) { onDatabaseReady() }
        sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        savedDate = Calendar.getInstance()
        val today = savedDate.timeInMillis / MILLIS_PER_DAY
        if (savedInstanceState != null && savedInstanceState.getLong(SAVING_DAY) == today)
            savedDate.timeInMillis = savedInstanceState.getLong(SAVED_DATE)
        DailyReceiver.createNotificationChannel(this)
        DailyReceiver.schedule(this)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    private fun onDatabaseReady() {
        pager = findViewById<View>(R.id.pager) as ViewPager
        pager.setOnPageChangeListener(this)
        pagerAdapter = MyPagerAdapter(this, pager)
        pager.adapter = pagerAdapter
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis / MILLIS_PER_DAY
        pagerAdapter.date = savedDate
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        val now = Calendar.getInstance()
        bundle.putLong(SAVED_DATE, pagerAdapter.date.timeInMillis)
        bundle.putLong(SAVING_DAY, now.timeInMillis / MILLIS_PER_DAY)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(TODAY_EXTRA, false)) {
            pagerAdapter.date = Calendar.getInstance()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.enable_notification).isChecked = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, true)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val setTimeItem = menu.findItem(R.id.set_notification_time)
        val enableNotificationItem = menu.findItem(R.id.enable_notification)
        setTimeItem.isEnabled = enableNotificationItem.isChecked
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.today -> {
                pagerAdapter.date = Calendar.getInstance()
                true
            }
            R.id.pick_date -> {
                val date = pagerAdapter.date
                val dateDialog = DatePickerDialog(this, dateSetListener, date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH])
                dateDialog.show()
                true
            }
            R.id.enable_notification -> {
                val enable = !item.isChecked
                item.isChecked = enable
                val editor = sharedPreferences.edit()
                editor.putBoolean(NOTIFICATIONS_ENABLED, enable)
                editor.commit()
                supportInvalidateOptionsMenu()
                true
            }
            R.id.set_notification_time -> {
                val time = sharedPreferences.getInt(NOTIFICATION_TIME, 420)
                val timeDialog = TimePickerDialog(this, nofificationTimeSetListener, time / 60, time % 60, true)
                timeDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        pagerAdapter.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        pagerAdapter.onPageScrolled(arg0, arg1, arg2)
    }

    override fun onPageSelected(page: Int) {
        pagerAdapter.onPageSelected(page)
    }

    companion object {
        const val MILLIS_PER_DAY = 86400000
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        const val SHARED_PREF = "sp"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val NOTIFICATION_TIME = "notification_time"
        const val TODAY_EXTRA = "today"
        private const val SAVED_DATE = "date"
        private const val SAVING_DAY = "savingdate"
    }
}
