package com.stupidtree.hita.theta.ui.widgets

import android.content.Context
import android.content.Intent
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
import com.stupidtree.hita.theta.R
import com.stupidtree.stupiduser.util.ImageUtils
import java.util.regex.Pattern

class EmoticonsTextView : AppCompatTextView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    val mInflater: LayoutInflater = LayoutInflater.from(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}

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
            val pattern = buildPattern()
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
            text
        }
    }

    companion object {
        fun buildPattern(): Pattern {
            return Pattern.compile("\\[u[0-9]+//@[^$]+\\$]")
        }
    }
}