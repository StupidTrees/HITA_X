package com.stupidtree.hitax.ui.main.timetable

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.databinding.FragmentTimetableBinding
import com.stupidtree.hitax.ui.event.add.PopupAddEvent
import com.stupidtree.hitax.ui.main.timetable.views.TimeTableView
import com.stupidtree.hitax.ui.main.timetable.views.TimetableWeekView
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.EventsUtils
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.style.base.BaseFragment
import tyrantgit.explosionfield.ExplosionField
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class TimetableFragment :
    BaseFragment<TimetableViewModel, FragmentTimetableBinding>() {

    companion object {
        const val WINDOW_SIZE: Int = 5
        const val WEEK_MILLS: Long = 1000 * 60 * 60 * 24 * 7
    }

    private lateinit var pagerAdapter: TimeTablePagerAdapter
    private var mainPageController: MainPageController? = null
    private var views = arrayOfNulls<TimetableWeekView>(WINDOW_SIZE)

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
        pagerAdapter = TimeTablePagerAdapter(WINDOW_SIZE)
        binding?.pager?.adapter = pagerAdapter
        binding?.pager?.offscreenPageLimit = WINDOW_SIZE / 2 - 1
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
                if (abs(position - viewModel.currentIndex) <= WINDOW_SIZE / 2) {
                    for (i in 0 until abs(position - viewModel.currentIndex)) {
                        viewModel.currentPageStartDate.value?.let {
                            val oldWindowStart = it - WEEK_MILLS * (WINDOW_SIZE / 2)
                            when {
                                position < viewModel.currentIndex -> {//往左
                                    viewModel.addStartDate(-WEEK_MILLS)
                                    pagerAdapter.scrollLeft(oldWindowStart - WEEK_MILLS)
                                }
                                position > viewModel.currentIndex -> {//往右
                                    viewModel.addStartDate(WEEK_MILLS)
                                    pagerAdapter.scrollRight(oldWindowStart + WEEK_MILLS)
                                }
                                else -> {

                                }
                            }
                        }
                    }
                }

                viewModel.currentIndex = position
            }

        })
        binding?.fab?.setOnClickListener {
            scrollToDate(System.currentTimeMillis())
        }
        binding?.pager?.currentItem = pagerAdapter.count / 2 + (WINDOW_SIZE / 2)
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
        viewModel.startTimeLiveData.observe(this) {
            binding?.labels?.setStartDate(it / 100, it % 100)
        }
        for (i in 0 until WINDOW_SIZE) {
            viewModel.windowStartData[i].observe(this) { date ->
                views[i]?.setDateTexts(date)
            }
            viewModel.windowEventsData[i].observe(this) {
                viewModel.windowStartData[i].value?.let { date ->
                    val dataHash = it.hashCode()
                    if (it.second != views[i]?.getStyleSheet()//样式不同也更新
                        || dataHash != viewModel.windowHashesData[i] ||
                        (views[i]?.getEventsViewNum() ?: 0) < it.first.size
                    ) {
                        viewModel.windowHashesData[i] = dataHash
                        views[i]?.refresh(date, it.first, it.second, false)
                    } else {
                        views[i]?.refresh(date, it.first, it.second, true)
                    }
                }
            }
        }
        val windowStart =
            (viewModel.currentPageStartDate.value ?: 0) - WEEK_MILLS * (WINDOW_SIZE / 2)
        for (i in 0 until WINDOW_SIZE) {
            viewModel.windowStartData[(i + viewModel.startIndex) % WINDOW_SIZE].value =
                windowStart + i * WEEK_MILLS
        }
        viewModel.currentPageStartDate.value = viewModel.currentPageStartDate.value
    }


    private fun getCurrentTimetableAndWeek(): Pair<Timetable?, Int?> {
        viewModel.timetableLiveData.value?.let { tts ->
            viewModel.currentPageStartDate.value?.let {
                val cdCalendar = Calendar.getInstance()
                cdCalendar.timeInMillis = it
                var minTT: Timetable? = null
                var minWk = Int.MAX_VALUE
                //找到距离当前页面最近的学期
                for (tt in tts) {
                    val wk = tt.getWeekNumber(cdCalendar.timeInMillis)
                    if (wk in 1 until minWk) {
                        minWk = wk
                        minTT = tt
                    }
                }
                return Pair(minTT, minWk)
            }
        }

        return Pair(null, null)
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
        var minTT: Timetable? = null
        var minWk = Int.MAX_VALUE
        //找到距离当前页面最近的学期
        for (tt in timetables) {
            val wk = tt.getWeekNumber(cdCalendar.timeInMillis)
            if (wk in 1 until minWk) {
                minWk = wk
                minTT = tt
            }
        }
        minTT?.let {
            mainPageController?.setTitleText(getString(R.string.week_title, minWk))
            it.name?.let { it1 -> mainPageController?.setTimetableName(it1) }
            TimeTableView.timetableStructure = it.scheduleStructure
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
            if (pagerAdapter.scrollToDate(start.timeInMillis, it)) {
                viewModel.currentPageStartDate.value = start.timeInMillis
            }
        }
    }


    inner class TimeTablePagerAdapter(
        val size: Int,
    ) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val pos = (position) % size
            if (views[pos] == null) {
                views[pos] = TimetableWeekView(context)
                views[pos]?.init()
                viewModel.windowStartData[pos].value = viewModel.windowStartData[pos].value
                viewModel.windowEventsData[pos].value?.let {
                    views[pos]?.refresh(
                        viewModel.windowStartData[pos].value ?: 0,
                        it.first,
                        it.second,
                        false
                    )
                }
                views[pos]?.setOnCardClickListener(object : TimeTableView.OnCardClickListener {
                    override fun onEventClick(v: View, eventItem: EventItem) {
                        context?.let { EventsUtils.showEventItem(it, eventItem) }
                    }

                    override fun onDuplicateEventClick(v: View, eventItems: List<EventItem>) {
                        context?.let { EventsUtils.showEventItem(it,eventItems) }
                    }

                })
                views[pos]?.setOnCardLongClickListener(object :
                    TimeTableView.OnCardLongClickListener {
                    override fun onEventLongClick(v: View, eventItem: EventItem): Boolean {
                        val pm = PopupMenu(requireContext(), v)
                        pm.inflate(R.menu.menu_opr_timetable)
                        pm.setOnMenuItemClickListener { item ->
                            if (item.itemId == R.id.delete) {
                                val ad = AlertDialog.Builder(requireContext())
                                    .setNegativeButton(R.string.button_cancel, null)
                                    .setPositiveButton(R.string.button_confirm) { _, _ ->
                                        val ef: ExplosionField =
                                            ExplosionField.attach2Window(requireActivity())
                                        ef.explode(v)
                                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        Thread{
                                            activity?.application?.let {
                                                TimetableRepository.getInstance(it).actionDeleteEvents(
                                                    listOf(eventItem)
                                                )
                                            }
                                        }.start()
                                    }.create()
                                ad.setTitle(R.string.dialog_title_sure_delete)
                                ad.show()
                            }
                            true
                        }
                        pm.show()
                        return true
                    }

                    override fun onDuplicateEventClick(
                        v: View,
                        eventItems: List<EventItem>
                    ): Boolean {
                        return false
                    }
                })
                views[pos]?.setOnAddClickListener(object : TimeTableView.OnAddClickListener {
                    override fun onAddClick(dow: Int, period: TimePeriodInDay) {
                        viewModel.timetableLiveData.value?.let {
                            val cdCalendar = Calendar.getInstance()
                            cdCalendar.timeInMillis = viewModel.currentPageStartDate.value ?: 0
                            val cp = getCurrentTimetableAndWeek()
                            cp.first?.let { timetable ->
                                PopupAddEvent().setInitTimetable(timetable).setInitTime(
                                    dow, week = cp.second ?: 1,
                                    period
                                ).show(childFragmentManager, "add")
                            } ?: kotlin.run {
                                Toast.makeText(
                                    context,
                                    getString(R.string.add_timetable_first),
                                    Toast.LENGTH_SHORT
                                ).show()
                                ActivityUtils.startTimetableManager(activity!!)
                            }

                        }
                    }
                })
                container.addView(views[pos])
            }
            return views[pos]!!
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val pos = (position) % size
            container.removeView(views[pos])
            views[pos] = null
            //super.destroyItem(container, pos, `object`)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return size * 80000
        }

        fun scrollRight(windowStart: Long) {
            viewModel.windowStartData[viewModel.startIndex].value =
                (windowStart + (size - 1) * WEEK_MILLS)
            viewModel.startIndex = (viewModel.startIndex + 1) % size
        }

        fun scrollLeft(windowStart: Long) {
            viewModel.windowStartData[(viewModel.startIndex + size - 1) % size].value = windowStart
            viewModel.startIndex = (viewModel.startIndex - 1 + size) % size
        }

        fun scrollToDate(date: Long, oldStart: Long): Boolean {
            var newWindowStart = date - WEEK_MILLS * (size / 2)
            val oldWindowStart = oldStart - WEEK_MILLS * (size / 2)
            if (date > oldWindowStart && date < oldWindowStart + WEEK_MILLS * size) { //在窗口内
                val offset =
                    ((newWindowStart - oldWindowStart).toFloat() / WEEK_MILLS).roundToInt()
                binding?.pager?.let { pg -> pg.currentItem += offset }
                return offset != 0
            }
//            Log.e("日期跳转", "超出窗口")
            if (date < oldStart) {
                binding?.pager?.let { pg -> pg.currentItem -= WINDOW_SIZE / 2 - 1 }
            } else {
                binding?.pager?.let { pg -> pg.currentItem += WINDOW_SIZE / 2 - 1 }
            }
            viewModel.startIndex = ((binding?.pager?.currentItem ?: 0) - size / 2 + size) % size
            for (i in 0 until size) {
                viewModel.windowStartData[(i + viewModel.startIndex) % size].value = newWindowStart
                newWindowStart += WEEK_MILLS
            }
            return true
        }


    }


}