package com.stupidtree.hitax.ui.widgets.pullextend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.stupidtree.hitax.R

/**
 * Created by Renny on 2018/1/2.
 */
class ExpendPoint @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var percent = 0f
    var maxRadius = 15f
    private var maxDist = 60f
    var mPaint: Paint = Paint()
    var outerPaint: Paint = Paint()
    fun setMaxRadius(maxRadius: Int) {
        this.maxRadius = maxRadius.toFloat()
    }

    fun setMaxDist(maxDist: Float) {
        this.maxDist = maxDist
    }

    fun setPercent(percent: Float) {
        if (percent != this.percent) {
            this.percent = percent
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        maxRadius = height / 2f
        val centerX = width / 2f
        val centerY = height / 2f
        if (percent <= 0.5f) {
            mPaint.alpha = 255
            outerPaint.alpha = 64
            val radius = percent * 2 * maxRadius
            canvas.drawCircle(centerX, centerY, radius / 3, mPaint)
            canvas.drawCircle(centerX, centerY, radius, outerPaint)
        } else {
            val afterPercent = (percent - 0.5f) / 0.5f
            val radius = maxRadius - maxRadius / 2 * afterPercent
            canvas.drawCircle(centerX, centerY, radius / (3 - 1.5f * afterPercent), mPaint)
            canvas.drawCircle(centerX, centerY, radius, outerPaint)
            //            canvas.drawCircle(centerX - afterPercent * maxDist, centerY, maxRadius / 2, mPaint);
//            canvas.drawCircle(centerX + afterPercent * maxDist, centerY, maxRadius / 2, mPaint);
        }
    }

    init {
        mPaint.isAntiAlias = true
        outerPaint.isAntiAlias = true
        val a = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.ExpendPoint,
            defStyleAttr,
            0
        )
        val n = a.indexCount
        for (i in 0..n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.ExpendPoint_pullDownAnimElementColor -> {
                    mPaint.color = a.getColor(attr, Color.GRAY)
                    outerPaint.color = a.getColor(attr, Color.GRAY)
                }
            }
        }
    }
}