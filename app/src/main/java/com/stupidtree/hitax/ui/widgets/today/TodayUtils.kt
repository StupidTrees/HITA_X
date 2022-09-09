package com.stupidtree.hitax.ui.widgets.today

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.main.MainActivity
import com.stupidtree.hitax.ui.widgets.WidgetUtils
import com.stupidtree.hitax.ui.widgets.today.normal.TodayWidget
import com.stupidtree.hitax.ui.widgets.today.slim.TodayWidgetSlim
import com.stupidtree.hitax.utils.TimeTools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

object TodayUtils {

    fun BroadcastReceiver.goAsync(
        coroutineScope: CoroutineScope = GlobalScope,
        block: suspend () -> Unit
    ) {
        val result = goAsync()
        coroutineScope.launch {
            try {
                block()
            } finally {
                // Always call finish(), even if the coroutineScope was cancelled
                result.finish()
            }
        }
    }

    fun setUpOneWidget(
        context: Context,
        events: List<EventItem>,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        slim: Boolean
    ) {
        val views = RemoteViews(
            context.packageName ?: "",
            if (slim) R.layout.widget_today_slim else R.layout.widget_today
        )
        val btIntent = Intent().setAction(WidgetUtils.EVENT_REFRESH)
        btIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        btIntent.setClass(
            context,
            if (slim) TodayWidgetSlim::class.java else TodayWidget::class.java
        )
        val btPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                btIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        views.setOnClickPendingIntent(R.id.refresh, btPendingIntent)
        val ai = Intent(context, MainActivity::class.java)
        val bi =
            PendingIntent.getActivity(
                context,
                0,
                ai,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        views.setOnClickPendingIntent(R.id.wid, bi)
//        val gridIntent = Intent(context, TodayWidget::class.java)
        val gridIntent = Intent(context, MainActivity::class.java)
        gridIntent.action = TodayWidget.EVENT_CLICK
        gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        gridIntent.data = Uri.parse(gridIntent.toUri(Intent.URI_INTENT_SCHEME))
        val pendingIntent = PendingIntent.getActivity(
            context,
            0, gridIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0, gridIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
//        )
        // 设置intent模板
        views.setPendingIntentTemplate(R.id.list, pendingIntent)
        views.setTextViewText(
            R.id.tv_title,
            TimeTools.getDateString(
                context,
                Calendar.getInstance(),
                simplified = true,
                TTYMode = if (slim) TimeTools.TTY_NONE else TimeTools.TTY_WK2_FOLLOWING
            )
        )
        val serviceIntent = Intent(context, ListWidgetService::class.java)
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        serviceIntent.putExtra("slim", slim)
        serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
        views.setRemoteAdapter(R.id.list, serviceIntent)
        if (events.isEmpty()) {
            views.setTextViewText(
                R.id.loading,
                context.getString(R.string.timeline_head_free_title)
            )
            views.setViewVisibility(R.id.list, View.GONE)
            views.setViewVisibility(R.id.loading_icon, View.VISIBLE)
            views.setViewVisibility(R.id.place_holder, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.place_holder, View.GONE)
            views.setViewVisibility(R.id.list, View.VISIBLE)
        }
        if (events.isEmpty()) {
            views.setViewVisibility(R.id.list, View.GONE)
            views.setViewVisibility(R.id.place_holder, View.VISIBLE)
            views.setTextViewText(
                R.id.loading,
                context.getString(R.string.timeline_head_free_title)
            )
            views.setViewVisibility(R.id.loading_icon, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.list, View.VISIBLE)
            views.setViewVisibility(R.id.place_holder, View.GONE)
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list)
    }
}