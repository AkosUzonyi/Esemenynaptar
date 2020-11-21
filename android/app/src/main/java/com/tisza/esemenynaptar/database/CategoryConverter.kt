package com.tisza.esemenynaptar.database

import androidx.room.*
import com.tisza.esemenynaptar.*

object CategoryConverter {
    @JvmStatic
	@TypeConverter
    fun categoryFromString(str: String) = Category.values().first { it.stringID == str }

    @JvmStatic
	@TypeConverter
    fun categoryToString(category: Category) = category.stringID
}
