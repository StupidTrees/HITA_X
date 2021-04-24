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
    fun loadAvatarInto(context: Context, imageId: String?, target: ImageView) {
        if (TextUtils.isEmpty(imageId)) {
            target.setImageResource(R.drawable.place_holder_avatar)
        } else {
            val glideUrl = GlideUrl(
                "https://hita.store:39999/profile/avatar?imageId=" +
                       imageId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            Glide.with(context).load(
                glideUrl
           ).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.place_holder_avatar).into(target)
        }
    }

    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}