package com.tisza.esemenynaptar

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.appcompat.app.*
import androidx.core.view.*
import com.tisza.esemenynaptar.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.*
import java.util.*

const val MILLIS_PER_DAY = 86400000
val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
const val TODAY_EXTRA = "today"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEventDatabase(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        navigation_view.setNavigationItemSelectedListener(this::onMenuItemSelected)
        createNotificationChannel(this)
        scheduleNotifications(this)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, CalendarFragment())
                .commit()
    }

    private fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val fragment = when (menuItem.itemId) {
            R.id.nav_calendar -> CalendarFragment()
            R.id.nav_favorites -> FavoritesFragment()
            R.id.nav_settings -> SettingsFragment()
            else -> throw RuntimeException("invalid menu item id")
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit()

        menuItem.isChecked = true
        title = menuItem.title
        drawer.closeDrawers()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(TODAY_EXTRA, false)) {
            //pagerAdapter.date = Calendar.getInstance()
        }
    }
}
