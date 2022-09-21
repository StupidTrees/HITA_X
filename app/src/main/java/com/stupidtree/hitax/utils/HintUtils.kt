package com.stupidtree.hitax.utils

import android.content.Context
import com.stupidtree.hitax.data.model.timetable.EventItem

object HintUtils {
    const val HINT_PULL_DOWN = "pull_down"

    fun getHints(context: Context): List<EventItem> {
        val sp = context.getSharedPreferences("hint", Context.MODE_PRIVATE)
        val res = mutableListOf<EventItem>()
        if (!sp.getBoolean(HINT_PULL_DOWN, false)) {
            val ei = EventItem.getTagInstance("下拉试试看？")
            ei.id = HINT_PULL_DOWN
            res.add(ei)
        }
        return res
    }

    fun clickHint(context: Context, hint: EventItem) {
        val sp = context.getSharedPreferences("hint", Context.MODE_PRIVATE)
        sp.edit().putBoolean(hint.id, true).apply()
    }
}