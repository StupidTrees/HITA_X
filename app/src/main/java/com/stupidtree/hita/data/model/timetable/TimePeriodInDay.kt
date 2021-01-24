package com.stupidtree.hita.data.model.timetable

class TimePeriodInDay(f:TimeInDay,t:TimeInDay) {
    var from:TimeInDay = f
    var to:TimeInDay = t
    override fun toString(): String {
        return "$from-$to"
    }

    fun getLengthInMinutes():Int{
        return from.getDistanceInMinutes(to)
    }


}