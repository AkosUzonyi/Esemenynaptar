package com.tisza.esemenynaptar;

public enum  Category
{
	IRODALOM(0, "irodalom", R.drawable.irodalom, R.string.irodalom),
	TORTENELEM(1, "tortenelem", R.drawable.tortenelem, R.string.tortenelem),
	ZENETORTENET(2, "zenetortenet", R.drawable.zenetortenet, R.string.zenetortenet),
	VIZUALIS_KULTURA(3, "vizualis_kultura", R.drawable.vizualis_kultura, R.string.vizualis_kultura);

	private int id;
	private final String stringID;
	private final int imageRes;
	private int displayNameRes;

	Category(int id, String stringID, int imageRes, int displayNameRes)
	{
		this.id = id;
		this.stringID = stringID;
		this.imageRes = imageRes;
		this.displayNameRes = displayNameRes;
	}

	public int getID()
	{
		return id;
	}

	public String getStringID()
	{
		return stringID;
	}

	public int getImageRes()
	{
		return imageRes;
	}

	public int getDisplayNameRes()
	{
		return displayNameRes;
	}

	public static Category fromStringID(String str)
	{
		for (Category category : values())
			if (category.getStringID().equals(str))
				return category;
		return null;
	}
}
