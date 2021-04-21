package com.stupidtree.style.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

/**
 * 一个基本的可选择列表Adapter
 *
 * @param <TextRecord> T泛型指定每个列表项的数据Model的类型
 * @param <H> H泛型指定ViewHolder的类型
</H></TextRecord> */
abstract class BasicSelectableListAdapter<T, H : RecyclerView.ViewHolder>(mContext: Context, mBeans: MutableList<T>) : BaseListAdapter<T, H>(mContext, mBeans) {
    var selectedIndex = 0
        protected set
    var selectedData: T? = null
        protected set

    /**
     * 设置选中项目
     *
     * @param data 选中数据
     */
    fun setSelected(data: T) {
        if (mBeans.contains(data)) {
            selectedIndex = mBeans.indexOf(data)
            selectedData = data
        }
    }

    protected fun selectItem(index: Int, data: T) {
        val old = selectedIndex
        selectedData = data
        notifyItemChanged(old) //原来选中的位置刷新
        selectedIndex = index
        notifyItemChanged(selectedIndex) //新选中的位置刷新
    }
}