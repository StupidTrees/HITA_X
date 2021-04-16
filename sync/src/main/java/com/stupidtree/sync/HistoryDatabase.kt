package com.stupidtree.sync

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stupidtree.sync.data.source.dao.HistoryDao
import com.stupidtree.sync.data.model.History

@Database(
    entities = [History::class],
    version = 1
)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryDatabase {
            if (INSTANCE == null) {
                synchronized(HistoryDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            HistoryDatabase::class.java, "stupid_sync"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}