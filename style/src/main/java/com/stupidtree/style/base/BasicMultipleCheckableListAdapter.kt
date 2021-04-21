package com.stupidtree.style.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * 一个基本的可多项选择列表Adapter
 *
 * @param <TextRecord> T泛型指定每个列表项的数据Model的类型
 * @param <H> H泛型指定ViewHolder的类型
</H></TextRecord> */
abstract class BasicMultipleCheckableListAdapter<T, H : RecyclerView.ViewHolder>
(mContext: Context, mBeans: MutableList<T>, private val minCheckNumber: Int) : BaseListAdapter<T, H>(mContext, mBeans) {
    protected var selectedIndex = mutableListOf<Int>()

    fun getCheckedData():List<T>{
        val res = mutableListOf<T>()
        for(i in selectedIndex){
            res.add(mBeans[i])
        }
        return res
    }

    /**
     * 设置选中项目
     *
     * @param data 选中数据
     */
    fun setChecked(data: List<T>) {
        selectedIndex.clear()
        for (d in data) {
            selectedIndex.add(mBeans.indexOf(d))
        }
        notifyDataSetChanged()
    }

    interface TargetMatcher<T,B>{
        fun match(target:T,value:B):Boolean
    }

    /**
     * 设置选中项目
     *
     * @param data 选中数据
     */
    fun<B> setChecked(data: List<B>,f:TargetMatcher<T,B>) {
        selectedIndex.clear()
        for (d in data) {
            for(i in 0 until mBeans.size){
                if(f.match(mBeans[i],d)){
                    selectedIndex.add(i)
                    break
                }
            }
        }
        notifyDataSetChanged()
    }

    protected fun checkItem(index: Int) {
        if(index<0||index>=mBeans.size) return
        if (selectedIndex.contains(index)) {
            if (selectedIndex.size > minCheckNumber) {
                selectedIndex.remove(index)
            }
        } else {
            selectedIndex.add(index)
        }
        notifyItemChanged(index)
    }
}