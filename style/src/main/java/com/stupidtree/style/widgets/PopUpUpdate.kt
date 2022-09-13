package com.stupidtree.style.widgets

import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import com.stupidtree.style.R
import com.stupidtree.style.databinding.DialogBottomTextBinding
import com.stupidtree.style.databinding.DialogBottomUpdateBinding

/**
 * 圆角的文本框底部弹窗
 */
class PopUpUpdate : TransparentBottomSheetDialog<DialogBottomUpdateBinding>() {


    @StringRes
    var init_title: Int? = null
    private var init_text: String? = null
    var onActionListener: OnActionListener? = null

    interface OnActionListener {
        fun onConfirm()
        fun onCancel()
        fun onSkip()
    }

    override fun onStart() {
        super.onStart()
        binding.text.requestFocus()
    }

    fun setTitle(@StringRes title: Int): PopUpUpdate {
        init_title = title
        return this
    }

    fun setDialogCancelable(cancelable: Boolean): PopUpUpdate {
        isCancelable = cancelable
        return this
    }

    fun setText(text: String?): PopUpUpdate {
        init_text = text
        return this
    }

    fun setOnActionListener(lis: OnActionListener): PopUpUpdate {
        this.onActionListener = lis
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
        binding.skip.setOnClickListener {
            onActionListener?.onSkip()
            dismiss()
        }
        binding.cancel.setOnClickListener {
            onActionListener?.onCancel()
            dismiss() }
        binding.confirm.setOnClickListener {

                onActionListener?.onConfirm()

            dismiss()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_update
    }

    override fun initViewBinding(v: View): DialogBottomUpdateBinding{
        return DialogBottomUpdateBinding.bind(v)
    }
}