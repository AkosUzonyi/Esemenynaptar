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
	private boolean isLiked;

	public Event(int year, int month, int day, Category category, String text, boolean isLiked)
	{
		this.year = year;
		this.month = month;
		this.day = day;
		this.category = category;
		this.text = text;
		this.isLiked = isLiked;
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

	public boolean isLiked()
	{
		return isLiked;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public void setMonth(int month)
	{
		this.month = month;
	}

	public void setDay(int day)
	{
		this.day = day;
	}

	public void setCategory(@NonNull Category category)
	{
		this.category = category;
	}

	public void setText(@NonNull String text)
	{
		this.text = text;
	}

	public void setLiked(boolean liked)
	{
		isLiked = liked;
	}
}
