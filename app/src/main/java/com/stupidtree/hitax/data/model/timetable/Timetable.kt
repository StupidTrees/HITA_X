package com.stupidtree.hitax.data.model.timetable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stupidtree.hitax.ui.main.timetable.TimetableFragment.Companion.WEEK_MILLS
import java.sql.Timestamp
import java.util.*
import kotlin.math.roundToInt

@Entity(tableName = "timetable")
class Timetable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name //课表名称
            : String? = null
    var code //适配教务的课表code
            : String? = null
    var startTime //开始时间
            : Timestamp = Timestamp(0)
    var endTime //结束时间
            : Timestamp = Timestamp(0)
    var createdAt //创建时间
            : Timestamp = Timestamp(System.currentTimeMillis())
    var scheduleStructure: List<TimePeriodInDay> = getDefaultTimeStructure()//时间表结构


    /**
     * 获取某时间戳所对应的周数 =
     */
    fun getWeekNumber(ts: Long): Int {
        val c = Calendar.getInstance()
        c.timeInMillis = ts
        c.firstDayOfWeek = Calendar.MONDAY
        c[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        if (c.timeInMillis > endTime.time) return -1
        val x = ((c.timeInMillis - startTime.time) / WEEK_MILLS.toFloat()).roundToInt()
        return when {
            x < 0 -> {
                -1
            }
            else -> {
                x + 1
            }
        }
    }


    fun getTimestamps(week:Int,dow:Int,start:Int,end:Int): List<Long> {
        val startOfDay:Long = startTime.time + (week-1).toLong()*7*24*60*60*1000 + (dow-1).toLong()*24*60*60*1000
        return listOf(startOfDay+scheduleStructure[start-1].from.toMills(),startOfDay+ scheduleStructure[end-1].to.toMills())
    }

    fun getTimestamps(week:Int,dow:Int,period: TimePeriodInDay): List<Long> {
        val startOfDay:Long = startTime.time + (week-1).toLong()*7*24*60*60*1000 + (dow-1).toLong()*24*60*60*1000
        return listOf(startOfDay+period.from.toMills(),startOfDay+ period.to.toMills())
    }

    fun transformTimePeriod(start:Int,end:Int):TimePeriodInDay{
        return TimePeriodInDay(scheduleStructure[start-1].from,scheduleStructure[end-1].to)
    }

    fun transformCourseNumber(period:TimePeriodInDay):Pair<Int,Int>{
        var start = 0
        var end = 0
        for(i in scheduleStructure.indices){
            if(scheduleStructure[i].contains(period.from)) start = i
            if(scheduleStructure[i].contains(period.to)) end = i
        }
        return Pair(start+1,end+1)
    }
    fun setScheduleStructure(tp: TimePeriodInDay, position: Int) {
        if (position < scheduleStructure.size) {
            scheduleStructure[position].from = tp.from
            scheduleStructure[position].to = tp.to
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Timetable

        if (id != other.id) return false
        if (name != other.name) return false
        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        return result
    }


    fun getDefaultTimeStructure():List<TimePeriodInDay>{
        val res = mutableListOf<TimePeriodInDay>()
        res.add(TimePeriodInDay(TimeInDay(8,30), TimeInDay(9,20)))
        res.add(TimePeriodInDay(TimeInDay(9,25),TimeInDay(10,15)))
        res.add(TimePeriodInDay(TimeInDay(10,30),TimeInDay(11,20)))
        res.add(TimePeriodInDay(TimeInDay(11,25),TimeInDay(12,15)))
        res.add(TimePeriodInDay(TimeInDay(14,0),TimeInDay(14,50)))
        res.add(TimePeriodInDay(TimeInDay(14,55),TimeInDay(15,45)))
        res.add(TimePeriodInDay(TimeInDay(16,0),TimeInDay(16,50)))
        res.add(TimePeriodInDay(TimeInDay(16,55),TimeInDay(17,45)))
        res.add(TimePeriodInDay(TimeInDay(18,45),TimeInDay(19,35)))
        res.add(TimePeriodInDay(TimeInDay(19,40),TimeInDay(20,30)))
        res.add(TimePeriodInDay(TimeInDay(20,45),TimeInDay(21,35)))
        res.add(TimePeriodInDay(TimeInDay(21,40),TimeInDay(22,30)))
        return res
    }


}