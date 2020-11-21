package com.tisza.esemenynaptar.database

import androidx.room.*
import com.tisza.esemenynaptar.*
import java.util.*

@Entity(tableName = "event", primaryKeys = ["year", "month", "day", "category"])
class Event(var year: Int, var month: Int, var day: Int, var category: Category, var text: String, var isLiked: Boolean) 

val Event.calendar : Calendar
    get() {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)
        return cal
    }
