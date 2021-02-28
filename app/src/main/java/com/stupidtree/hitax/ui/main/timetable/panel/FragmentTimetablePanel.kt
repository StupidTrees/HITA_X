package com.stupidtree.hitax.ui.main.timetable.panel

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.view.HapticFeedbackConstants
import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.databinding.FragmentTimetablePanelBinding
import com.stupidtree.hitax.ui.base.TransparentModeledBottomSheetDialog

class FragmentTimetablePanel : TransparentModeledBottomSheetDialog<TimetablePanelViewModel, FragmentTimetablePanelBinding>() {


    override fun initViews(view: View) {
        bindLiveData()
        binding?.reset?.setOnClickListener {
            viewModel.startResetColor()
        }
        binding?.from?.setOnClickListener {
            viewModel.startDateLiveData.value?.let {
                val minute = it % 100
                val hour = it / 100
                TimePickerDialog(requireContext(), { view, hourOfDay, minute ->
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.changeStartDate(hourOfDay, minute)
                }, hour, minute, true)
                        .show()
            }
        }
        binding?.drawbglines?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDrawBGLines(isChecked)
        }
        binding?.colorEnable?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setColorEnable(isChecked)
        }
        binding?.fadeEnable?.setOnCheckedChangeListener{_,isChecked->
            viewModel.setFadeEnable(isChecked)
        }

    }

    @SuppressLint("SetTextI18n")
    fun bindLiveData() {
        viewModel.drawBGLinesLiveData.observe(this) {
            binding?.drawbglines?.isChecked = it
        }
        viewModel.startDateLiveData.observe(this) {
            binding?.from?.text = TimeInDay(it/100,it%100).toString()
        }
        viewModel.colorEnableLiveData.observe(this) {
            binding?.colorEnable?.isChecked = it
        }
        viewModel.fadeEnableLiveData.observe(this) {
            binding?.fadeEnable?.isChecked = it
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_timetable_panel
    }

    override fun getViewModelClass(): Class<TimetablePanelViewModel> {
        return TimetablePanelViewModel::class.java
    }

    override fun initViewBinding(v: View): FragmentTimetablePanelBinding {
        return FragmentTimetablePanelBinding.bind(v)
    }
}