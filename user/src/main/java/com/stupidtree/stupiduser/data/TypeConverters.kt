package com.stupidtree.stupiduser.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.stupidtree.stupiduser.data.model.service.UserLocal
import java.sql.Timestamp

/**
 * ROOM需要使用转换器将时间戳转换为Date
 */
object TypeConverters {
    @JvmStatic
    @TypeConverter
    fun timestampToLong(value: Timestamp?): Long? {
        return value?.time
    }

    @JvmStatic
    @TypeConverter
    fun longToTimestamp(date: Long?): Timestamp? {
        return date?.let { Timestamp(it) }
    }

    @JvmStatic
    @TypeConverter
    fun genderToString(date: UserLocal.GENDER): String {
        return date.name
    }

    @JvmStatic
    @TypeConverter
    fun stringToGender(str: String): UserLocal.GENDER {
        return UserLocal.GENDER.valueOf(str)
    }



}