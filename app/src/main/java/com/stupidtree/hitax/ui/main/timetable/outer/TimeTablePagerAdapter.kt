package com.stupidtree.hitax.ui.main.timetable.outer

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.main.timetable.inner.TimetableStyleSheet
import com.stupidtree.hitax.ui.main.timetable.views.TimeTableView

class TimeTablePagerAdapter(
    private val context: Context,
    private val pager: ViewPager,
    val size: Int
) : PagerAdapter() {

    private var tables = arrayOfNulls<TimeTableView?>(size)
    private val cache: MutableMap<Int, Triple<Long, List<EventItem>, TimetableStyleSheet>> =
        mutableMapOf()


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (tables[position % size] == null) {
            val target = TimeTableView(context)
            target.init()
            container.addView(target)
            tables[position%size] = target
            if (cache.containsKey(position % size)) {
                val tp = cache[position % size]!!
                target.notifyRefresh(tp.first, tp.second, tp.third)
                cache.remove(position % size)
            }
        }
        return tables[position % size]!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position%size, `object`)
        if (tables[position % size] != null) {
            container.removeView(tables[position % size])
            tables[position % size] = null
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return size * 80000
    }

    fun notifyTable(
        position: Int,
        startDate: Long,
        events: List<EventItem>,
        style: TimetableStyleSheet
    ) {
        Log.e("notifyTable", style.toString())
        if (tables[position] == null) {
            cache[position] = Triple(startDate, events, style)
        } else {
            tables[position]?.notifyRefresh(startDate, events, style)
        }
    }

    init {
        //比一半少一个，否则每次滑动，被循环的那个fragment将先被instantiate，再被destroy，白屏
        pager.offscreenPageLimit = size / 2 - 1
        pager.adapter = this
        pager.currentItem = count / 2 + (size / 2)
    }

    companion object {
        const val WEEK_MILLS: Long = 1000 * 60 * 60 * 24 * 7
    }

}