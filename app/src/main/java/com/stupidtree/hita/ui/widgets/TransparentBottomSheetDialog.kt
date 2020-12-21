package com.stupidtree.hita.ui.widgets

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stupidtree.hita.R

/**
 * 透明背景的底部弹窗Fragment
 */
abstract class TransparentBottomSheetDialog : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.AppTheme) // your app theme here
        val v = inflater.cloneInContext(contextThemeWrapper).inflate(getLayoutId(), container, false)
        ButterKnife.bind(this, v)
        initViews(v)
        return v
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initViews(v: View)
}