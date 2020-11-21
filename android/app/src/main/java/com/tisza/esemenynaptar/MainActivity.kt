package com.tisza.esemenynaptar

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.appcompat.app.*
import androidx.appcompat.widget.*
import com.tisza.esemenynaptar.database.*
import java.text.*
import java.util.*

const val MILLIS_PER_DAY = 86400000
val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
const val SHARED_PREF = "sp"
const val SP_NOTIFICATIONS_ENABLED = "notifications_enabled"
const val SP_NOTIFICATION_TIME = "notification_time"
const val TODAY_EXTRA = "today"

class MainActivity : AppCompatActivity() {
    private lateinit var inflater: LayoutInflater
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = layoutInflater
        initEventDatabase(this) { onDatabaseReady() }
        sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        createNotificationChannel(this)
        scheduleNotifications(this)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    private fun onDatabaseReady() {
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, CalendarFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.enable_notification).isChecked = sharedPreferences.getBoolean(SP_NOTIFICATIONS_ENABLED, true)
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(TODAY_EXTRA, false)) {
            //pagerAdapter.date = Calendar.getInstance()
        }
    }
}
