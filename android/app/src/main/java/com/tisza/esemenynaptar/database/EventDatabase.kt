package com.tisza.esemenynaptar.database

import android.content.*
import android.util.*
import androidx.room.*
import androidx.room.migration.*
import androidx.sqlite.db.*
import java.io.*

private const val EVENT_DATABASE_NAME = "event.db"
private const val EVENT_DATABASE_SRC_NAME = "event_src.db"
private const val VERSION = 2

lateinit var eventDatabase: EventDatabase
    private set

@Database(entities = [Event::class], version = VERSION, exportSchema = false)
@TypeConverters(CategoryConverter::class)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

@Synchronized
fun initEventDatabase(context: Context) {
    val migrations = Array(VERSION - 1) { i ->
        UpdateDatabaseFromAssetsMigration(context, i + 1, i + 2)
    }

    eventDatabase = Room.databaseBuilder(context.applicationContext, EventDatabase::class.java, EVENT_DATABASE_NAME)
            .addMigrations(*migrations)
            .addCallback(EventDatabaseCallback(context))
            .build()
}

private class EventDatabaseCallback(val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        updateDatabaseFromAsset(context, db)
    }
}

private class UpdateDatabaseFromAssetsMigration(val context: Context, startVersion: Int, endVersion: Int): Migration(startVersion, endVersion) {
    override fun migrate(db: SupportSQLiteDatabase) {
        updateDatabaseFromAsset(context, db)
    }
}

private fun updateDatabaseFromAsset(context: Context, db: SupportSQLiteDatabase) {
    db.setTransactionSuccessful()
    db.endTransaction()

    db.execSQL("ATTACH DATABASE ? AS event_src;", arrayOf<Any?>(copyAssetDatabaseToData(context)))

    db.beginTransaction()

    db.execSQL("DROP TABLE IF EXISTS event_old;")
    db.execSQL("CREATE TABLE event_old AS SELECT * FROM event;")
    db.execSQL("DELETE FROM event;")
    db.execSQL("INSERT INTO event SELECT event_src.event.year, event_src.event.month, event_src.event.day, event_src.event.category, event_src.event.text, ifnull(event_old.isLiked, 0) "
            + "FROM event_src.event LEFT JOIN event_old "
            + "ON event_src.event.year = event_old.year AND event_src.event.month = event_old.month AND event_src.event.day = event_old.day AND event_src.event.category = event_old.category;")
    db.execSQL("DROP TABLE IF EXISTS event_old;")
}

private fun copyAssetDatabaseToData(context: Context): String? {
    val dbPath = context.getDatabasePath(EVENT_DATABASE_SRC_NAME)
    dbPath.parentFile.mkdirs()
    try {
        val input = context.assets.open(EVENT_DATABASE_NAME)
        val output = FileOutputStream(dbPath)
        val buffer = ByteArray(8192)
        var length: Int
        while (input.read(buffer, 0, 8192).also { length = it } > 0)
            output.write(buffer, 0, length)
        output.close()
        input.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return dbPath.absolutePath
}
