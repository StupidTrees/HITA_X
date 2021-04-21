package com.stupidtree.style.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

/**
 * 本项目所有Activity的基类
 * @param <TextRecord> 泛型T指定的是这个页面绑定的ViewModel
</TextRecord> */
abstract class BaseActivityWithReceiver<T : ViewModel,V:ViewBinding> : BaseActivity<T, V>() {    abstract var receiver: BroadcastReceiver
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