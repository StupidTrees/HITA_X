package com.stupidtree.hita.theta.ui.widgets

import android.graphics.Canvas
import android.graphics.Paint
import android.view.ViewGroup

import android.text.style.ReplacementSpan
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable


class MarkerViewSpan(view: View) : ReplacementSpan() {
    private var view: View = view

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val widthSpec: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        if (fm != null) {
            val height: Int = view.measuredHeight
            fm.top = -height / 2
            fm.ascent = fm.top
            fm.bottom = height / 2
            fm.descent = fm.bottom
        }
        return view.right
    }

    override fun draw(
        @NonNull canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        @NonNull paint: Paint
    ) {
        val fm: Paint.FontMetricsInt = paint.fontMetricsInt
        val transY: Int =
            (y + fm.descent + y + fm.ascent) / 2 - view.measuredHeight / 2 //计算y方向的位移
        canvas.save()
        canvas.translate(x, transY.toFloat())
        view.draw(canvas)
        canvas.restore()
    }

    init {
        this.view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}