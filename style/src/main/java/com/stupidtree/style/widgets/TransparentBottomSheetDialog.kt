package com.stupidtree.style.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stupidtree.style.R

/**
 * 透明背景的底部弹窗Fragment
 */
abstract class TransparentBottomSheetDialog<V:ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding:V


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(context,R.style.AppTheme)
        val v = inflater.cloneInContext(contextThemeWrapper)
            .inflate(getLayoutId(), container, false)
        binding = initViewBinding(v)
        initViews(binding.root)
        return binding.root
    }

    protected abstract fun getLayoutId():Int
    protected abstract fun initViewBinding(v:View):V
    protected abstract fun initViews(v: View)
}