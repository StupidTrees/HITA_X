package com.stupidtree.style.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.stupidtree.style.R
import com.stupidtree.style.base.BaseActivity


/**
 * 透明背景的底部弹窗Fragment
 */
abstract class TransparentDialog<V:ViewBinding> : DialogFragment() {
    lateinit var binding:V
    fun getColorPrimary(): Int {
        return (activity as BaseActivity<*, *>).getColorPrimary()
    }

    fun getColorControlNormal(): Int {
        return (activity as BaseActivity<*, *>).getColorControlNormal()
    }
    fun getTextColorSecondary(): Int {
        return (activity as BaseActivity<*, *>).getTextColorSecondary()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(context,R.style.AppTheme)
        val v = inflater.cloneInContext(contextThemeWrapper)
            .inflate(getLayoutId(), container, false)
        binding = initViewBinding(v)
        initViews(binding.root)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val attributes: WindowManager.LayoutParams? = window?.attributes
        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT //满屏
        window?.attributes = attributes;
    }

    protected abstract fun getLayoutId():Int
    protected abstract fun initViewBinding(v:View):V
    protected abstract fun initViews(v: View)
}