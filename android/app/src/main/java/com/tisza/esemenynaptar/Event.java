package com.tisza.esemenynaptar;

public class Event
{
	private final Category category;
	private final String text;

	public Event(Category category, String text)
	{
		this.category = category;
		this.text = text;
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
