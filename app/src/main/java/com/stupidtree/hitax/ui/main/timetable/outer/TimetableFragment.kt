package com.stupidtree.hitax.ui.main.timetable.outer

import android.content.Context
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.FragmentTimetableBinding
import com.stupidtree.hitax.ui.base.BaseFragment
import com.stupidtree.hitax.ui.main.timetable.outer.TimeTablePagerAdapter.Companion.WEEK_MILLS
import java.util.*
import kotlin.math.abs

class TimetableFragment :
        BaseFragment<TimetableViewModel, FragmentTimetableBinding>() {
    private var pagerAdapter: TimeTablePagerAdapter? = null
    private var mainPageController: MainPageController? = null
    private var currentIndex = 0


    override fun initViewBinding(): FragmentTimetableBinding {
        return FragmentTimetableBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableViewModel> {
        return TimetableViewModel::class.java
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainPageController) {
            mainPageController = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainPageController = null
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()
    }

    override fun initViews(view: View) {
        pagerAdapter = binding?.pager?.let { it ->
            TimeTablePagerAdapter(it, childFragmentManager, WINDOW_SIZE, viewModel.initWindow())
        }
        currentIndex = binding?.pager?.currentItem ?: -1
        viewModel.timetableLiveData.observe(this) {
            viewModel.currentPageStartDate.value?.let { date ->
                refreshWeekLayout(date, it)
            }
        }
        viewModel.currentPageStartDate.observe(this) { date ->
            viewModel.timetableLiveData.value?.let {
                refreshWeekLayout(date, it)
            }
        }
        viewModel.startDateLiveData.observe(this) {
            binding?.labels?.setStartDate(it / 100, it % 100)
        }
        binding?.pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (abs(position - currentIndex) <= WINDOW_SIZE / 2) {
                    for (i in 0 until abs(position - currentIndex)) {
                        viewModel.currentPageStartDate.value?.let {
                            val oldWindowStart = it - WEEK_MILLS * (WINDOW_SIZE / 2)
                            when {
                                position < currentIndex -> {//往左
                                    viewModel.addStartDate(-WEEK_MILLS)
                                    pagerAdapter?.scrollLeft(oldWindowStart - WEEK_MILLS)
                                }
                                position > currentIndex -> {//往右
                                    viewModel.addStartDate(WEEK_MILLS)
                                    pagerAdapter?.scrollRight(oldWindowStart + WEEK_MILLS)
                                }
                                else -> {

                                }
                            }
                        }
                    }
                }

                currentIndex = position
            }

        })
        binding?.floatingActionButton?.setOnClickListener {
            scrollToDate(System.currentTimeMillis())
        }
    }


    private fun refreshWeekLayout(currentPageStart: Long, timetables: List<Timetable>) {
        val cdCalendar = Calendar.getInstance()
        cdCalendar.timeInMillis = currentPageStart
        binding?.month?.text = resources.getStringArray(R.array.months)[cdCalendar[Calendar.MONTH]]
        if (currentPageStart <= System.currentTimeMillis() && System.currentTimeMillis() < currentPageStart + WEEK_MILLS) {
            binding?.floatingActionButton?.hide()
        } else {
            binding?.floatingActionButton?.show()
        }
        if (timetables.isEmpty()) {
            mainPageController?.setSingleTitle(getString(R.string.no_timetable))
            return
        }
        val weeks = mutableListOf<Int>()

        var minTT: Timetable? = null
        var minWk = Int.MAX_VALUE
        //找到距离当前页面最近的学期
        for (tt in timetables) {
            val wk = tt.getWeekNumber(cdCalendar.timeInMillis)
            weeks.add(wk)
            if (wk in 1 until minWk) {
                minWk = wk
                minTT = tt
            }
        }

        minTT?.let {
            mainPageController?.setTitleText(getString(R.string.week_title, minWk))
            it.name?.let { it1 -> mainPageController?.setTimetableName(it1) }
            return
        }
        mainPageController?.setSingleTitle(getString(R.string.holiday))

    }

    interface MainPageController {
        fun setTitleText(string: String)
        fun setTimetableName(String: String)
        fun setSingleTitle(string: String)
    }

    private fun scrollToDate(date: Long) {
        val start = Calendar.getInstance()
        start.timeInMillis = date
        start.firstDayOfWeek = Calendar.MONDAY
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.MILLISECOND, 0)
        start.set(Calendar.SECOND, 0)
        viewModel.currentPageStartDate.value?.let {
            if (pagerAdapter?.scrollToDate(start.timeInMillis, it) == true) {
                viewModel.currentPageStartDate.value = start.timeInMillis
            }
        }

    }

    companion object {
        const val WINDOW_SIZE: Int = 5
    }

}