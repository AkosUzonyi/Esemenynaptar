package com.tisza.esemenynaptar.database

import androidx.lifecycle.*
import androidx.room.*
import java.util.*

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE year = :year AND month = :month AND day = :day")
    fun getEventsForDate(year: Int, month: Int, day: Int): LiveData<List<Event>>
    fun getEventsForDate(calendar: Calendar): LiveData<List<Event>> {
        return getEventsForDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
    }

    @Update
    fun updateEvent(event: Event?)
}
