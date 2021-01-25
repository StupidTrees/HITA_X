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
}