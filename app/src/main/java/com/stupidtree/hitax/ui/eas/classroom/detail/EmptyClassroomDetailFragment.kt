package com.stupidtree.hitax.ui.eas.classroom.detail

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.databinding.ActivityEasClassroomSecondBinding
import com.stupidtree.hitax.ui.base.TransparentBottomSheetDialog
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem
import com.stupidtree.hitax.utils.TimeTools

class EmptyClassroomDetailFragment(
    private val term: TermItem,
    private val week: Int,
    private val classroom: ClassroomItem, private val scheduleStructure: List<TimePeriodInDay>
) : TransparentBottomSheetDialog<ActivityEasClassroomSecondBinding>() {
    private lateinit var listAdapter: EmptyClassroomDetailAdapter


    private fun refreshList(dow: Int) {
        var i = 1
        val r = mutableListOf<HashMap<String, String?>>()
        for (tp in scheduleStructure) {
            val hm = HashMap<String, String?>()
            hm["time"] = "${tp.from} - ${tp.to}"
            hm["number"] = getString(R.string.number_schedule, i)
            hm["state"] = "空"
            for (jo in classroom.scheduleList) {
                if (jo.optString("XQJ") == dow.toString()
                    && jo.optString("XJ") == i.toString() //找到这节课的占用
                ) {
                    if (!jo.optString("JYBJ").isNullOrEmpty() && jo.optString("JYBJ") != "null") {
                        hm["state"] = jo.optString("JYBJ")
                        break
                    } else if (!jo.optString("PKBJ")
                            .isNullOrEmpty() && jo.optString("PKBJ") != "null"
                    ) {
                        hm["state"] = jo.optString("PKBJ")
                        break
                    }
                }
            }
            r.add(hm)
            i++
        }
        listAdapter.notifyDataSetChanged(r.toList())
        binding.list.scheduleLayoutAnimation()
    }


    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        val currentDow = TimeTools.getDow(System.currentTimeMillis())
        binding.tabs.selectTab(binding.tabs.getTabAt(currentDow - 1))
        refreshList(currentDow)
        val currentNumber = TimeTools.getCurrentScheduleNumber(scheduleStructure)
        binding.list.smoothScrollToPosition(currentNumber / 10)
    }


    override fun initViewBinding(v: View): ActivityEasClassroomSecondBinding {
        return ActivityEasClassroomSecondBinding.bind(v)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_eas_classroom_second
    }

    @SuppressLint("SetTextI18n")
    override fun initViews(v: View) {
        binding.subtitle.text = "${term.name}${getString(R.string.week_title, week)}"
        binding.title.text = getString(R.string.empty_classroom_format, classroom.name)
        listAdapter = EmptyClassroomDetailAdapter(requireContext(), mutableListOf())
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(
            requireContext()
        )
        val dows: Array<String> = resources.getStringArray(R.array.dow2)
        for (i in 0 until binding.tabs.tabCount) {
            binding.tabs.getTabAt(i)?.text = dows[i]
        }
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                refreshList(tab.position + 1)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}