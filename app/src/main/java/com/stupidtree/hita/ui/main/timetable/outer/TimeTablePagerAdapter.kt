package com.stupidtree.hita.ui.main.timetable.outer

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.ui.main.timetable.inner.TimetablePageFragment
import com.stupidtree.hita.utils.TimeUtils

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


    fun scrollToDate(date: Long, oldStart: Long) {
        var newStart = date - WEEK_MILLS * size / 2
        if (date < oldStart) {//跳出了窗口
            startIndex = 0//(startIndex - 3 + size) % size
            for (i in 0 until size) {
                fragments[i].resetWeek(newStart, true)
                newStart += WEEK_MILLS
            }
            pager.currentItem = pager.currentItem - pager.currentItem % size + size / 2
        } else if (date >= oldStart + WEEK_MILLS * size) {
            startIndex = 0//(startIndex + 3 + size) % size
            for (i in 0 until size) {
                fragments[i].resetWeek(newStart, true)
                newStart += WEEK_MILLS
            }
            pager.currentItem = pager.currentItem - pager.currentItem % size + size + size / 2
        }

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
        Log.e("window", TimeUtils.printDate(initWindowStart))
        pager.currentItem = count / 2 + size / 2

    }

    companion object {
        const val WEEK_MILLS: Long = 1000 * 60 * 60 * 24 * 7
    }
}