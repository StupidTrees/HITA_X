package com.stupidtree.hita.ui.main.timetable.outer

import android.util.Log
import android.view.ViewGroup
import android.view.ViewParent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.ui.main.timetable.inner.TimetablePageFragment
import com.stupidtree.hita.ui.main.timetable.outer.TimetableFragment.Companion.WINDOW_SIZE
import com.stupidtree.hita.utils.TimeUtils
import kotlin.math.roundToInt

class TimeTablePagerAdapter(
    private val pager: ViewPager,
    fm: FragmentManager,
    val size: Int,
    initWindowStart: Long
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var fragments: MutableList<TimetablePageFragment> = mutableListOf()


    private var startIndex = 0


    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pos = (position) % size
        return super.instantiateItem(container, pos)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position % size, `object`)
    }

    override fun getCount(): Int {
        return size * 80000
    }


    fun scrollRight(windowStart: Long) {
        val toChange = fragments[startIndex]//最左页面挪到右边使用
        toChange.resetWeek(windowStart + (size - 1) * WEEK_MILLS)
        startIndex = (startIndex + 1) % size
    }

    fun scrollLeft(windowStart: Long) {
        val toChange = fragments[(startIndex + size - 1) % size]//最右页面挪到左边使用
        toChange.resetWeek(windowStart)
        startIndex = (startIndex - 1 + size) % size
    }


    fun scrollToDate(date: Long, oldStart: Long): Boolean {
        var newWindowStart = date - WEEK_MILLS * (size / 2)
        val oldWindowStart = oldStart - WEEK_MILLS * (size / 2)
        if (date > oldWindowStart && date < oldWindowStart + WEEK_MILLS * size) { //在窗口内
            val offset = ((newWindowStart - oldWindowStart).toFloat() / WEEK_MILLS).roundToInt()
            pager.currentItem += offset
            return offset != 0
        }
        Log.e("日期跳转","超出窗口")
        if (date < oldStart) {
            pager.currentItem -= WINDOW_SIZE/2 - 1
        } else {
            pager.currentItem += WINDOW_SIZE/2 - 1
        }
        startIndex = (pager.currentItem - size / 2 + size) % size
        for (i in 0 until size) {
            fragments[(i + startIndex) % size].resetWeek(newWindowStart)
            newWindowStart += WEEK_MILLS
        }
        return true
    }


    init {
        //比一半少一个，否则每次滑动，被循环的那个fragment将先被instantiate，再被destroy，白屏
        pager.offscreenPageLimit = size / 2 - 1
        pager.adapter = this
        var tm = initWindowStart

        for (i in 0 until size) {
            val f = TimetablePageFragment.newInstance()
            f.resetWeek(tm)
            fragments.add(f)
            tm += WEEK_MILLS
        }
        pager.currentItem = count / 2 + (size / 2)

    }

    companion object {
        const val WEEK_MILLS: Long = 1000 * 60 * 60 * 24 * 7
    }
}