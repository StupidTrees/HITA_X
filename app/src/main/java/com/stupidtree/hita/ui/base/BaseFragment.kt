package com.stupidtree.hita.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * 本项目所有Fragment的基类
 * @param <TextRecord> 泛型指定的是Fragment所绑定的ViewModel类型
</TextRecord> */
abstract class BaseFragment<T : ViewModel,V:ViewBinding> : Fragment() {
//    //本Fragment持有的根View
//    private var rootView: View? = null

    //本Fragment绑定的ViewModel
    protected var viewModel: T? = null
    protected  var _binding:V? = null

    /**
     * 以下四个函数的作用和BaseActivity里的四个函数类似
     */
    protected abstract fun getViewModelClass(): Class<T>
    protected abstract fun initViews(view: View)
    protected abstract fun getLayoutId(): Int
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (rootView == null) {
//            rootView = inflater.inflate(getLayoutId(), container, false)
//        }

        //同样，所有的Fragment也支持ButterKnife的View注入
        //ButterKnife.bind(this, rootView!!)
        //初始化ViewModel
        viewModel = ViewModelProvider(this)[getViewModelClass()]
        _binding?.let {
            initViews(it.root)
        }
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}