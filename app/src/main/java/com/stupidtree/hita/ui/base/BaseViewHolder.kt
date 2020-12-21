package com.stupidtree.hita.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife

/**
 * 本项目所有RecycleView的ViewHolder的基类
 */
open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //让这个ViewHolder也支持ButterKnifeView注入
    init {
        ButterKnife.bind(this, itemView)
    }
}