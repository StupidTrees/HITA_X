package com.stupidtree.hitax.ui.widgets.today.slim

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.ui.widgets.WidgetUtils.EVENT_REFRESH
import com.stupidtree.hitax.ui.widgets.today.TodayUtils
import com.stupidtree.hitax.ui.widgets.today.TodayUtils.goAsync
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Implementation of App Widget functionality.
 */
@OptIn(DelicateCoroutinesApi::class)
class TodayWidgetSlim : AppWidgetProvider() {
    companion object {
        const val EVENT_CLICK2 = "com.stupidtree.hita.WIDGET_EVENT_CLICK2"
        const val EVENT_EXTRA2 = "com.stupidtree.hita.EXTRA_ITEM2"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val timetableRepo =
            TimetableRepository.getInstance(context.applicationContext as Application)
        goAsync {
            val events = timetableRepo.getTodayEventsSync()
            for (appWidgetId in appWidgetIds) {
                Log.e("WI2", "UPDATE:$appWidgetId")
                TodayUtils.setUpOneWidget(context, events, appWidgetManager, appWidgetId, true)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent!!.action) {
            EVENT_REFRESH -> {
                val cn = ComponentName(context, TodayWidgetSlim::class.java)
                val mgr = AppWidgetManager.getInstance(context)
                val timetableRepo =
                    TimetableRepository.getInstance(context.applicationContext as Application)
                goAsync {
                    val events = timetableRepo.getTodayEventsSync()
                    for (appWidgetId in mgr.getAppWidgetIds(cn)) {
                        Log.e("WI2", "refressh$appWidgetId")
                        TodayUtils.setUpOneWidget(context, events, mgr, appWidgetId, true)
                    }
                }
            }
        }

    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.e("WI2", "onDisabled")
        super.onDisabled(context)
    }


}

