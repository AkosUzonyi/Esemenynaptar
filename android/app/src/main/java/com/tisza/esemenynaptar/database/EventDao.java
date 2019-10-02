package com.tisza.esemenynaptar.database;

import androidx.lifecycle.*;
import androidx.room.*;
import androidx.sqlite.db.*;

import java.util.*;

@Dao
public interface EventDao
{
	@Query("SELECT * FROM event WHERE year = :year AND month = :month AND day = :day")
	LiveData<List<Event>> getEventsForDate(int year, int month, int day);

	default LiveData<List<Event>> getEventsForDate(Calendar calendar)
	{
		return getEventsForDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	@Update
	void updateEvent(Event event);
}
