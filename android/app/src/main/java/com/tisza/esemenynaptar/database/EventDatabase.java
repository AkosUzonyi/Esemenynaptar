package com.tisza.esemenynaptar.database;

import android.content.*;
import android.os.*;
import androidx.annotation.*;
import androidx.room.*;
import androidx.sqlite.db.*;

import java.io.*;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
@TypeConverters({CategoryConverter.class})
public abstract class EventDatabase extends RoomDatabase
{
	private static final String NAME = "event.db";
	private static final String NAME_SRC = "event_src.db";
	private static EventDatabase instance;
	private static EventDatabase tempDatabase;

	public abstract EventDao eventDao();

	public static synchronized void init(Context context, Runnable onReady)
	{
		tempDatabase = Room.databaseBuilder(context.getApplicationContext(), EventDatabase.class, NAME).addCallback(new Callback()
		{
			@Override
			public void onOpen(@NonNull SupportSQLiteDatabase db)
			{
				db.execSQL("ATTACH DATABASE ? AS event_src;", new Object[]{copyAssetDatabaseToData(context)});
				db.execSQL("DROP TABLE IF EXISTS event_old;");
				db.execSQL("CREATE TABLE event_old AS SELECT * FROM event;");
				db.execSQL("DELETE FROM event;");
				db.execSQL("INSERT INTO event SELECT event_src.event.year, event_src.event.month, event_src.event.day, event_src.event.category, event_src.event.text, ifnull(event_old.isLiked, 0) "
						+ "FROM event_src.event LEFT JOIN event_old "
						+ "ON event_src.event.year = event_old.year AND event_src.event.month = event_old.month AND event_src.event.day = event_old.day AND event_src.event.category = event_old.category;");
				db.execSQL("DROP TABLE IF EXISTS event_old;");

				new Handler(context.getMainLooper()).post(() ->
				{
					tempDatabase.close();
					instance = Room.databaseBuilder(context.getApplicationContext(), EventDatabase.class, NAME).build();
					onReady.run();
				});
				super.onOpen(db);
			}
		}).build();

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... voids)
			{
				try
				{
					tempDatabase.query("SELECT 0", null);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	private static String copyAssetDatabaseToData(Context context)
	{
		File dbPath = context.getDatabasePath(NAME_SRC);
		dbPath.getParentFile().mkdirs();

		try
		{
			final InputStream input = context.getAssets().open(NAME);
			final OutputStream output = new FileOutputStream(dbPath);

			byte[] buffer = new byte[8192];
			int length;

			while ((length = input.read(buffer, 0, 8192)) > 0)
				output.write(buffer, 0, length);

			output.close();
			input.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

		return dbPath.getAbsolutePath();
	}

	public static EventDatabase getInstance()
	{
		return instance;
	}
}
