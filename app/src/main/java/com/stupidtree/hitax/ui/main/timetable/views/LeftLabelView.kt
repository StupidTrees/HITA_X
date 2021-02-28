package com.stupidtree.hitax.ui.main.timetable.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimeInDay

class LeftLabelView : View {
    constructor(context: Context?) : super(context) {}

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        typedTimeTableView(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        typedTimeTableView(attrs, defStyleAttr)
    }

    private fun typedTimeTableView(attrs: AttributeSet, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LeftLabelView,
                defStyleAttr,
                0
        )
        val n = a.indexCount
        for (i in 0..n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.LeftLabelView_timeLabelSize -> labelSize = a.getDimensionPixelSize(
                        attr, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 8f, resources.displayMetrics
                ).toInt()
                )
                R.styleable.LeftLabelView_timeLabelColor -> labelColor =
                        a.getColor(attr, Color.BLACK)
            }
        }
    }

    private val mLabelPaint = Paint()
    var labelColor = 0
    var labelSize = 0
    private val startDate = TimeInDay(8, 0)
    private val temp = TimeInDay(8, 0)
    var sectionHeight = 180

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mLabelPaint.color = Color.BLACK
        mLabelPaint.textSize = labelSize.toFloat()
        mLabelPaint.textAlign = Paint.Align.LEFT
        for (i in startDate.hour..23) {
            temp.hour = i
            val top = ((startDate.getDistanceInMinutes(temp)/60f) * sectionHeight)
            canvas.drawText(
                    temp.toString(), 0f, (top + labelSize), mLabelPaint)
        }
    }

    fun setStartDate(hour: Int, minute: Int) {
        startDate.hour = hour
        startDate.minute = minute
        invalidate()
    }
}