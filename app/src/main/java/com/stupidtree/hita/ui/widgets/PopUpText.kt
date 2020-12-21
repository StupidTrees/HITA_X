package com.stupidtree.hita.ui.widgets

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import butterknife.BindView
import com.stupidtree.hita.R
import com.stupidtree.hita.ui.widgets.TransparentBottomSheetDialog

/**
 * 圆角的文本框底部弹窗
 */
class PopUpText : TransparentBottomSheetDialog() {
    @JvmField
    @BindView(R.id.title)
    var title: TextView? = null

    @JvmField
    @BindView(R.id.text)
    var textView: TextView? = null

    @JvmField
    @BindView(R.id.confirm)
    var confirm: View? = null

    @JvmField
    @BindView(R.id.cancel)
    var cancel: View? = null

    @StringRes
    var init_title: Int? = null
    private var init_text: String? = null
    var onConfirmListener: OnConfirmListener? = null

    interface OnConfirmListener {
        open fun OnConfirm()
    }

    override fun onStart() {
        super.onStart()
        textView!!.requestFocus()
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

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_text
    }

    override fun initViews(v: View) {
        if (init_title != null) {
            title!!.setText(init_title!!)
        }
        if (!TextUtils.isEmpty(init_text)) {
            textView!!.text = init_text
            textView!!.visibility = View.VISIBLE
        } else {
            textView!!.visibility = View.GONE
        }
        if (isCancelable) {
            cancel!!.visibility = View.VISIBLE
        } else {
            cancel!!.visibility = View.GONE
        }
        cancel!!.setOnClickListener { view: View? -> dismiss() }
        confirm!!.setOnClickListener { view: View? ->
            if (onConfirmListener != null) {
                onConfirmListener!!.OnConfirm()
            }
            dismiss()
        }
    }
}