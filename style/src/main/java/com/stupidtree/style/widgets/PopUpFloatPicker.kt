package com.stupidtree.style.widgets

import android.view.View
import androidx.annotation.StringRes
import com.stupidtree.style.R
import com.stupidtree.style.databinding.DialogBottomFloatPickerBinding
import java.util.*
import kotlin.math.roundToInt

class PopUpFloatPicker :
    TransparentBottomSheetDialog<DialogBottomFloatPickerBinding>() {
    private var a = 0
    private var b = 0
    private var mOnDialogConformListener: OnDialogConformListener? = null

    @StringRes
    private var init_title = 0
    private var init_a: Int = 0
    private var init_b: Int = 0

    protected override fun initViewBinding(v: View): DialogBottomFloatPickerBinding {
        return DialogBottomFloatPickerBinding.bind(v)
    }

    interface OnDialogConformListener {
        fun onClick(result:Float)
    }

    fun setDialogTitle(@StringRes title: Int): PopUpFloatPicker {
        init_title = title
        return this
    }

    fun setInitialValue(x:Float): PopUpFloatPicker {
        init_a = x.toInt()
        init_b = (10*(x-init_a)).roundToInt()
        return this
    }

    fun setOnDialogConformListener(m: OnDialogConformListener): PopUpFloatPicker {
        mOnDialogConformListener = m
        return this
    }

    override fun initViews(v: View) {
        val aTexts: MutableList<String> = ArrayList()
        val bTexts: MutableList<String> = ArrayList()
        for (i in 0..9) aTexts.add(""+i)
        for (i in 0..9) bTexts.add(""+i)
        binding.a.setEntries(aTexts)
        binding.b.setEntries(bTexts)
        binding.a.setOnWheelChangedListener { _, _, newIndex ->
            a = newIndex
        }
        binding.b.setOnWheelChangedListener { _, _, newIndex ->
           b = newIndex
        }
        binding.confirm.setOnClickListener {
            mOnDialogConformListener?.onClick(
                a.toFloat()+b.toFloat()/10
            )
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.pickTimeLayout.visibility = View.VISIBLE
        binding.title.setText(init_title)
        binding.a.currentIndex = init_a
        binding.b.currentIndex = init_b
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_float_picker
    }

    init {
        isCancelable = false
    }
}