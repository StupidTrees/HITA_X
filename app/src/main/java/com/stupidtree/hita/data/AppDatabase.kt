package com.stupidtree.hita.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stupidtree.hita.data.model.EventItem
import com.stupidtree.hita.data.model.Subject
import com.stupidtree.hita.data.model.Timetable
import com.stupidtree.hita.data.source.dao.EventItemDao
import com.stupidtree.hita.data.source.dao.SubjectDao
import com.stupidtree.hita.data.source.dao.TimetableDao

@Database(entities = [EventItem::class, Subject::class, Timetable::class], version = 1)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventItemDao():EventItemDao
    abstract fun subjectDao():SubjectDao
    abstract fun timetableDao():TimetableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "hita").build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}