package com.stupidtree.hitax.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class LockedViewPager : ViewPager {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun scrollTo(x: Int, y: Int) {
//        if(noScroll){  //加上判断无法用 setCurrentItem 方法切换
        super.scrollTo(x, y)
        //        }
    }

    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return false
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item)
    }
}