package com.stupidtree.hita.data

import androidx.room.TypeConverter
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.Subject
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
    fun eventTypeToInt(eventType: EventItem.TYPE): Int {
        return eventType.ordinal
    }

    @JvmStatic
    @TypeConverter
    fun stringToEventType(string: String): EventItem.TYPE {
        return EventItem.TYPE.valueOf(string)
    }

    @JvmStatic
    @TypeConverter
    fun subjectTypeToInt(subjectType: Subject.TYPE): Int {
        return subjectType.ordinal
    }

    @JvmStatic
    @TypeConverter
    fun stringToSubjectType(string: String): Subject.TYPE {
        return Subject.TYPE.valueOf(string)
    }



}