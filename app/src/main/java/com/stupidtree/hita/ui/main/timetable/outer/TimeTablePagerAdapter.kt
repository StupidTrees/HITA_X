package com.stupidtree.hita.ui.main.timetable.outer

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.ui.main.timetable.inner.TimetablePageFragment
import java.util.*

class TimeTablePagerAdapter(pager: ViewPager, fm: FragmentManager,val size: Int) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var fragments: MutableList<TimetablePageFragment> = mutableListOf()
    private val windowStart: Calendar = Calendar.getInstance()

    private var currentIndex = 0
    private var startIndex = 0


    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pos = (position) % size
        return super.instantiateItem(container, pos)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
       super.destroyItem(container, position%size, `object`)
    }

    override fun getCount(): Int {
        return size*80000
    }

    fun getCurrentDate():Long{
        return windowStart.timeInMillis+(fragments.size/2)* WEEK_MILLS + WEEK_MILLS/2
    }

    fun scrollRight() {
        windowStart.timeInMillis += WEEK_MILLS
        val toChange = fragments[startIndex]//最左页面挪到右边使用
        toChange.resetWeek(windowStart.timeInMillis + (fragments.size - 1) * WEEK_MILLS)
        startIndex = (startIndex+1)%fragments.size
    }

    fun scrollLeft() {
        windowStart.timeInMillis -= WEEK_MILLS
        val toChange = fragments[(startIndex+fragments.size-1)%fragments.size]//最右页面挪到左边使用
        toChange.resetWeek(windowStart.timeInMillis)
        startIndex = (startIndex-1+fragments.size)%fragments.size
    }


    init {
        windowStart.firstDayOfWeek = Calendar.MONDAY
        windowStart[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        windowStart[Calendar.HOUR_OF_DAY] = 0
        windowStart[Calendar.MINUTE] = 0
        windowStart.add(Calendar.DATE, -7 * (size / 2))
        //比一半少一个，否则每次滑动，被循环的那个fragment将先被instantiate，再被destroy，白屏
        pager.offscreenPageLimit = size/2-1

        pager.adapter = this
        var tm = windowStart.timeInMillis
        for (i in 0 until size) {
            val f = TimetablePageFragment.newInstance(tm)
            fragments.add(f)
            tm += WEEK_MILLS
        }
        pager.currentItem = count/2 + size/2
        currentIndex = pager.currentItem
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == currentIndex - 1) {
                    scrollLeft()
                } else if (position == currentIndex + 1) {
                    scrollRight()
                }
                currentIndex = position
            }

        })
    }

    companion object{
        const val WEEK_MILLS:Long = 1000 * 60 * 60 * 24 * 7
    }
}