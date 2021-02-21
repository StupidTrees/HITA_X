package com.stupidtree.hita.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.stupidtree.hita.data.model.service.UserLocal
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
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
    fun subjectTypeToInt(subjectType: TermSubject.TYPE): Int {
        return subjectType.ordinal
    }

    @JvmStatic
    @TypeConverter
    fun genderToString(date: UserLocal.GENDER): String {
        return date.name
    }

    @JvmStatic
    @TypeConverter
    fun stringToGender(str:String): UserLocal.GENDER {
        return UserLocal.GENDER.valueOf(str)
    }

    @JvmStatic
    @TypeConverter
    fun stringToSubjectType(string: String): TermSubject.TYPE? {
        return TermSubject.TYPE.valueOf(string)
    }


    @JvmStatic
    @TypeConverter
    fun timePeriodListToString(l:List<TimePeriodInDay>):String{
        return Gson().toJson(l)
    }
    @JvmStatic
    @TypeConverter
    fun stringToTimePeriodList(string: String): List<TimePeriodInDay> {
        val res = mutableListOf<TimePeriodInDay>()
        val g = Gson()
        val list = g.fromJson(string,List::class.java)
        for(e in list){
            res.add(g.fromJson(e.toString(),TimePeriodInDay::class.java))
        }
        return res
    }




}