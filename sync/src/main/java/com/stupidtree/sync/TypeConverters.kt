package com.stupidtree.sync

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.stupidtree.sync.data.model.History
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
    fun actionToString(action: History.ACTION): String {
        return action.name
    }


    @JvmStatic
    @TypeConverter
    fun stringToAction(string: String): History.ACTION {
        return History.ACTION.valueOf(string)
    }

    @JvmStatic
    @TypeConverter
    fun stringToList(str: String): List<String> {
        val res = mutableListOf<String>()
        try {
            for (o in Gson().fromJson(str, List::class.java)) {
                res.add(o.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    @JvmStatic
    @TypeConverter
    fun listToString(list: List<String>): String {
        return Gson().toJson(list)
    }

}