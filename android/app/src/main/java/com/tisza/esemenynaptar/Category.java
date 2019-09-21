package com.tisza.esemenynaptar;

public enum  Category
{
	IRODALOM(0, "irodalom", R.drawable.irodalom, R.string.irodalom),
	TORTENELEM(1, "tortenelem", R.drawable.tortenelem, R.string.tortenelem),
	ZENETORTENET(2, "zenetortenet", R.drawable.zenetortenet, R.string.zenetortenet),
	VIZUALIS_KULTURA(3, "vizualis_kultura", R.drawable.vizualis_kultura, R.string.vizualis_kultura);

	private int id;
	private final String directoryName;
	private final int imageRes;
	private int displayNameRes;

	Category(int id, String directoryName, int imageRes, int displayNameRes)
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
