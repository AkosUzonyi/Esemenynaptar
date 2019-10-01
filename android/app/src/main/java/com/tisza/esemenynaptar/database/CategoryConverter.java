package com.tisza.esemenynaptar.database;

import androidx.room.*;
import com.tisza.esemenynaptar.*;

public class CategoryConverter
{
	@TypeConverter
	public static Category categoryFromString(String str)
	{
		return Category.fromStringID(str);
	}

	@TypeConverter
	public static String categoryToString(Category category)
	{
		return category == null ? null : category.getStringID();
	}

}
