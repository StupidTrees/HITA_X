package com.stupidtree.hita.ui.widgets

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import butterknife.BindView
import com.stupidtree.hita.R

/**
 * 圆角的文本框底部弹窗
 */
class PopUpEditText : TransparentBottomSheetDialog() {
    @BindView(R.id.title)
    lateinit var title: TextView

    @BindView(R.id.text)
    lateinit var editText: EditText

    @BindView(R.id.confirm)
    lateinit var confirm: View

    @BindView(R.id.cancel)
    lateinit var cancel: View

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
        editText!!.requestFocus()
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

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_edit_text
    }

    override fun initViews(v: View) {
        if (init_title != null) {
            title!!.setText(init_title!!)
        }
        if (init_hint != null) {
            editText.setHint(init_hint!!)
        }
        if (init_text != null) {
            editText.setText(init_text)
        }
        cancel.setOnClickListener { view: View? -> dismiss() }
        confirm!!.setOnClickListener { view: View? ->
            if (onConfirmListener != null) {
                onConfirmListener!!.OnConfirm(editText!!.text.toString())
            }
            dismiss()
        }
    }
}