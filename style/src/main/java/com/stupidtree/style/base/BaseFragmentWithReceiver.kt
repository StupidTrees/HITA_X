package com.stupidtree.style.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

/**
 * 本项目所有Fragment的基类
 * @param <TextRecord> 泛型指定的是Fragment所绑定的ViewModel类型
</TextRecord> */
abstract class BaseFragmentWithReceiver<T : ViewModel,V:ViewBinding> : BaseFragment<T, V>() {
    abstract var receiver: BroadcastReceiver
    abstract fun getIntentFilter():IntentFilter
    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(receiver, getIntentFilter())
    }
    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(receiver)
    }
}