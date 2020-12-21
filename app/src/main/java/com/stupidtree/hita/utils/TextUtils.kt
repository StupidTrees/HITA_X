package com.stupidtree.hita.utils

import android.content.Context
import android.text.TextUtils
import com.stupidtree.hita.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
/**
 * 此类整合了一些文本处理有关的函数
 */
object TextUtils {
    fun isEmpty(text: CharSequence?): Boolean {
        return TextUtils.isEmpty(text)
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
        } else if (isSameDay(time, yesterday) || isSameDay(time, theDayBeforeYesterday)) { //昨天、前天
            val prefix = if (isSameDay(time, yesterday)) context.getString(R.string.yesterday) else context.getString(R.string.the_day_before_yesterday)
            if (time[Calendar.AM_PM] == Calendar.AM) {
                context.getString(R.string.date_format_with_prefix, prefix, sdf_am_hour.format(time.time))
            } else {
                context.getString(R.string.date_format_with_prefix, prefix, sdf_pm_hour.format(time.time))
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