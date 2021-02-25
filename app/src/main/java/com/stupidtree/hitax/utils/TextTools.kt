package com.stupidtree.hitax.utils

import android.content.Context
import com.stupidtree.hitax.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * 此类整合了一些文本处理有关的函数
 */
object TextTools {
    fun isUsernameValid(username: String?): Boolean {
        if (!username.isNullOrBlank()) {
            return username.length > 3
        }
        return false
    }

    fun isPasswordValid(password: String?): Boolean {
        if (!password.isNullOrBlank()) {
            return password.length >= 8
        }
        return false
    }
    fun containsNumber(s: String?): Boolean {
        if (s != null) {
            return s.contains("1") || s.contains("2") || s.contains("3") || s.contains("4") || s.contains("5") || s.contains("6") ||
                    s.contains("7") || s.contains("8") || s.contains("9") || s.contains("0")
        }
        return false
    }
    fun isNumber(x: String?): Boolean {
        if (x == null || x.isEmpty()) return false
        for (i in x.indices) {
            if (x[i] !in '0'..'9') {
                return false
            }
        }
        return true
    }
    fun getNormalDateText(context: Context, calendar: Calendar):String {
        val sdf_year =
            SimpleDateFormat(context.getString(R.string.date_format_1), Locale.getDefault())
        return sdf_year.format(calendar.time)
    }

    /**
     * 获得聊天列表的日期字符串（模仿微信）
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 字符串
     */
    fun getConversationTimeText(context: Context, timestamp: Timestamp?): String {
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp?.time ?: -1
        val now = Calendar.getInstance()
        val sdf_year =
            SimpleDateFormat(context.getString(R.string.date_format_1), Locale.getDefault())
        val sdf_month =
            SimpleDateFormat(context.getString(R.string.date_format_2), Locale.getDefault())
        val sdf_am_hour =
            SimpleDateFormat(context.getString(R.string.date_format_am), Locale.getDefault())
        val sdf_pm_hour =
            SimpleDateFormat(context.getString(R.string.date_format_pm), Locale.getDefault())
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

    /**
     * 获得聊天窗口内的时间戳字符串（模仿QQ）
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 字符串
     */
    fun getChatTimeText(context: Context, timestamp: Timestamp?): String {
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp?.time ?: -1
        val now = Calendar.getInstance()
        val sdf_year =
            SimpleDateFormat(context.getString(R.string.date_format_1), Locale.getDefault())
        val sdf_month =
            SimpleDateFormat(context.getString(R.string.date_format_2), Locale.getDefault())
        val sdf_am_hour =
            SimpleDateFormat(context.getString(R.string.date_format_am), Locale.getDefault())
        val sdf_pm_hour =
            SimpleDateFormat(context.getString(R.string.date_format_pm), Locale.getDefault())
        val yesterday = getDateAddedCalendar(now, -1)
        val theDayBeforeYesterday = getDateAddedCalendar(now, -2)
        return if (isSameDay(time, now)) { //今天
            if (time[Calendar.AM_PM] == Calendar.AM) {
                sdf_am_hour.format(time.time)
            } else {
                sdf_pm_hour.format(time.time)
            }
        } else if (isSameDay(time, yesterday) || isSameDay(time, theDayBeforeYesterday)) { //昨天、前天
            val prefix = if (isSameDay(
                            time,
                            yesterday
                    )
            ) context.getString(R.string.yesterday) else context.getString(R.string.the_day_before_yesterday)
            if (time[Calendar.AM_PM] == Calendar.AM) {
                context.getString(
                        R.string.date_format_with_prefix,
                        prefix,
                        sdf_am_hour.format(time.time)
                )
            } else {
                context.getString(
                        R.string.date_format_with_prefix,
                        prefix,
                        sdf_pm_hour.format(time.time)
                )
            }
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