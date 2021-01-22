package com.stupidtree.hita.data.model.timetable

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
    var createdAt //创建时间
            : Timestamp = Timestamp(System.currentTimeMillis())
    var scheduleStructure:List<TimePeriodInDay> = mutableListOf()//时间表结构
}