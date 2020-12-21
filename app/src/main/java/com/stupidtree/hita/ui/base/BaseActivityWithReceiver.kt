package com.stupidtree.hita.ui.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import butterknife.ButterKnife
import com.stupidtree.hita.R

/**
 * 本项目所有Activity的基类
 * @param <TextRecord> 泛型T指定的是这个页面绑定的ViewModel
</TextRecord> */
abstract class BaseActivityWithReceiver<T : ViewModel?> :BaseActivity<T>() {    abstract var receiver: BroadcastReceiver
    abstract fun getIntentFilter(): IntentFilter
    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, getIntentFilter())
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }
}