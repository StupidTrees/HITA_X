package com.stupidtree.hitax.ui.widgets

import android.content.Context
import android.content.Intent
import com.stupidtree.hitax.ui.widgets.today.normal.TodayWidget
import com.stupidtree.hitax.ui.widgets.today.slim.TodayWidgetSlim

object WidgetUtils {
    val widgets = listOf(TodayWidget::class.java, TodayWidgetSlim::class.java)
    const val EVENT_REFRESH = "com.stupidtree.hita.WIDGET_EVENT_REFRESH"

    fun sendRefreshToAll(context: Context){
        for(wid in widgets){
            val btIntent = Intent().setAction(EVENT_REFRESH)
            btIntent.setClass(context, wid)
            context.sendBroadcast(btIntent)
        }
    }
}