package com.stupidtree.hitax.ui.widgets.today.slim

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.ui.widgets.today.slim.TodayWidgetSlim.Companion.EVENT_EXTRA2
import com.stupidtree.hitax.utils.TimeTools

internal class ListRemoteViewsSlimFactory(val mContext: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {
    private val appWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
    private val mBeans = mutableListOf<EventItem>()
    override fun getViewAt(position: Int): RemoteViews {
        // 获取 item_widget_device.xml 对应的RemoteViews
        val rv = RemoteViews(mContext.packageName, R.layout.widget_today_item_slim)

        // 设置 第position位的“视图”的数据
        val event = mBeans[position]
        rv.setTextViewText(R.id.name, event.name)
        rv.setTextViewText(
            R.id.time, TimeTools.printTime(event.from.time) + "-" + TimeTools.printTime(
                event.to.time
            )
        )
        val result =
            if (TextUtils.isEmpty(event.place)) mContext.getString(R.string.unknown_location_widget) else event.place
        rv.setTextViewText(R.id.location, result)

        val lockIntent = Intent()
        lockIntent.putExtra(EVENT_EXTRA2, event.id)
        val bd = Bundle()
        bd.putString("eventId", event.id)
        bd.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        lockIntent.putExtras(bd)
        rv.setOnClickFillInIntent(R.id.item, lockIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null//return RemoteViews(mContext.packageName, R.layout.widget_today_loading)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }


    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val timetableRepo =
            TimetableRepository.getInstance(mContext.applicationContext as Application)
        val events = timetableRepo.getTodayEventsSync()
        mBeans.clear()
        mBeans.addAll(events)
    }


    override fun getItemId(position: Int): Long {
        // 返回当前项在“集合视图”中的位置
        return position.toLong()
    }


    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getCount(): Int {
        return mBeans.size
    }

    override fun onDestroy() {
        mBeans.clear()
    }

}