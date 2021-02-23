package com.stupidtree.hita.ui.base

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
abstract class BaseFragmentClassic<T : ViewModel> : Fragment() {

    //本Fragment绑定的ViewModel
    var viewModelInit:Boolean = false
    protected lateinit var viewModel: T

    /**
     * 以下四个函数的作用和BaseActivity里的四个函数类似
     */
    protected abstract fun getViewModelClass(): Class<T>
    abstract fun getLayoutId():Int
    protected abstract fun initViews(view: View)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getViewModelClass().let {
            viewModel = if (it.superclass == AndroidViewModel::class.java || it.superclass.superclass == AndroidViewModel::class.java) {
                ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(it)
            } else {
                ViewModelProvider(this).get(it)
            }
        }
        viewModelInit = true
        val v = layoutInflater.inflate(getLayoutId(),container,false)
        initViews(v)
        return v
    }



    fun getColorPrimary():Int{
        return (activity as BaseActivity<*, *>).getColorPrimary()
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}