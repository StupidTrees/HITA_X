package com.stupidtree.hitax.ui.widgets

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.DialogBottomCalendarBinding
import com.stupidtree.hitax.ui.base.TransparentBottomSheetDialog
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.hitax.utils.TimeTools.TTY_REPLACE
import java.util.*

/**
 * 圆角的文本框底部弹窗
 */
class PopUpCalendarPicker : TransparentBottomSheetDialog<DialogBottomCalendarBinding>() {

    @StringRes
    var initTitle: Int? = null
    private var initValue: Long? = null

    private var onConfirmListener: OnConfirmListener? = null
    var dateLiveData: MutableLiveData<Calendar> = MutableLiveData()

    interface OnConfirmListener {
        fun onConfirm(c: Calendar)
    }

    fun setTitle(@StringRes title: Int): PopUpCalendarPicker {
        initTitle = title
        return this
    }


    fun setInitValue(value: Long?): PopUpCalendarPicker {
        initValue = value
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener): PopUpCalendarPicker {
        this.onConfirmListener = onConfirmListener
        return this
    }


    override fun onStart() {
        super.onStart()
        initValue?.let {
            binding.calendar.setDate(it, true, true)
            val c = Calendar.getInstance()
            c.timeInMillis = it
            dateLiveData.value = c
        }
    }

    override fun initViews(v: View) {
        initTitle?.let { binding.title.setText(it) }
        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            dateLiveData.value?.let { it1 ->
                onConfirmListener?.onConfirm(it1)
                dismiss()
            }
        }
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth)
            dateLiveData.value = c
        }
        dateLiveData.observe(this) {
            binding.title.text = TimeTools.getDateString(requireContext(), it, true, TTY_REPLACE)
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_calendar
    }

    override fun initViewBinding(v: View): DialogBottomCalendarBinding {
        return DialogBottomCalendarBinding.bind(v)
    }
}