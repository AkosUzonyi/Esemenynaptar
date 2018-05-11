package com.tisza.esemenynaptar;

public class Category
{
	private int id;
	private final String directoryName;
	private final int imageRes;
	private int displayNameRes;

	public Category(int id, String directoryName, int imageRes, int displayNameRes)
	{
		this.id = id;
		this.directoryName = directoryName;
		this.imageRes = imageRes;
		this.displayNameRes = displayNameRes;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getDirectoryName()
	{
		return directoryName;
	}
	
	public int getImageRes()
	{
		return imageRes;
	}
	
	public int getDisplayNameRes()
	{
		return displayNameRes;
	}
}
