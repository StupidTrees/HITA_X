package com.stupidtree.style.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


/**
 * 本项目所有RecycleView的ViewHolder的基类
 */
abstract class BaseViewHolder<V:ViewBinding>(viewBinding: V) : RecyclerView.ViewHolder(viewBinding.root) {
    var binding = viewBinding
}