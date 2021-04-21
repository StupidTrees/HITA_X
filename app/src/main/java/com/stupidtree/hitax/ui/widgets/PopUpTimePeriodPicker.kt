package com.stupidtree.hitax.ui.widgets

import android.view.View
import androidx.annotation.StringRes
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.databinding.DialogBottomTimePeriodPickerBinding
import com.stupidtree.style.widgets.TransparentBottomSheetDialog
import java.util.*

class PopUpTimePeriodPicker() :
    TransparentBottomSheetDialog<DialogBottomTimePeriodPickerBinding>() {
    private var hour1 = 0
    private var minute1 = 0
    private var hour2 = 0
    private var minute2 = 0
    private var timeSet = false
    private var mOnDialogConformListener: OnDialogConformListener? = null

    @StringRes
    private var init_title = 0
    private var init_fT: TimeInDay? = null
    private var init_tT: TimeInDay? = null

    protected override fun initViewBinding(v: View): DialogBottomTimePeriodPickerBinding {
        return DialogBottomTimePeriodPickerBinding.bind(v)
    }

    interface OnDialogConformListener {
        fun onClick(timePeriodInDay: TimePeriodInDay)
    }

    fun setDialogTitle(@StringRes title: Int): PopUpTimePeriodPicker {
        init_title = title
        return this
    }

    fun setInitialValue(fT: TimeInDay?, tT: TimeInDay?): PopUpTimePeriodPicker {
        init_fT = fT
        init_tT = tT
        return this
    }

    fun setOnDialogConformListener(m: OnDialogConformListener):PopUpTimePeriodPicker {
        mOnDialogConformListener = m
        return this
    }

    override fun initViews(v: View) {
        val hourTexts: MutableList<String> = ArrayList()
        val minuteTexts: MutableList<String> = ArrayList()
        for (i in 0..23) if (i < 10) hourTexts.add("0$i") else hourTexts.add(i.toString() + "")
        for (i in 0..59) if (i < 10) minuteTexts.add("0$i") else minuteTexts.add(i.toString() + "")
        binding.hour1.setEntries(hourTexts)
        binding.hour2.setEntries(hourTexts)
        binding.minute1.setEntries(minuteTexts)
        binding.minute2.setEntries(minuteTexts)
        binding.hour1.setOnWheelChangedListener { _, _, newIndex ->
            hour1 = newIndex
            val from = TimeInDay(hour1, minute1)
            val to = TimeInDay(hour2, minute2)
            if (to.before(from)) {
                if (hour2 < newIndex) binding.hour2.currentIndex = newIndex
                if (minute2 < minute1) binding.minute2.currentIndex = minute1
            }
        }
        binding.hour2.setOnWheelChangedListener { _, _, newIndex ->
            hour2 = newIndex
            val from = TimeInDay(hour1, minute1)
            val to = TimeInDay(hour2, minute2)
            if (to.before(from)) {
                if (hour1 > newIndex) binding.hour1.currentIndex = newIndex
                if (minute2 < minute1) binding.minute1.currentIndex = minute2
            }
        }
        binding.minute1.setOnWheelChangedListener { _, _, newIndex ->
            minute1 = newIndex
            val from = TimeInDay(hour1, minute1)
            val to = TimeInDay(hour2, minute2)
            if (to.before(from)) {
                if (hour1 > hour2) binding.hour2.currentIndex = hour1
                if (minute2 < minute1) binding.minute2.currentIndex = newIndex
            }
        }
        binding.minute2.setOnWheelChangedListener { _, _, newIndex ->
            minute2 = newIndex
            val from = TimeInDay(hour1, minute1)
            val to = TimeInDay(hour2, minute2)
            if (to.before(from)) {
                if (hour1 > hour2) binding.hour1.currentIndex = hour2
                if (minute2 < minute1) binding.minute1.currentIndex = newIndex
            }
        }
        binding.confirm.setOnClickListener {
            timeSet = true
            mOnDialogConformListener?.onClick(TimePeriodInDay(TimeInDay(hour1, minute1),TimeInDay(hour2, minute2)))
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.pickTimeLayout.visibility = View.VISIBLE
        val now = Calendar.getInstance()
        binding.title.setText(init_title)
        binding.hour1.currentIndex = init_fT?.hour ?: now.get(Calendar.HOUR_OF_DAY)
        binding.minute1.currentIndex = init_fT?.minute ?: now.get(Calendar.MINUTE)
        binding.hour2.currentIndex = init_tT?.hour ?: now.get(Calendar.HOUR_OF_DAY)
        binding.minute2.currentIndex = init_tT?.minute ?: now.get(Calendar.MINUTE)
        timeSet = true
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_time_period_picker
    }

    init {
       isCancelable = false
    }
}