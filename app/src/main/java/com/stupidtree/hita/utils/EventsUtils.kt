package com.stupidtree.hita.utils

import android.content.Context
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.event.FragmentTimeInfoSheet
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