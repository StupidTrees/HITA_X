package com.stupidtree.hitax.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.ui.main.timetable.TimetableFragment.Companion.WEEK_MILLS
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object TimeTools {

    enum class SEASON {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    /**
     * 获得某时间所在的周一0点0分
     */
    fun getMonday(ts: Long): Calendar {
        val res = Calendar.getInstance()
        res.timeInMillis = ts
        res.firstDayOfWeek = Calendar.MONDAY
        res[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        res[Calendar.HOUR_OF_DAY] = 0
        res[Calendar.MINUTE] = 0
        return res
    }

    /**
     * 获取季节（课表意义）
     */
    fun getSeason(ts: Long): SEASON {
        val c = Calendar.getInstance()
        c.timeInMillis = ts
        return when (c[Calendar.MONTH] + 1) {
            in 1..4 -> return SEASON.SPRING
            in 5..7 -> return SEASON.SUMMER
            in 8..10 -> return SEASON.AUTUMN
            in 11..12 -> return SEASON.WINTER
            else -> SEASON.WINTER
        }
    }


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
    fun getDow(ts: Long): Int {
        val c = Calendar.getInstance()
        c.timeInMillis = ts
        c.firstDayOfWeek = Calendar.MONDAY
        return if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            7
        } else {
            c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }


    fun printDate(date: Long?): String {
        val c = Calendar.getInstance()
        if (date != null) {
            c.timeInMillis = date
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
    }

    fun printTime(date: Long?): String {
        val c = Calendar.getInstance()
        if (date != null) {
            c.timeInMillis = date
        }
        return SimpleDateFormat("hh:mm", Locale.getDefault()).format(c.time)
    }

    fun getDateAtWOT(startDate: Calendar, WeekOfTerm: Int, DOW: Int): Calendar {
        val temp = Calendar.getInstance()
        val daysToPlus = (WeekOfTerm - 1) * 7 + (DOW - 1)
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
    fun isSameWeekWithStartDate(calendar1: Calendar, time: Long): Boolean {
        val end = calendar1.timeInMillis + WEEK_MILLS
        val res = time >= calendar1.timeInMillis && time < end
        Log.e(
            "sameWeek",
            "${printDate(calendar1.timeInMillis)},${printDate(time)}"
        )

        return res
    }

    fun getDateString(context: Context, ts:Long, simplified: Boolean, TTYMode: Int): String{
        val tc = Calendar.getInstance()
        tc.timeInMillis = ts
        return getDateString(context,tc,simplified,TTYMode)
    }

    /**
     * simplified:简化April为Apr
     * TTYMode:0显示原信息,1替换整个为今天，2为末尾加(今天/明天)
     */
    fun getDateString(context: Context, c: Calendar, simplified: Boolean, TTYMode: Int): String {
        val tag: String = getTTTag(context, c)
        var following = ""
        when (TTYMode) {
            TTY_NONE -> {
            }
            TTY_REPLACE -> if (!TextUtils.isEmpty(tag)) return tag
            TTY_FOLLOWING -> if (!TextUtils.isEmpty(tag)) {
                following = context.getString(R.string.brackets_content, tag)
            }
            TTY_WK_FOLLOWING -> {
                following = " " +context.resources.getStringArray(R.array.dow2)[getDow(c.timeInMillis) - 1]
                        }
            TTY_WK2_FOLLOWING -> {
                following = " " +context.resources.getStringArray(R.array.dow1)[getDow(c.timeInMillis) - 1]
            }
        }
        return SimpleDateFormat(
            context.getString(if (simplified) R.string.date_format_2 else R.string.date_format_3),
            Locale.getDefault()
        )
            .format(c.time) + following
    }


//    /**
//     * simplified:简化Thursday为Thu
//     * TTYMode:0显示原信息,1替换整个为今天，2为末尾加(今天/明天)
//     * TTY=n+3可将周数替换为这周、下周
//     * TTY=n+4
//     */
//    fun getWeekDowString(ei: EventItem, simplified: Boolean, TTYMode: Int): String? {
//        return getWeekDowString(ei.getWeek(), ei.getDOW(), simplified, TTYMode)
//    }
//    fun getWeekDowString(
//        context: Context,
//        week: Int,
//        dow: Int,
//        simplified: Boolean,
//        TTYMode: Int
//    ): String? {
//        val then: Calendar = c.getDateAt(week, dow, HTime(12, 0))
//        var rawText = SimpleDateFormat(
//            context.getString(
//                if (simplified) R.string.date_format_2_simplified else R.string.date_format_2,
//                week
//            ), Locale.getDefault()
//        ).format(then.time)
//        val tag: String = getTTTag(then)
//        val wkTag: String =
//            getWKTag(tc.getThisWeekOfTerm(), week)
//        if (TTYMode and TTY_WK_FOLLOWING > 0) {
//            if (!TextUtils.isEmpty(wkTag)) {
//                rawText = SimpleDateFormat(
//                    context.getString(
//                        if (simplified) R.string.date_format_2_simplified_wk_followed else R.string.date_format_2_wk_followed,
//                        week,
//                        wkTag
//                    ), Locale.getDefault()
//                ).format(then.time)
//            }
//        } else if (TTYMode and TTY_WK_REPLACE > 0) {
//            if (!TextUtils.isEmpty(wkTag)) {
//                rawText = SimpleDateFormat(
//                    context.getString(
//                        if (simplified) R.string.date_format_2_simplified_wk_replaced else R.string.date_format_2_wk_replaced,
//                        wkTag
//                    ), Locale.getDefault()
//                ).format(then.time)
//            }
//        }
//        if (TTYMode and TTY_REPLACE > 0) {
//            if (!TextUtils.isEmpty(tag)) rawText = tag
//        } else if (TTYMode and TTY_FOLLOWING > 0) {
//            if (!TextUtils.isEmpty(tag)) {
//                rawText += context.getString(R.string.brackets_content, tag)
//            }
//        }
//        return rawText
//    }

    /**
     *  获得当前是第几节课
     *  返回结果为节数*10（+5）.+5表示课间
     */
    fun getCurrentScheduleNumber(it: List<TimePeriodInDay>): Int {
        var current = 0
        val ts = System.currentTimeMillis()
        val tp = TimeInDay(Calendar.getInstance())
        for (i in it.indices) {
            when {
                it[i].contains(ts) -> {
                    current = 10 * (i + 1)
                    break
                }
                tp.before(it[i].from) -> {
                    current += 5
                    break
                }
                tp.after(it[i].to) -> current += 10
            }
        }
        return current
    }

    private fun getWKTag(context: Context, currentWk: Int, week: Int): String {
        return when (currentWk) {
            week -> context.getString(R.string.name_this_week)
            week - 1 -> context.getString(
                R.string.name_next_week
            )
            week + 1 -> context.getString(R.string.name_last_week)
            else -> ""
        }
    }

    private fun getTTTag(context: Context, then: Calendar): String {
        val now = Calendar.getInstance()
        val tom = now.clone() as Calendar
        tom.add(Calendar.DATE, 1)
        val yest = now.clone() as Calendar
        yest.add(Calendar.DATE, -1)
        val tat = now.clone() as Calendar
        tat.add(Calendar.DATE, 2)
        val tby = now.clone() as Calendar
        tby.add(Calendar.DATE, -2)
        return when {
            isSameDay(
                now,
                then
            ) -> context.getString(R.string.today)
            isSameDay(
                tom,
                then
            ) -> context.getString(R.string.tomorrow)
            isSameDay(
                yest,
                then
            ) -> context.getString(R.string.yesterday)
            isSameDay(
                tat,
                then
            ) -> context.getString(R.string.tda_tomorrow)
            isSameDay(
                tby,
                then
            ) -> context.getString(R.string.tdb_yesterday)
            else -> ""
        }
    }


    /**
     * 获取小时（东八区）
     */
    fun getHour(date: Long): Int {
        return ((8 + ((date % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)))%24).toInt()
    }

    fun getMinute(date: Long): Int {
        return ((date % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    }

    fun isSameDay(tsA:Long, tsB:Long): Boolean {
        val calA = Calendar.getInstance()
        calA.timeInMillis = tsA
        val calB = Calendar.getInstance()
        calB.timeInMillis = tsB
        return isSameDay(calA,calB)
       }

    fun isSameDay(calDateA: Calendar, calDateB: Calendar): Boolean {
        return calDateA[Calendar.YEAR] == calDateB[Calendar.YEAR] && calDateA[Calendar.MONTH] == calDateB[Calendar.MONTH] && calDateA[Calendar.DAY_OF_MONTH] == calDateB[Calendar.DAY_OF_MONTH]
    }


    const val TTY_NONE = 0
    const val TTY_REPLACE = 1 shl 1
    const val TTY_FOLLOWING = 1 shl 2
    const val TTY_WK_REPLACE = 1 shl 3
    const val TTY_WK_FOLLOWING = 1 shl 4
    const val TTY_WK2_FOLLOWING = 1 shl 5


}