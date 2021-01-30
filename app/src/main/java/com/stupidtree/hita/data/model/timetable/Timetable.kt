package com.stupidtree.hita.data.model.timetable

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

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
    var scheduleStructure: List<TimePeriodInDay> = mutableListOf()//时间表结构


    /**
     * 获取某时间戳所对应的周数 =
     */
    fun getWeekNumber(ts: Long): Int {
        if (ts > endTime.time) return -1
        val x = ((ts - startTime.time) / (1000 * 60 * 60 * 24 * 7.toFloat()))
        return when {
            x < 0 -> {
                -1
            }
            else -> {
                x.toInt() + 1
            }
        }
    }
}