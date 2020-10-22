package com.tisza.esemenynaptar.database

import androidx.room.*
import com.tisza.esemenynaptar.*

@Entity(tableName = "event", primaryKeys = ["year", "month", "day", "category"])
class Event(var year: Int, var month: Int, var day: Int, var category: Category, var text: String, var isLiked: Boolean) 
