package com.stupidtree.hita.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class LockedViewPager extends ViewPager {
    public LockedViewPager(@NonNull Context context) {
        super(context);
    }

    public LockedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
//        if(noScroll){  //加上判断无法用 setCurrentItem 方法切换
        super.scrollTo(x, y);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
