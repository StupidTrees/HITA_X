package com.stupidtree.hitax.ui.base

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.R

/**
 * 本项目所有Activity的基类
 * @param <TextRecord> 泛型T指定的是这个页面绑定的ViewModel
</TextRecord> */
abstract class BaseActivityCompose<T : ViewModel> : AppCompatActivity() {
    /**
     * 每个Acitivity绑定一个ViewModel
     */
    protected lateinit var viewModel: T

    /**
     * 所有继承BaseActivity的Activity都要实现以下几个函数
     */

    //获取ViewModel的具体类型
    protected abstract fun getViewModelClass(): Class<T>

    //为Activity中的View设置行为
    protected abstract fun initViews()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //对ViewModel进行初始化
        getViewModelClass().let {
            viewModel =
                if (it.superclass == AndroidViewModel::class.java || it.superclass.superclass == AndroidViewModel::class.java) {
                    ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    ).get(it)
                } else {
                    ViewModelProvider(this).get(it)
                }
        }

        //调用这个函数
        initViews()
    }

    /**
     * 设置Activity的窗口属性
     * @param statusBar 是否让状态栏沉浸
     * @param darkColor 状态栏图标的颜色是否显示为深色
     * @param navi 是否让导航栏沉浸（一些手机底部会开启三大金刚）
     */
    protected fun setWindowParams(statusBar: Boolean, darkColor: Boolean, navi: Boolean) {
        if (darkColor) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        /* if (AppCompatDelegate.getDefaultNightMode()!=AppCompatDelegate.MODE_NIGHT_YES&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&darkColor)getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); */if (statusBar) window.addFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        if (navi) window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
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
    fun getThis(): BaseActivityCompose<T> {
        return this
    }

    fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

}


