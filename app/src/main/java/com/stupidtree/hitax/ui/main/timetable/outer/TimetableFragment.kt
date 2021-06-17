package com.stupidtree.hitax.ui.main.timetable.outer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.FragmentTimetableBinding
import com.stupidtree.hitax.ui.main.timetable.outer.TimeTablePagerAdapter.Companion.WEEK_MILLS
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.style.base.BaseFragment
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class TimetableFragment :
    BaseFragment<TimetableViewModel, FragmentTimetableBinding>() {
    private lateinit var pagerAdapter: TimeTablePagerAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fc = childFragmentManager.beginTransaction()
        for (f in childFragmentManager.fragments) {
            fc.remove(f)
        }
        fc.commitNowAllowingStateLoss()
        currentIndex = 0
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun initViews(view: View) {
        pagerAdapter = TimeTablePagerAdapter(requireContext(), binding?.pager!!, WINDOW_SIZE)
        currentIndex = binding?.pager?.currentItem ?: -1
        viewModel.timetableLiveData.observe(this) {

            viewModel.windowStartDate.value?.let { date ->
                val pageStart = date + WEEK_MILLS * (WINDOW_SIZE / 2)
                refreshWeekLayout(pageStart, it)
            }
        }
        viewModel.windowStartDate.observe(this) { date ->
            viewModel.timetableLiveData.value?.let {
                val pageStart = date + WEEK_MILLS * (WINDOW_SIZE / 2)
                refreshWeekLayout(pageStart, it)
            }
        }
        viewModel.startTimeLiveData.observe(this) {
            binding?.labels?.setStartDate(it / 100, it % 100)
        }
        for (i in 0 until WINDOW_SIZE) {
            viewModel.eventsLiveData[i].observe(this) {
                pagerAdapter.notifyTable(
                    i, viewModel.eventsTriggers[i].value?:0,
                    it.first,
                    it.second
                )
            }
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
                        when {
                            position < currentIndex -> {//往左
                                viewModel.scrollPrev()
                            }
                            position > currentIndex -> {//往右
                                viewModel.scrollNext()
                            }
                            else -> {

                            }
                        }
                    }
                }

                currentIndex = position
            }

        })
        binding?.fab?.setOnClickListener {
            scrollToDate(System.currentTimeMillis())
        }
    }



    private fun refreshWeekLayout(currentPageStart: Long, timetables: List<Timetable>) {
        val cdCalendar = Calendar.getInstance()
        cdCalendar.timeInMillis = currentPageStart
        binding?.month?.text = resources.getStringArray(R.array.months)[cdCalendar[Calendar.MONTH]]
        if (currentPageStart <= System.currentTimeMillis() && System.currentTimeMillis() < currentPageStart + WEEK_MILLS) {
            binding?.fab?.hide()
        } else {
            binding?.fab?.show()
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
        val sd = start.timeInMillis
        val oldStart = viewModel.windowStartDate.value ?: 0
        val newWindowStart = sd - WEEK_MILLS * (WINDOW_SIZE / 2)
        val oldWindowStart = oldStart - WEEK_MILLS * (WINDOW_SIZE / 2)
        if (sd > oldWindowStart && sd < oldWindowStart + WEEK_MILLS * WINDOW_SIZE) { //在窗口内
            val offset = ((newWindowStart - oldWindowStart).toFloat() / WEEK_MILLS).roundToInt()
            //viewModel.startIndex = (viewModel.startIndex + WINDOW_SIZE + offset) % WINDOW_SIZE
            binding!!.pager.currentItem += offset
        } else {
            Log.e("日期跳转", "超出窗口")
            if (sd < oldStart) {
                binding!!.pager.currentItem -= WINDOW_SIZE / 2 - 1
                viewModel.startIndex = (viewModel.startIndex + WINDOW_SIZE - (WINDOW_SIZE / 2 - 1)) % WINDOW_SIZE
            } else {
                binding!!.pager.currentItem += WINDOW_SIZE / 2 - 1
                viewModel.startIndex = (viewModel.startIndex + WINDOW_SIZE + (WINDOW_SIZE / 2 - 1)) % WINDOW_SIZE
            }
            viewModel.resetAll(date)
        }
    }

    companion object {
        const val WINDOW_SIZE: Int = 5
    }

}