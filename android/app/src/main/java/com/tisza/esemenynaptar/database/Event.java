package com.tisza.esemenynaptar.database;

import androidx.annotation.*;
import androidx.room.*;
import com.tisza.esemenynaptar.*;

@Entity(tableName = "event", primaryKeys = {"year", "month", "day", "category"})
public class Event
{
	private int year, month, day;
	@NonNull
	private Category category;
	@NonNull
	private String text;

	public Event(int year, int month, int day, Category category, String text)
	{
		this.year = year;
		this.month = month;
		this.day = day;
		this.category = category;
		this.text = text;
	}

	public int getYear()
	{
		return year;
	}

	public int getMonth()
	{
		return month;
	}

	public int getDay()
	{
		return day;
	}

	public Category getCategory()
	{
		return category;
	}

	public String getText()
	{
		return text;
	}
}
