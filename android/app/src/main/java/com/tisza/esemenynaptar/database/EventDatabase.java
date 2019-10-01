package com.tisza.esemenynaptar.database;

import android.content.*;
import androidx.annotation.*;
import androidx.room.*;
import androidx.room.migration.*;
import androidx.sqlite.db.*;

import java.io.*;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
@TypeConverters({CategoryConverter.class})
public abstract class EventDatabase extends RoomDatabase
{
	private static final String NAME = "event.db";
	private static EventDatabase instance;

	public abstract EventDao eventDao();

	public static synchronized void init(Context context)
	{
		instance = Room.databaseBuilder(context.getApplicationContext(), EventDatabase.class, NAME).addMigrations(INIT_MIGRATION).build();
	}

	private static void copyDatabase(Context context)
	{
		File dbPath = context.getDatabasePath(NAME);
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
		}
	}

	public static EventDatabase getInstance()
	{
		return instance;
	}

	private static Migration INIT_MIGRATION = new Migration(1, 2)
	{
		@Override
		public void migrate(@NonNull SupportSQLiteDatabase database) {}
	};
}
