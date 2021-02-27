package com.stupidtree.hitax.utils

import android.content.Context
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.base.BaseActivity
import com.stupidtree.hitax.ui.event.FragmentTimeInfoSheet
import java.util.*

object EventsUtils {
    fun showEventItem(context: Context, eventItem: EventItem) {
        if (context is BaseActivity<*, *>) {
            val list: ArrayList<EventItem> = ArrayList<EventItem>()
            list.add(eventItem)
            FragmentTimeInfoSheet.newInstance(list).show(context.supportFragmentManager, "event")
        }
    }

    fun showEventItem(context: Context, eventItems: List<EventItem>) {
        if (context is BaseActivity<*, *>) {
            val list: ArrayList<EventItem> = ArrayList<EventItem>(eventItems)
            FragmentTimeInfoSheet.newInstance(list)
                .show(context.supportFragmentManager, "event")
        }
    }

    /**
     * 获得当前是第几节课
     * num为节数*10（+5）
     */
    fun getCurrentNumberText(context: Context,num:Int): String {
        val base = num / 10
        val plus = num % 10
        return if (base == 0) {
            context.getString(R.string.before_first_class)
        } else if (base == 12 && plus != 0) {
            context.getString(R.string.after_last_class)
        } else {
            if (plus == 0) context.getString(
                R.string.class_number_what,
                base
            ) else context.getString(R.string.class_after_number_what, base)
        }
    }

}