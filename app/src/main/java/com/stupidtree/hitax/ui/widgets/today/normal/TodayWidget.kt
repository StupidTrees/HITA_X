package com.stupidtree.hitax.ui.widgets.today.normal

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.ui.widgets.WidgetUtils.EVENT_REFRESH
import com.stupidtree.hitax.ui.widgets.today.TodayUtils.goAsync
import com.stupidtree.hitax.ui.widgets.today.TodayUtils.setUpOneWidget
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Implementation of App Widget functionality.
 */
class TodayWidget : AppWidgetProvider() {
    companion object {
        const val EVENT_CLICK = "com.stupidtree.hita.WIDGET_EVENT_CLICK"
        const val EVENT_EXTRA = "com.stupidtree.hita.EXTRA_ITEM"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        val timetableRepo =
            TimetableRepository.getInstance(context.applicationContext as Application)
        goAsync{
            val events = timetableRepo.getTodayEventsSync()
            for (appWidgetId in appWidgetIds) {
                Log.e("WI", "UPDATE:$appWidgetId")
                setUpOneWidget(context, events, appWidgetManager, appWidgetId,false)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        //val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
        when (intent!!.action) {
            EVENT_CLICK -> {
//                Log.e("WI", "click")
//                val bd = intent.extras
//                val position = intent.getStringExtra(EVENT_EXTRA)
//                Toast.makeText(context, "打开...$position", Toast.LENGTH_SHORT).show()
            }
            EVENT_REFRESH -> {
                val cn = ComponentName(context, TodayWidget::class.java)
                val mgr = AppWidgetManager.getInstance(context)
                val timetableRepo =
                    TimetableRepository.getInstance(context.applicationContext as Application)
                goAsync {
                    val events = timetableRepo.getTodayEventsSync()
                    for (appWidgetId in mgr.getAppWidgetIds(cn)) {
                        Log.e("WI", "refressh$appWidgetId")
                        setUpOneWidget(context, events, mgr, appWidgetId,false)
                    }
                }

            }
        }

    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.e("WI", "onDisabled")
        super.onDisabled(context)
    }


}

