package com.stupidtree.hita.data.model.timetable

import java.sql.Timestamp
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
    constructor(h: Timestamp){
        val c = Calendar.getInstance()
        c.timeInMillis = h.time
        this.hour = c.get(Calendar.HOUR_OF_DAY)
        this.minute = c.get(Calendar.MINUTE)
    }
    override fun toString(): String {
        if(minute==0) return "$hour:00"
        return "$hour:$minute"
    }

    operator fun compareTo(o: Any): Int {
        return if (hour == (o as TimeInDay).hour) minute - o.minute else hour - o.hour
    }

    fun before(timeInDay: TimeInDay):Boolean{
        return compareTo(timeInDay)<0
    }
    fun before(timestamp: Timestamp):Boolean{
        val c = Calendar.getInstance()
        c.timeInMillis = timestamp.time
        return if(c[Calendar.HOUR_OF_DAY]==hour){
            c[Calendar.MINUTE]>minute
        }else{
            c[Calendar.HOUR_OF_DAY]>hour
        }
    }
    fun getDistanceInMinutes(ei: TimeInDay):Int{
        val temp1 = Calendar.getInstance()
        temp1[Calendar.HOUR_OF_DAY] = hour
        temp1[Calendar.MINUTE] = minute
        val ts1 = temp1.timeInMillis
        temp1[Calendar.HOUR_OF_DAY] = ei.hour
        temp1[Calendar.MINUTE] = ei.minute
        val ts2 = temp1.timeInMillis
        return ((ts2-ts1) / (1000*60)).toInt()
    }

    fun getDistanceInMinutes(ts:Long):Int{
        val temp1 = Calendar.getInstance()
        temp1.timeInMillis = ts
        temp1[Calendar.HOUR_OF_DAY] = hour
        temp1[Calendar.MINUTE] = minute
        val temp2 = Calendar.getInstance()
        temp2.timeInMillis = ts
        return ((temp2.timeInMillis - temp1.timeInMillis) / (1000*60)).toInt()
    }
}