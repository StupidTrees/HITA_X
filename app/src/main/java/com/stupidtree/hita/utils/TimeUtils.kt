package com.stupidtree.hita.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
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
        temp[Calendar.HOUR] = 0
        temp[Calendar.MINUTE] = 0
        temp[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        temp.add(Calendar.DATE, daysToPlus)
        temp.add(Calendar.DATE, DOW - 1)
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