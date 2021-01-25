package com.stupidtree.hita.ui.timetable.fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TimeInDay
import com.stupidtree.hita.databinding.FragmentTimetablePageBinding
import com.stupidtree.hita.ui.base.BaseFragment
import com.stupidtree.hita.ui.timetable.views.TimeTableBlockView.TimeTablePreferenceRoot
import com.stupidtree.hita.ui.timetable.views.TimeTableViewGroup
import java.util.*

class TimetablePageFragment : BaseFragment<TimetablePageViewModel, FragmentTimetablePageBinding>() {
    private val topDateTexts = arrayOfNulls<TextView>(8) //顶部日期文本

    private fun initDateTextViews() {
        topDateTexts[0] = binding?.ttTvMonth
        topDateTexts[1] = binding?.ttTvDay1
        topDateTexts[2] = binding?.ttTvDay2
        topDateTexts[3] = binding?.ttTvDay3
        topDateTexts[4] = binding?.ttTvDay4
        topDateTexts[5] = binding?.ttTvDay5
        topDateTexts[6] = binding?.ttTvDay6
        topDateTexts[7] = binding?.ttTvDay7
    }

    private fun refreshDateViews(date: Long) {
        //Log.e("dateView",com.stupidtree.hita.utils.TimeUtils.printDate(date))
        val startDate = Calendar.getInstance()
        startDate.timeInMillis = date
        try {
            /*显示上方日期*/
            topDateTexts[0]?.text = requireContext().resources.getStringArray(R.array.months)[startDate[Calendar.MONTH]]
            val temp = Calendar.getInstance()
            for (k in 1..7) {
                temp.time = startDate.time
                temp.add(Calendar.DATE, k - 1)
                topDateTexts[k]!!.text = temp[Calendar.DAY_OF_MONTH].toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()

        arguments?.getLong("date")?.let {
            refreshDateViews(it)
            viewModel.startRefresh(it)
        }

    }
//
//    fun NotifyRefresh() {
//        if (isResumed) {
//            willRefreshOnResume = false
//            refreshPageView(pageWeek)
//        } else willRefreshOnResume = true
//    }



    fun setWeek(date:Long){
        arguments?.getLong("date")?.let {
            val old = it
            if(date< old ||date>old+1000*60*60*24*7){
                val b = Bundle()
                b.putLong("date", date)
                arguments = b
//                if(isAdded){
//                    viewModel.startRefresh(date)
//                }
            }
        }

    }



    override fun initViews(view: View) {
        initDateTextViews()
        binding?.timetableView?.init(object :TimeTablePreferenceRoot{
            override val isColorEnabled: Boolean = false
            override val cardTitleColor: String = "white"
            override val subTitleColor: String = "white"
            override val iconColor: String = "white"

            override fun willBoldText(): Boolean {
                return true
            }

            override fun drawBGLine(): Boolean {
                 return true
            }

            override fun cardIconEnabled(): Boolean {
               return true
            }

            override val cardOpacity: Int
                get() = 75
            override val cardHeight: Int
                get() = 180
            override val startTime: TimeInDay
                get() = TimeInDay(8,0)
            override val todayBGColor: Int
                get() = Color.BLACK
            override val titleGravity: Int
                get() = Gravity.CENTER
            override val colorPrimary: Int
                get() = getColorPrimary()
            override val colorAccent: Int
                get() = getColorPrimary()
            override val titleAlpha: Int
                get() = 100
            override val subtitleAlpha: Int
                get() = 60

            override fun animEnabled(): Boolean {
                return true
            }

            override val cardBackground: String
                get() = "primary"
            override val tTPreference: SharedPreferences
                get() = tTPreference

            override fun drawNowLine(): Boolean {
                return true
            }

        })
        binding?.timetableView?.setOnCardClickListener(object : TimeTableViewGroup.OnCardClickListener {
            override fun onEventClick(v: View, eventItem: EventItem) {
                //EventsUtils.showEventItem(requireContext(), eventItem)
            }

            override fun onDuplicateEventClick(v: View, eventItems: List<EventItem>) {
                // EventsUtils.showEventItem(requireContext(), eventItems)
            }
        })
        binding?.timetableView?.setOnCardLongClickListener(object : TimeTableViewGroup.OnCardLongClickListener {
            override fun onEventLongClick(v: View, ei: EventItem): Boolean {
//                val pm = PopupMenu(requireContext(), v)
//                pm.inflate(R.menu.menu_opr_timetable)
//                pm.setOnMenuItemClickListener { item ->
//                    if (item.itemId == R.id.opr_delete) {
//                        val ad = AlertDialog.Builder(requireContext()).setNegativeButton(R.string.button_cancel, null)
//                                .setPositiveButton(R.string.button_confirm) { dialog, which ->
//                                    val ef: ExplosionField = ExplosionField.attach2Window(requireActivity())
//                                    ef.explode(v)
//                                    deleteEventsTask(this@FragmentTimeTablePage, ei).execute()
//                                }.create()
//                        ad.setTitle(R.string.dialog_title_sure_delete)
//                        if (ei.eventType === COURSE) {
//                            ad.setMessage("删除课程后,可以通过导入课表或同步云端数据恢复初始课表")
//                        }
//                        ad.show()
//                    }
//                    true
//                }
//                pm.show()
                return true
            }

            override fun onDuplicateEventClick(v: View, eventItems: List<EventItem>): Boolean {
//                val pm = PopupMenu(requireContext(), v)
//                pm.inflate(R.menu.menu_opr_timetable)
//                pm.setOnMenuItemClickListener { item ->
//                    if (item.itemId == R.id.opr_delete) {
//                        val ad = AlertDialog.Builder(requireContext()).setNegativeButton(R.string.button_cancel, null)
//                                .setPositiveButton(R.string.button_confirm) { dialog, which ->
//                                    val ef: ExplosionField = ExplosionField.attach2Window(requireActivity())
//                                    ef.explode(v)
//                                    deleteEventsTask(this@FragmentTimeTablePage, eventItems).execute()
//                                }.create()
//                        ad.setTitle(R.string.dialog_title_sure_delete)
//                        if (eventItems.size > 0 && eventItems[0].getEventType() === COURSE) {
//                            ad.setMessage("删除课程后,可以通过导入课表或同步云端数据恢复初始课表")
//                        }
//                        ad.show()
//                    }
//                    true
//                }
//                pm.show()
                return true
            }
        })
        viewModel.eventsOfThisWeek.observe(this) {
            binding?.timetableView?.removeAllViews()
            binding?.timetableView?.notifyRefresh()
            for (o in it) {
                binding?.timetableView?.addBlock(o)
            }
//            if (pageWeek == TimetableCore.getInstance(HContext).getThisWeekOfTerm() && root!!.drawNowLine() && HTime(TimetableCore.getNow()).after(root!!.startTime)) {
//                binding?.timetableView?!!.addView(TimeTableNowLine(requireContext(), getIconColorSecond()))
//            }
        }
    }

    override fun getViewModelClass(): Class<TimetablePageViewModel> {
        return TimetablePageViewModel::class.java
    }

    override fun initViewBinding(): FragmentTimetablePageBinding {
        return FragmentTimetablePageBinding.inflate(layoutInflater)
    }

//    internal class refreshPageTask(listRefreshedListener: OperationListener<List<*>?>?, var week: Int) : BaseOperationTask<List<*>?>(listRefreshedListener) {
//        protected fun doInBackground(listRefreshedListener: OperationListener?, vararg booleans: Boolean?): List<*> {
//            val res: MutableList<Any> = ArrayList()
//            if (TimetableCore.getInstance(HContext).getCurrentCurriculum() == null) return res
//            val oneWeekEvent: List<EventItem> = TimetableCore.getInstance(HContext).getEventsWithinWeeks(week, week)
//            for (p in 1..7) {
//                val oneDayEvent: MutableList<EventItem> = ArrayList<EventItem>()
//                for (x in oneWeekEvent) {
//                    if (x.getDOW() === p) oneDayEvent.add(x)
//                }
//                val oneDay: MutableList<Any> = ArrayList()
//                val usedIndex: MutableList<Int> = ArrayList()
//                for (i in oneDayEvent.indices) {
//                    if (usedIndex.contains(i)) continue
//                    val ei: EventItem = oneDayEvent[i]
//                    if (ei.isWholeDay()) {
//                        oneDay.add(ei)
//                        continue
//                    }
//                    val result: MutableList<EventItem> = ArrayList<EventItem>()
//                    result.add(ei)
//                    for (j in i + 1 until oneDayEvent.size) {
//                        val x: EventItem = oneDayEvent[j]
//                        if (x.isWholeDay()) continue
//                        if (ei.startTime.equals(x.startTime) && ei.endTime.equals(x.endTime)) {
//                            result.add(x)
//                            usedIndex.add(j)
//                        }
//                    }
//                    if (result.size > 1 && !ei.isWholeDay()) oneDay.add(result) else oneDay.add(ei)
//                }
//                res.addAll(oneDay)
//            }
//            return res
//        }
//
//        init {
//            id = "refresh"
//        }
//    } //    static class deleteEventsTask extends BaseOperationTask<Boolean> {

    //        List<EventItem> eventItems;
    //
    //        deleteEventsTask(OperationListener listRefreshedListener, EventItem eventItem) {
    //            super(listRefreshedListener);
    //            id = "delete";
    //            this.eventItems = Collections.singletonList(eventItem);
    //        }
    //
    //        deleteEventsTask(OperationListener listRefreshedListener, List<EventItem> eventItem) {
    //            super(listRefreshedListener);
    //            id = "delete";
    //            this.eventItems = eventItem;
    //        }
    //
    //
    //        @Override
    //        protected Boolean doInBackground(OperationListener<Boolean> listRefreshedListener, Boolean... booleans) {
    //            for (EventItem ei : eventItems) {
    //                boolean res = TimetableCore.getInstance(HContext).deleteEvent(ei, false);
    //                if (!res) return false;
    //            }
    //            return true;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(OperationListener<Boolean> listRefreshedListener, Boolean ts) {
    //            super.onPostExecute(listRefreshedListener, ts);
    //        }
    //    }
    //
    //    class OnWholeDayCardClickListener implements View.OnClickListener {
    //
    //        List<EventItem> res;
    //
    //        OnWholeDayCardClickListener(final List<EventItem> res) {
    //            this.res = res;
    //        }
    //
    //        @Override
    //        public void onClick(View v) {
    //            EventsUtils.showEventItem(getBaseActivity(), res);
    //        }
    //    }
    companion object {
        fun newInstance(): TimetablePageFragment {
           // Log.e("newInstance", startDate.toString())
            val b = Bundle()
            b.putLong("date", System.currentTimeMillis())
            val f = TimetablePageFragment()
            f.arguments = b
            return f
        }
    }
}