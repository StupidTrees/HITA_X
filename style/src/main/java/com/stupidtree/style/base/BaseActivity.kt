package com.stupidtree.style.base

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.stupidtree.style.R

/**
 * 本项目所有Activity的基类
 * @param <TextRecord> 泛型T指定的是这个页面绑定的ViewModel
</TextRecord> */
abstract class BaseActivity<T : ViewModel, V : ViewBinding> : AppCompatActivity() {
    /**
     * 每个Acitivity绑定一个ViewModel
     */
    lateinit var viewModel: T

    protected lateinit var binding: V

    /**
     * 所有继承BaseActivity的Activity都要实现以下几个函数
     */

    //获取这个Activity的布局id
    protected abstract fun initViewBinding(): V

    //获取ViewModel的具体类型
    protected abstract fun getViewModelClass(): Class<T>

    //为Activity中的View设置行为
    protected abstract fun initViews()
    override fun onCreate(savedInstanceState: Bundle?) {
        setTranslucentStatusBar()
        super.onCreate(savedInstanceState)
        binding = initViewBinding()
        setContentView(binding.root)
        //所有的Activity都可以使用ButterKnife进行View注入，十分方便
        //ButterKnife.bind(this)
        //对ViewModel进行初始化
        getViewModelClass().let {
            viewModel = if (it.superclass == AndroidViewModel::class.java) {
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(it)
            } else {
                ViewModelProvider(this).get(it)
            }
        }

        //调用这个函数
        initViews()
    }

    /**
     * 设置Activity的窗口属性
     */
    protected open fun setTranslucentStatusBar() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            } // Night mode is active, we're using dark theme
        }

    }

    /**
     * 设置一个带返回按钮的Toolbar
     * @param toolbar 已经初始化的一个Toolbar
     */
    protected fun setToolbarActionBack(toolbar: Toolbar) {
        //设置为系统支持的ActionBar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //左侧添加一个默认的返回图标
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }

    /**
     * 获取这个Activity本身
     * @return Activity自己
     */
    fun getThis(): BaseActivity<T, V> {
        return this
    }

    fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    fun getTextColorSecondary(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.textColorSecondary, typedValue, true)
        return typedValue.data
    }

    fun getColorControlNormal(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)
        return typedValue.data
    }

    fun getBackgroundColorSecondAsTint(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.backgroundColorSecondAsTint, typedValue, true)
        return typedValue.data
    }
}