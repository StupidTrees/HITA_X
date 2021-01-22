package com.stupidtree.hita.data.model.timetable

import java.util.*

class TimeInDay {
    var hour:Int
    var minute:Int
    constructor(h: Int, m: Int){
        this.hour = h
        this.minute = m
    }

   constructor(h: Calendar){
        this.hour = h.get(Calendar.HOUR_OF_DAY)
        this.minute = h.get(Calendar.MINUTE)
    }

    override fun toString(): String {
        if(minute==0) return "$hour:00"
        return "$hour:$minute"
    }

    operator fun compareTo(o: Any): Int {
        return if (hour == (o as TimeInDay).hour) minute - o.minute else hour - o.hour
    }
}