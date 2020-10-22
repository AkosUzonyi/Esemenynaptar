package com.tisza.esemenynaptar.database

import androidx.room.*
import com.tisza.esemenynaptar.*

object CategoryConverter {
    @JvmStatic
	@TypeConverter
    fun categoryFromString(str: String?): Category? {
        return Category.fromStringID(str!!)
    }

    @JvmStatic
	@TypeConverter
    fun categoryToString(category: Category?): String? {
        return category?.stringID
    }
}
