package com.stupidtree.hitax.ui.event.add

import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import java.sql.Time
import java.util.*

class CourseTime {
//    var begin:Int = 0
//    var end:Int = 0
//    var startTime:TimeInDay = TimeInDay(0,0)
//    var endTime:TimeInDay = TimeInDay(0,0)
    var period:TimePeriodInDay = TimePeriodInDay(TimeInDay(0,0),TimeInDay(0,0))
    var dow:Int = 1
    var weeks:List<Int> = mutableListOf()
}