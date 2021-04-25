package com.stupidtree.hita.theta.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.angcyo.widget.span.DslDrawableSpan
import com.angcyo.widget.span.SpanClickMethod
import com.stupidtree.hita.theta.R
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity
import java.util.regex.Pattern

class EmoticonsTextView2 : AppCompatTextView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }
    val mInflater: LayoutInflater = LayoutInflater.from(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    init {
        SpanClickMethod.install(this)
    }

    override fun setText(text: CharSequence, type: BufferType) {
        if (text.isNotEmpty()) {
            super.setText(replace(text.toString()), type)
        } else {
            super.setText(text, type)
        }
    }

    private fun replace(text: String): CharSequence {
        return try {
            val spannableString = SpannableString(text)
            var start = 0
            val pattern = EmoticonsTextView.buildPattern()
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val faceText = matcher.group()
                val key = faceText.substring(faceText.indexOf("[u") + 2, faceText.indexOf("//@"))
                val username = faceText.substring(faceText.indexOf("//@") + 2, faceText.indexOf("$]"))
                val startIndex = text.indexOf(faceText, start)
                val endIndex = startIndex + faceText.length

                val span = DslDrawableSpan()
                span.showText = username
                span.paddingLeft = ImageUtils.dp2px(context, 6f)
                span.paddingRight = ImageUtils.dp2px(context, 6f)
                span.paddingTop = ImageUtils.dp2px(context, 2f)
                span.paddingBottom = ImageUtils.dp2px(context, 2f)
                span.marginLeft = ImageUtils.dp2px(context, 4f)
                span.marginRight = ImageUtils.dp2px(context, 4f)
                span.textSize = ImageUtils.dp2px(context, 12f).toFloat()
                span.textPaint.typeface = Typeface.DEFAULT_BOLD
                span.textColor = (context as BaseActivity<*, *>).getColorPrimary()
                span.spanClickAction = object : (View, DslDrawableSpan) -> Unit {
                    override fun invoke(view: View, span: DslDrawableSpan) {
                        val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
                        val i = Intent(context, c)
                        i.putExtra("id", key)
                        context.startActivity(i)
                    }
                }
                span.backgroundDrawable = ContextCompat.getDrawable(context,R.drawable.element_rounded_bar_primary_light)
                spannableString.setSpan(
                    span, startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                start = endIndex - 1
            }
            spannableString
        } catch (e: Exception) {
            text
        }
    }

}