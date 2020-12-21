package com.stupidtree.hita.ui.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife

/**
 * 本项目所有Fragment的基类
 * @param <TextRecord> 泛型指定的是Fragment所绑定的ViewModel类型
</TextRecord> */
abstract class BaseFragmentWithReceiver<T : ViewModel> : BaseFragment<T>() {
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