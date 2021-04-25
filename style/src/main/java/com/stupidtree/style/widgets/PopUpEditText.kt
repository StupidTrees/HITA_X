package com.stupidtree.style.widgets

import android.view.View
import androidx.annotation.StringRes
import com.stupidtree.style.R
import com.stupidtree.style.databinding.DialogBottomEditTextBinding


/**
 * 圆角的文本框底部弹窗
 */
class PopUpEditText : TransparentBottomSheetDialog<DialogBottomEditTextBinding>() {

    @StringRes
    var init_title: Int? = null

    @StringRes
    var init_hint: Int? = null
    var init_text: String? = null
    var onConfirmListener: OnConfirmListener? = null


    interface OnConfirmListener {
        fun OnConfirm(text: String)
    }

    override fun onStart() {
        super.onStart()
        binding.text.requestFocus()
    }

    fun setTitle(@StringRes title: Int): PopUpEditText {
        init_title = title
        return this
    }

    fun setText(text: String?): PopUpEditText {
        init_text = text
        return this
    }

    fun setHint(@StringRes hint: Int): PopUpEditText {
        init_hint = hint
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener): PopUpEditText {
        this.onConfirmListener = onConfirmListener
        return this
    }


    override fun initViews(v: View) {
        if (init_title != null) {
            binding.title.setText(init_title!!)
        }
        if (init_hint != null) {
            binding.text.setHint(init_hint!!)
        }
        if (init_text != null) {
            binding.text.setText(init_text)
        }
        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            if (onConfirmListener != null) {
                onConfirmListener!!.OnConfirm(binding.text.text.toString())
            }
            dismiss()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_edit_text
    }

    override fun initViewBinding(v: View): DialogBottomEditTextBinding {
        return DialogBottomEditTextBinding.bind(v)
    }
}