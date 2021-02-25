package com.stupidtree.hitax.utils

import android.content.Context
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

}