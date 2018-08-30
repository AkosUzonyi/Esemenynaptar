package com.tisza.esemenynaptar;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v4.view.ViewPager.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener
{
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	public static final String SHARED_PREF = "sp";
	public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
	public static final String NOTIFICATION_TIME = "notification_time";
	public static final String TODAY_EXTRA = "today";
	private static final String SAVED_DATE = "date";

	private ViewPager pager;
	private MyPagerAdapter pagerAdapter;
	private LayoutInflater inflater;
	private EventLoader eventLoader;
	private SharedPreferences sharedPreferences;

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			pagerAdapter.setDate(year, monthOfYear, dayOfMonth);
		}
	}; 

	private TimePickerDialog.OnTimeSetListener nofificationTimeSetListener = new TimePickerDialog.OnTimeSetListener()
	{
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(NOTIFICATION_TIME, hourOfDay * 60 + minute);
			editor.commit();
			DailyReceiver.schedule(MainActivity.this);
		}
	}; 

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		inflater = getLayoutInflater();
		eventLoader = new EventLoader(this);
		sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
		
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setOnPageChangeListener(this);
		pagerAdapter = new MyPagerAdapter(this, pager, eventLoader);
		pager.setAdapter(pagerAdapter);
		Calendar calendar = Calendar.getInstance();
		if (savedInstanceState != null)
			calendar.setTimeInMillis(savedInstanceState.getLong(SAVED_DATE));
		pagerAdapter.setDate(calendar);
		
		DailyReceiver.schedule(this);
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle)
	{
		super.onSaveInstanceState(bundle);
		bundle.putLong(SAVED_DATE, pagerAdapter.getDate().getTimeInMillis());
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(TODAY_EXTRA, false))
		{
			pagerAdapter.setDate(Calendar.getInstance());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		menu.findItem(R.id.enable_notification).setChecked(sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, true));
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem setTimeItem = menu.findItem(R.id.set_notification_time); 
		MenuItem enableNotificationItem = menu.findItem(R.id.enable_notification); 
		setTimeItem.setEnabled(enableNotificationItem.isChecked());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.today:
				pagerAdapter.setDate(Calendar.getInstance());
				return true;
			case R.id.pick_date:
				Calendar date = pagerAdapter.getDate();
				DatePickerDialog dateDialog = new DatePickerDialog(this, dateSetListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
				return true;
			case R.id.enable_notification:
				boolean enable = !item.isChecked();
				item.setChecked(enable);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(NOTIFICATIONS_ENABLED, enable);
				editor.commit();
				supportInvalidateOptionsMenu();
				return true;
			case R.id.set_notification_time:
				int time = sharedPreferences.getInt(NOTIFICATION_TIME, 420);
				TimePickerDialog timeDialog = new TimePickerDialog(this, nofificationTimeSetListener, time / 60, time % 60, true);
				timeDialog.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state)
	{
		pagerAdapter.onPageScrollStateChanged(state);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		pagerAdapter.onPageScrolled(arg0, arg1, arg2);
	}

	@Override
	public void onPageSelected(int page)
	{
		pagerAdapter.onPageSelected(page);
	}
}
