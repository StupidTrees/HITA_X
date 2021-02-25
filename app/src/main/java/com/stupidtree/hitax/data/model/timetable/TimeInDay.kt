package com.stupidtree.hitax.data.model.timetable

import com.stupidtree.hitax.utils.TimeTools
import java.sql.Timestamp
import java.util.*

class TimeInDay {
    var hour: Int
    var minute: Int

    constructor(h: Int, m: Int) {
        this.hour = h
        this.minute = m
    }

    constructor(h: Calendar) {
        this.hour = h.get(Calendar.HOUR_OF_DAY)
        this.minute = h.get(Calendar.MINUTE)
    }

    constructor(h: Timestamp) {
        val c = Calendar.getInstance()
        c.timeInMillis = h.time
        this.hour = c.get(Calendar.HOUR_OF_DAY)
        this.minute = c.get(Calendar.MINUTE)
    }

    override fun toString(): String {
        if (minute == 0) return "$hour:00"
        return "$hour:$minute"
    }

    operator fun compareTo(o: Any): Int {
        return if (hour == (o as TimeInDay).hour) minute - o.minute else hour - o.hour
    }

    fun before(timeInDay: TimeInDay): Boolean {
        return compareTo(timeInDay) < 0
    }


    fun getDistanceInMinutes(ei: TimeInDay): Int {
        return getDistanceInMinutes(ei.hour, ei.minute)
    }

    fun getDistanceInMinutes(ts: Long): Int {
        val m = TimeTools.getMinute(ts)
        val h = TimeTools.getHour(ts)
        return getDistanceInMinutes(h, m)
    }

    fun getDistanceInMinutes(hour: Int, minute: Int): Int {
        return (hour - this.hour) * 60 + minute - this.minute
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeInDay

        if (hour != other.hour) return false
        if (minute != other.minute) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hour
        result = 31 * result + minute
        return result
    }


}