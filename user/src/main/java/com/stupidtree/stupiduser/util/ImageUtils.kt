package com.stupidtree.stupiduser.util

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.stupidtree.stupiduser.R
import com.stupidtree.stupiduser.data.source.preference.UserPreferenceSource

object ImageUtils {
    fun loadAvatarInto(context: Context, userId: String?, target: ImageView) {
        if (TextUtils.isEmpty(userId)) {
            target.setImageResource(R.drawable.place_holder_avatar)
        } else {
            val glideUrl = GlideUrl(
                "http://hita.store:39999/profile/avatar?userId=" +
                        userId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            Glide.with(context).load(
                glideUrl
           ).apply(RequestOptions.bitmapTransform(CircleCrop())).diskCacheStrategy(
                DiskCacheStrategy.NONE
            ).placeholder(R.drawable.place_holder_avatar).into(target)
        }
    }

    fun loadLocalAvatarInto(context: Context, userId: String?, target: ImageView?) {
        val sign = UserPreferenceSource.getInstance(context).myAvatarGlideSignature
        if (userId.isNullOrEmpty()) {
            target?.setImageResource(R.drawable.place_holder_avatar)
        } else {
            val glideUrl = GlideUrl(
                "http://hita.store:39999/profile/avatar?userId=" +
                        userId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            target?.let {
                Glide.with(context).load(
                    glideUrl
                ).apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .signature(ObjectKey(sign))
                    .placeholder(R.drawable.place_holder_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(it)
            }
            // p.edit().putString("my_avatar","normal").apply();
        }
    }

    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}