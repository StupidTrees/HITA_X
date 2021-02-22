package com.stupidtree.hita.ui.main.timeline

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.databinding.FragmentTimelineBinding
import com.stupidtree.hita.ui.base.BaseFragment
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.utils.EventsUtils

class FragmentTimeLine : BaseFragment<FragmentTimelineViewModel, FragmentTimelineBinding>(){
    private var listAdapter: TimelineListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefresh()
    }


    private fun initListAndAdapter() {
        listAdapter = TimelineListAdapter(this.requireContext(), mutableListOf())
        binding?.list?.setItemViewCacheSize(Int.MAX_VALUE)
        binding?.list?.adapter = listAdapter
        binding?.list?.layoutManager = LinearLayoutManager(requireContext())
        listAdapter!!.setOnItemClickListener(object : BaseListAdapter.OnItemClickListener<EventItem> {
            override fun onItemClick(data: EventItem?, card: View?, position: Int) {
                data?.let { EventsUtils.showEventItem(requireContext(), it) }
            }
        })
        listAdapter!!.setOnHintConfirmedListener(object : TimelineListAdapter.OnHintConfirmedListener {

            override fun onConfirmed(v: View?, position: Int, hint: EventItem?) {
                v?.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                val newL: MutableList<EventItem> = ArrayList(listAdapter!!.mBeans)
                newL.remove(hint)
                listAdapter!!.notifyItemChangedSmooth(newL)
                //defaultSP.edit().putBoolean(hintTag, true).apply()
            }
        })
    }


    override fun initViews(view: View) {
        initListAndAdapter()
        viewModel.todayEventsLiveData.observe(this) {
            listAdapter?.notifyItemChangedSmooth(it)
            val holder: RecyclerView.ViewHolder? = binding?.list?.findViewHolderForAdapterPosition(0)
            if (holder != null) {
                val header: TimelineListAdapter.timelineHeaderHolder = holder as TimelineListAdapter.timelineHeaderHolder
                header.UpdateHeadView()
            }
        }

    }

    override fun initViewBinding(): FragmentTimelineBinding {
        return FragmentTimelineBinding.inflate(layoutInflater)
    }


    override fun getViewModelClass(): Class<FragmentTimelineViewModel> {
        return FragmentTimelineViewModel::class.java
    }



}