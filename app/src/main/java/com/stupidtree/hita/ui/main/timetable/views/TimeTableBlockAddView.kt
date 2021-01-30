package com.stupidtree.hita.ui.main.timetable.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.ui.base.BaseActivity

class TimeTableBlockAddView(context: Context, var timePeriod: TimePeriodInDay) : FrameLayout(context) {


    var card: View
    var add: View
    val duration: Int
        get() = timePeriod.getLengthInMinutes()


    init {
        inflate(context, R.layout.dynamic_timetable_block_add, this)
        add = findViewById(R.id.add)
        card = findViewById(R.id.card)
        add.setOnClickListener {
            val parent = parent as ViewGroup
            parent.removeView(this@TimeTableBlockAddView)
        }
        card.setOnClickListener { add.callOnClick() }
    }
}