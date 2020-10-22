package com.tisza.esemenynaptar.database

import android.content.*
import android.os.*
import androidx.room.*
import androidx.sqlite.db.*
import java.io.*

@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(CategoryConverter::class)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao?

    companion object {
        private const val NAME = "event.db"
        private const val NAME_SRC = "event_src.db"
        var instance: EventDatabase? = null
            private set
        private var tempDatabase: EventDatabase? = null

        @Synchronized
        fun init(context: Context, onReady: Runnable) {
            tempDatabase = Room.databaseBuilder(context.applicationContext, EventDatabase::class.java, NAME).addCallback(object : Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("ATTACH DATABASE ? AS event_src;", arrayOf<Any?>(copyAssetDatabaseToData(context)))
                    db.execSQL("DROP TABLE IF EXISTS event_old;")
                    db.execSQL("CREATE TABLE event_old AS SELECT * FROM event;")
                    db.execSQL("DELETE FROM event;")
                    db.execSQL("INSERT INTO event SELECT event_src.event.year, event_src.event.month, event_src.event.day, event_src.event.category, event_src.event.text, ifnull(event_old.isLiked, 0) "
                            + "FROM event_src.event LEFT JOIN event_old "
                            + "ON event_src.event.year = event_old.year AND event_src.event.month = event_old.month AND event_src.event.day = event_old.day AND event_src.event.category = event_old.category;")
                    db.execSQL("DROP TABLE IF EXISTS event_old;")
                    Handler(context.mainLooper).post {
                        tempDatabase!!.close()
                        instance = Room.databaseBuilder(context.applicationContext, EventDatabase::class.java, NAME).build()
                        onReady.run()
                    }
                    super.onOpen(db)
                }
            }).build()
            object : AsyncTask<Void?, Void?, Void?>() {
                protected override fun doInBackground(vararg voids: Void): Void? {
                    try {
                        tempDatabase!!.query("SELECT 0", null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }
            }.execute()
        }

        private fun copyAssetDatabaseToData(context: Context): String? {
            val dbPath = context.getDatabasePath(NAME_SRC)
            dbPath.parentFile.mkdirs()
            try {
                val input = context.assets.open(NAME)
                val output: OutputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(8192)
                var length: Int
                while (input.read(buffer, 0, 8192).also { length = it } > 0) output.write(buffer, 0, length)
                output.close()
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return dbPath.absolutePath
        }

    }
}
