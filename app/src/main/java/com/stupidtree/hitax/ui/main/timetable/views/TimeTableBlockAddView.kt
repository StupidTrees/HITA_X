package com.stupidtree.hitax.ui.main.timetable.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay

class TimeTableBlockAddView(context: Context, var timePeriod: TimePeriodInDay, val dow: Int) : FrameLayout(context) {

    var card: View
    var add: View
    val duration: Int
        get() = timePeriod.getLengthInMinutes()

    interface OnAddClickListener{
        fun onClick(view:View)
    }

    var onAddClickListener:OnAddClickListener?=null

    init {
        inflate(context, R.layout.dynamic_timetable_block_add, this)
        add = findViewById(R.id.add)
        card = findViewById(R.id.card)
        add.setOnClickListener {
            val parent = parent as ViewGroup
            onAddClickListener?.onClick(it)
            parent.removeView(this@TimeTableBlockAddView)
        }
        card.setOnClickListener { add.callOnClick() }
    }
}