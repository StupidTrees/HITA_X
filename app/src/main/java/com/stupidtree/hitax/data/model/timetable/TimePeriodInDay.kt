package com.stupidtree.hitax.data.model.timetable

class TimePeriodInDay(f:TimeInDay,t:TimeInDay) {
    var from:TimeInDay = f
    var to:TimeInDay = t
    override fun toString(): String {
        return "$from-$to"
    }

    fun getLengthInMinutes():Int{
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


}