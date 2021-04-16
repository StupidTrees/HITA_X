package com.stupidtree.hitax.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils.isEmpty
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.stupidtree.hitax.R
import com.stupidtree.stupiduser.data.source.preference.UserPreferenceSource

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

    fun loadAvatarInto(context: Context, userId: String?, target: ImageView) {
        if (isEmpty(userId)) {
            target.setImageResource(R.drawable.ic_baseline_location_24)
        } else {
            val glideUrl = GlideUrl(
                "http://hita.store:39999/profile/avatar?userId=" +
                        userId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            Glide.with(context).load(
                glideUrl
            ).apply(RequestOptions.bitmapTransform(CircleCrop())).diskCacheStrategy(
                DiskCacheStrategy.NONE
            ).skipMemoryCache(true).placeholder(R.drawable.place_holder_avatar).into(target)
        }
    }

    fun loadLocalAvatarInto(context: Context, userId: String?, target: ImageView) {
        val sign = UserPreferenceSource.getInstance(context).myAvatarGlideSignature
        if (userId.isNullOrEmpty()) {
            target.setImageResource(R.drawable.place_holder_avatar)
        } else {
            val glideUrl = GlideUrl(
                "http://hita.store:39999/profile/avatar?userId=" +
                        userId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            Glide.with(context).load(
                glideUrl
            ).apply(RequestOptions.bitmapTransform(CircleCrop()))
                .signature(ObjectKey(sign))
                .placeholder(R.drawable.place_holder_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(target)
            // p.edit().putString("my_avatar","normal").apply();
        }
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

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


}