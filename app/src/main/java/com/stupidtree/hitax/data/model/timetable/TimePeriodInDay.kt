package com.stupidtree.hitax.data.model.timetable

import java.util.*

class TimePeriodInDay(f: TimeInDay, t: TimeInDay) {
    var from: TimeInDay
    var to: TimeInDay
    override fun toString(): String {
        return "$from-$to"
    }

    init {
        from = f.getAdded(0)
        to = t.getAdded(0)
    }

    fun getLengthInMinutes(): Int {
        return from.getDistanceInMinutes(to)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimePeriodInDay

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    fun clone():TimePeriodInDay{
        return TimePeriodInDay(from,to)
    }


    fun contains(ts: Long): Boolean {
        val c = Calendar.getInstance()
        c.timeInMillis = ts
        val t = TimeInDay(c)
        return from.before(t) && t.before(to)
    }

    fun contains(tm:TimeInDay): Boolean {
        return from.before(tm) && tm.before(to) || tm == from || tm == to
    }

    fun before(tm:TimeInDay): Boolean {
        return to.before(tm)
    }
    fun after(tm:TimeInDay):Boolean{
        return from.after(tm)
    }
}