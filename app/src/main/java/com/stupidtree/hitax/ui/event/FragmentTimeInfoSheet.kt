package com.stupidtree.hitax.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.databinding.DialogBottomEventsBinding
import com.stupidtree.hitax.ui.base.TransparentBottomSheetDialog
import java.util.ArrayList

@SuppressLint("ValidFragment")
class FragmentTimeInfoSheet : TransparentBottomSheetDialog<DialogBottomEventsBinding>() {
    var mode = 0
    private var currentPosition = 0
    private var events: MutableList<EventItem> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            events.addAll(arguments!!.getSerializable("events") as List<EventItem>)
            mode = arguments!!.getInt("mode")
        }
    }


    override fun initViews(v: View) {
        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (currentPosition < tab.position) {
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fragment_slide_from_right,
                            R.anim.fragment_slide_to_left
                        )
                        .replace(R.id.layout, EventItemFragment.newInstance(events[tab.position]))
                        .commitAllowingStateLoss()
                } else {
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fragment_slide_from_left,
                            R.anim.fragment_slide_to_right
                        )
                        .replace(R.id.layout, EventItemFragment.newInstance(events[tab.position]))
                        .commitAllowingStateLoss()
                }
                currentPosition = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        if (events.size <= 1) {
            binding.tabs.visibility = View.GONE
            binding.layout.visibility = View.VISIBLE
            val ei = events[0]
            childFragmentManager.beginTransaction()
                .add(R.id.layout, EventItemFragment.newInstance(ei), "f").commit()
        } else {
            binding.layout.visibility = View.VISIBLE
            binding.tabs.visibility = View.VISIBLE
            binding.tabs.removeAllTabs()
            for (ei in events) {
                binding.tabs.addTab(binding.tabs.newTab().setText(ei.name))
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.layout, EventItemFragment.newInstance(events[0]), "f").commit()
            currentPosition = 0
        }

    }

    fun hasMultiEvents(): Boolean {
        return events.size > 1
    }


    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_events
    }

    override fun initViewBinding(v: View): DialogBottomEventsBinding {
        return DialogBottomEventsBinding.bind(v)
    }

    companion object {
        fun newInstance(events: ArrayList<EventItem>): FragmentTimeInfoSheet {
            val d = Bundle()
            d.putSerializable("events", events)
            val fe = FragmentTimeInfoSheet()
            fe.arguments = d
            return fe
        }
    }
}