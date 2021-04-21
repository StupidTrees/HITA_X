package com.stupidtree.style.widgets

import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import com.stupidtree.style.R
import com.stupidtree.style.databinding.DialogBottomTextBinding

/**
 * 圆角的文本框底部弹窗
 */
class PopUpText : TransparentBottomSheetDialog<DialogBottomTextBinding>() {


    @StringRes
    var init_title: Int? = null
    private var init_text: String? = null
    var onConfirmListener: OnConfirmListener? = null

    interface OnConfirmListener {
        open fun OnConfirm()
    }

    override fun onStart() {
        super.onStart()
        binding.text.requestFocus()
    }

    fun setTitle(@StringRes title: Int): PopUpText {
        init_title = title
        return this
    }

    fun setDialogCancelable(cancelable: Boolean): PopUpText {
        isCancelable = cancelable
        return this
    }

    fun setText(text: String?): PopUpText {
        init_text = text
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener): PopUpText {
        this.onConfirmListener = onConfirmListener
        return this
    }


    override fun initViews(v: View) {
        if (init_title != null) {
            binding.title.setText(init_title!!)
        }
        if (!TextUtils.isEmpty(init_text)) {
            binding.text.text = init_text
            binding.text.visibility = View.VISIBLE
        } else {
            binding.text.visibility = View.GONE
        }
        if (isCancelable) {
            binding.cancel.visibility = View.VISIBLE
        } else {
            binding.cancel.visibility = View.GONE
        }
        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            if (onConfirmListener != null) {
                onConfirmListener!!.OnConfirm()
            }
            dismiss()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_text
    }

    override fun initViewBinding(v: View): DialogBottomTextBinding {
        return DialogBottomTextBinding.bind(v)
    }
}