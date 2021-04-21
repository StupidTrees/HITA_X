package com.stupidtree.style.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * 本项目所有Fragment的基类
 * @param <TextRecord> 泛型指定的是Fragment所绑定的ViewModel类型
</TextRecord> */
abstract class BaseFragment<T : ViewModel,V:ViewBinding> : Fragment() {

    //本Fragment绑定的ViewModel
    protected var viewModelInit:Boolean = false
    protected lateinit var viewModel: T
    protected var binding:V? = null

    /**
     * 以下四个函数的作用和BaseActivity里的四个函数类似
     */
    protected abstract fun getViewModelClass(): Class<T>
    protected abstract fun initViews(view: View)
    protected abstract fun initViewBinding():V
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = initViewBinding()
        getViewModelClass().let {
            viewModel = if (it.superclass == AndroidViewModel::class.java|| it.superclass.superclass == AndroidViewModel::class.java) {
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(it)
            } else {
                ViewModelProvider(this).get(it)
            }
        }
        binding?.let {
            initViews(it.root)
        }
        viewModelInit = true
        return binding?.root
    }


    fun getColorPrimary():Int{
        return (activity as BaseActivity<*, *>).getColorPrimary()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}