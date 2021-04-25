package com.stupidtree.hita.theta.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.stupidtree.hita.theta.ui.create.CornerTransform
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.hita.theta.R

object ImageUtils {

    fun loadArticleImageInto(context: Context, imageId: String?, target: ImageView) {
        var transformation =
            CornerTransform(context, ImageUtils.dp2px(context, 8f).toFloat())

        if (TextUtils.isEmpty(imageId)) {
            target.setImageResource(R.drawable.place_holder_loading)
        } else {
            val glideUrl = GlideUrl(
                "https://hita.store:39999/article/image?imageId=" +
                        imageId, LazyHeaders.Builder().addHeader("device-type", "android").build()
            )
            Glide.with(context).load(
                glideUrl
            ).apply(RequestOptions.bitmapTransform(transformation))
                .placeholder(R.drawable.place_holder_loading).into(target)
        }
    }


    fun getResourceBitmap(context: Context,id:Int): Bitmap {
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


}