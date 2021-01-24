package com.stupidtree.hita.ui.timetable.activity

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.core.util.lruCache
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.stupidtree.hita.ui.timetable.fragment.TimetablePageFragment
import java.text.SimpleDateFormat
import java.util.*

class TimeTablePagerAdapter(private val context: Context, fm: FragmentManager?, initPosition: Int) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    //保存每个Fragment的Tag，刷新页面的依据
    private val initTime: Long
    private val initPosition: Int
    private val fragments = lruCache<Long, TimetablePageFragment>(10, { _, _ ->
        return@lruCache 4
    }, {
        Log.e("init", it.toString())
        return@lruCache TimetablePageFragment.newInstance(it)
    }, { _, _, _, _ -> })


    override fun getPageTitle(position: Int): CharSequence {
        val offset = (7 * 24 * 60 * 60 * 1000 * (position - initPosition)).toLong()
        val c = Calendar.getInstance()
        c.timeInMillis = initTime + offset
        return SimpleDateFormat("yy-MM-dd").format(c.time)
        //return String.format(context.getString(R.string.timetable_tab_title), position + 1)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragments.remove(initPosition + (7 * 24 * 60 * 60 * 1000 * (position - initPosition)).toLong())
    }

    override fun instantiateItem(container: ViewGroup,  position: Int): Any {
        var position = position
        position %= 10
        return super.instantiateItem(container, position)
    }
    override fun getItem(position: Int): Fragment {
        return fragments[initPosition + (7 * 24 * 60 * 60 * 1000 * (position - initPosition)).toLong()]
    }

    override fun getCount(): Int {
        return 40
    }

    init {
        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        c[Calendar.HOUR] = 0
        c[Calendar.MINUTE] = 0
        initTime = c.timeInMillis
        this.initPosition = initPosition
    }
}