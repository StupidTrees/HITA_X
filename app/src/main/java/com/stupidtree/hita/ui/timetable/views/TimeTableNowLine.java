package com.stupidtree.hita.ui.timetable.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;


public class TimeTableNowLine extends View {
    int color;
    public TimeTableNowLine(Context context,int color) {
        super(context);
        this.color = color;
        setBackgroundColor(color);
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public TimeTableNowLine(Context context) {
        super(context);
    }

    public TimeTableNowLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTableNowLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
