package com.stupidtree.hitax.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils.isEmpty
import android.widget.ImageView
import androidx.core.content.ContextCompat


/**
 * 此类封装了加载用户头像的各个方法
 * 以及各种图形函数
 */
object ImageUtils {

    fun getResourceBitmap(context: Context,id:Int):Bitmap{
        val vectorDrawable = ContextCompat.getDrawable(context,id)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }



    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }



}