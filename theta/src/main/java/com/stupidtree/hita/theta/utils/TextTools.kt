package com.stupidtree.hita.theta.utils

import android.content.Context
import com.stupidtree.hita.theta.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object TextTools {


    fun getArticleTimeText(context: Context, timestamp: Timestamp?): String {
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp?.time ?: -1
        val now = Calendar.getInstance()
        val sdf_year = SimpleDateFormat(context.getString(R.string.date_format_1), Locale.getDefault())
        val sdf_month = SimpleDateFormat(context.getString(R.string.date_format_2), Locale.getDefault())
        val sdf_am_hour = SimpleDateFormat(context.getString(R.string.date_format_am), Locale.getDefault())
        val sdf_pm_hour = SimpleDateFormat(context.getString(R.string.date_format_pm), Locale.getDefault())
        val yesterday = getDateAddedCalendar(now, -1)
        val theDayBeforeYesterday = getDateAddedCalendar(now, -2)
        return if (isSameDay(time, now)) { //今天
            if (time[Calendar.AM_PM] == Calendar.AM) {
                sdf_am_hour.format(time.time)
            } else {
                sdf_pm_hour.format(time.time)
            }
        } else if (isSameDay(time, yesterday)) { //昨天
            context.getString(R.string.yesterday)
        } else if (isSameDay(time, theDayBeforeYesterday)) {
            context.getString(R.string.the_day_before_yesterday) //前天
        } else if (time[Calendar.YEAR] != now[Calendar.YEAR]) { //不是同一年
            sdf_year.format(time.time)
        } else if (time[Calendar.MONTH] != now[Calendar.MONTH]) { //不是同个月
            sdf_month.format(time.time)
        } else {
            sdf_month.format(time.time)
        }
    }

    private fun getDateAddedCalendar(c: Calendar, day: Int): Calendar {
        val n = c.clone() as Calendar
        n.add(Calendar.DATE, day)
        return n
    }
    private fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a[Calendar.YEAR] == b[Calendar.YEAR] && a[Calendar.MONTH] == b[Calendar.MONTH] && a[Calendar.DAY_OF_MONTH] == b[Calendar.DAY_OF_MONTH]
    }
}