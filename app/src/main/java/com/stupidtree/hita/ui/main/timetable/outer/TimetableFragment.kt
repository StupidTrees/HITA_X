package com.stupidtree.hita.ui.main.timetable.outer

import android.content.Context
import android.util.Log
import android.util.TimeUtils
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.Timetable

import com.stupidtree.hita.databinding.FragmentTimeTableBinding
import com.stupidtree.hita.ui.base.BaseFragment
import java.util.*

class TimetableFragment :
        BaseFragment<TimetableViewModel, FragmentTimeTableBinding>() {
    private lateinit var pagerAdapter: TimeTablePagerAdapter
    private var mainPageController: MainPageController? = null


    override fun initViewBinding(): FragmentTimeTableBinding {
        return FragmentTimeTableBinding.inflate(layoutInflater)
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
        pagerAdapter = binding?.pager?.let { TimeTablePagerAdapter(it, childFragmentManager, 5) }!!
        viewModel.timetableLiveData.observe(this) {
            refreshWeekLayout(it)
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
                viewModel.timetableLiveData.value?.let {
                    refreshWeekLayout(it)
                }
            }

        })
    }

    private fun refreshWeekLayout(timetables: List<Timetable>) {
        if (timetables.isEmpty()) {
            mainPageController?.setSingleTitle(getString(R.string.no_timetable))
            return
        }
        val weeks = mutableListOf<Int>()
        val cd = pagerAdapter.getCurrentDate()
        val cdCalendar = Calendar.getInstance()
        cdCalendar.timeInMillis = cd

        binding?.month?.text = resources.getStringArray(R.array.months)[cdCalendar[Calendar.MONTH]]

        var minTT: Timetable? = null
        var minWk = 99999
        //找到距离当前页面最近的学期
        for (tt in timetables) {
            val wk = tt.getWeekNumber(cd)
            weeks.add(wk)
            if (wk in 1 until minWk) {
                minWk = wk
                minTT = tt
            }
        }
        minTT?.let {
            mainPageController?.setTitleText(getString(R.string.week_title,minWk))
            it.name?.let { it1 -> mainPageController?.setTimetableName(it1) }
            return
        }
        mainPageController?.setSingleTitle(getString(R.string.holiday))

    }

    interface MainPageController {
        fun setTitleText(string: String)
        fun setTimetableName(String:String)
        fun setSingleTitle(string:String)
    }

}