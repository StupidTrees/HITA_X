package com.stupidtree.hitax.ui.main.timetable.inner

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.databinding.FragmentTimetablePageBinding
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.hitax.ui.main.timetable.views.TimeTableView
import com.stupidtree.hitax.utils.EventsUtils
import com.stupidtree.hitax.utils.TimeTools
import java.util.*

class TimetablePageFragment : BaseFragment<TimetablePageViewModel, FragmentTimetablePageBinding>() {
    private val topDateTexts = arrayOfNulls<TextView>(8) //顶部日期文本
    private var initStartDate:Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false
    }

    override fun onStart() {
        super.onStart()
        initStartDate?.let {
            //Log.e("use_init", this.toString()+","+TimeUtils.printDate(it))
            viewModel.setStartDate(it)
            initStartDate = null
            return
        }
        viewModel.startDateLiveData.value?.let {
            viewModel.setStartDate(it)
        }
    }


    fun resetWeek(date: Long,force:Boolean = false) {
        initStartDate = if(viewModelInit){
            viewModel.setStartDate(date,force)
            null
        }else{
            //Log.e("set_init",this.toString()+","+TimeUtils.printDate(date))
            date
        }
    }




    override fun initViews(view: View) {
        initDateTextViews()
        binding?.timetableView?.init()
        binding?.timetableView?.setOnCardClickListener(object : TimeTableView.OnCardClickListener {
            override fun onEventClick(v: View, eventItem: EventItem) {
                EventsUtils.showEventItem(requireContext(), eventItem)
            }

            override fun onDuplicateEventClick(v: View, eventItems: List<EventItem>) {
                // EventsUtils.showEventItem(requireContext(), eventItems)
            }
        })
        binding?.timetableView?.setOnCardLongClickListener(object : TimeTableView.OnCardLongClickListener {
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
//        viewModel.eventsOfThisWeek.observe(this) {
//            viewModel.startDateLiveData.value?.let { date ->
//                val dataHash = it.hashCode()
//                if (dataHash != viewModel.dataHashCode ||
//                        binding?.timetableView?.childCount ?: 0 < it.size) {
//                    viewModel.dataHashCode = dataHash
//                    binding?.timetableView?.notifyRefresh(date, it,viewModel.timetableStyleLiveData.value!!)
//                }
//            }
//            // }
//        }
        viewModel.startDateLiveData.observe(this) { date ->
            val startDate = Calendar.getInstance()
            startDate.timeInMillis = date
            //
            /*显示上方日期*/
            topDateTexts[0]?.text = requireContext().resources.getStringArray(R.array.months)[startDate[Calendar.MONTH]]
            val temp = Calendar.getInstance()
            for (k in 1..7) {
                temp.time = startDate.time
                temp.add(Calendar.DATE, k - 1)
                topDateTexts[k]!!.text = temp[Calendar.DAY_OF_MONTH].toString()
            }
        }
        viewModel.timetableViewLiveData.observe(this){
            viewModel.startDateLiveData.value?.let { date ->
                val dataHash = it.hashCode()
                if (it.second!=binding?.timetableView?.styleSheet//样式不同也更新
                        ||dataHash != viewModel.dataHashCode ||
                        binding?.timetableView?.childCount ?: 0 < it.first.size) {
                    viewModel.dataHashCode = dataHash
                    binding?.timetableView?.notifyRefresh(date, it.first,it.second)
                }else{
                    binding?.timetableView?.setStartDate(date)
                }
            }
        }
    }

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


    override fun getViewModelClass(): Class<TimetablePageViewModel> {
        return TimetablePageViewModel::class.java
    }

    override fun initViewBinding(): FragmentTimetablePageBinding {
        return FragmentTimetablePageBinding.inflate(layoutInflater)
    }


    companion object {
        fun newInstance(): TimetablePageFragment {
            return TimetablePageFragment()
        }
    }
}