package com.stupidtree.hita.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object TimeUtils {
    /**
     * 是否已过去
     */
    fun passed(time: Timestamp): Boolean {
        return time.time < System.currentTimeMillis()
    }

    /**
     * 现在是星期几
     */
    fun currentDOW(): Int {
        return getDow(System.currentTimeMillis())
    }


    /**
     * 星期几
     * 周一为1
     */
    fun getDow(ts:Long):Int{
        val c = Calendar.getInstance()
        c.timeInMillis = ts
        c.firstDayOfWeek = Calendar.MONDAY
        return if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            7
        } else {
            c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }


    fun printDate(date:Long):String{
        val c = Calendar.getInstance()
        c.timeInMillis = date
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
    }


    fun getDateAtWOT(startDate: Calendar,WeekOfTerm: Int, DOW: Int): Calendar {
        val temp = Calendar.getInstance()
        val daysToPlus = (WeekOfTerm - 1) * 7 + (DOW-1)
        temp.timeInMillis = startDate.timeInMillis
        temp.firstDayOfWeek = Calendar.MONDAY
        temp[Calendar.HOUR_OF_DAY] = 0
        temp[Calendar.MINUTE] = 0
        temp[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        temp.add(Calendar.DATE, daysToPlus)
        return temp
    }

    /**
     * 两个日期是否在同周
     * 其中calendar1要求是周一的0点0分
     */
    fun isSameWeekWithStartDate(calendar1: Calendar,time:Long): Boolean {
        val end = calendar1.timeInMillis + 1000*60*60*24*7
        return time>=calendar1.timeInMillis && time<end
    }

    /**
     * 获取小时（东八区）
     */
    fun getHour(date:Long):Int{
        return 8+((date%(1000*60*60*24))/(1000*60*60)).toInt()
    }
    fun getMinute(date:Long):Int{
        return ((date%(1000*60*60))/(1000*60)).toInt()
    }
}