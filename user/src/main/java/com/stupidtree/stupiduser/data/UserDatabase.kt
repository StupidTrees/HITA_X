package com.stupidtree.stupiduser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stupidtree.stupiduser.data.model.service.UserProfile
import com.stupidtree.stupiduser.data.source.dao.UserProfileDao

@Database(
    entities = [UserProfile::class],
    version = 1
)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): UserDatabase {
            if (INSTANCE == null) {
                synchronized(UserDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java, "stupid_user"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}