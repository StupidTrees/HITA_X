package com.stupidtree.hita.theta.ui.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.angcyo.widget.span.DslDrawableSpan
import com.angcyo.widget.span.SpanClickMethod
import com.stupidtree.hita.theta.R
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.stupiduser.util.ImageUtils.dp2px
import com.stupidtree.style.base.BaseActivity

class EmoticonsEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun setText(text: CharSequence, type: BufferType) {
        if (text.isNotEmpty()) {
            super.setText(replace(text.toString()), type)
        } else {
            super.setText(text, type)
        }
    }

    override fun append(text: CharSequence, start: Int, end: Int) {
        if (text.isNotEmpty()) {
            super.append(replace(text.toString()), start, end)
        } else {
            super.append(text, start, end)
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
                val username =
                    faceText.substring(faceText.indexOf("//") + 2, faceText.indexOf("$]"))
                val startIndex = text.indexOf(faceText, start)
                val endIndex = startIndex + faceText.length
                val view = mInflater.inflate(R.layout.article_user_marker, null)
                view.findViewById<TextView>(R.id.text)?.text = username
                val span = MarkerViewSpan(view)
                if (startIndex >= 0) spannableString.setSpan(
                    span, startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                start = endIndex - 1
            }
            spannableString
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }
}

