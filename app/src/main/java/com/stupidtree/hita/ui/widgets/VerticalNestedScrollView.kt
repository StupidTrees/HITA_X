package com.stupidtree.hita.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import kotlin.math.abs


open class VerticalNestedScrollView : NestedScrollView {


    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }

    private var mDownPosX = 0f
    private var mDownPosY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosX = x
                mDownPosY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = abs(x - mDownPosX)
                val deltaY = abs(y - mDownPosY)
                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (deltaX > 0.5*deltaY) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

}