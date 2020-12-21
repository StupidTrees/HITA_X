package com.stupidtree.hita.ui.base

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * 本项目一般的RecyclerView的Adapter的基类
 *
 * @param <TextRecord> T泛型指定每个列表项的数据Model的类型
 * @param <H> H泛型指定ViewHolder的类型
</H></TextRecord> */
abstract class BaseListAdapter<T, H : RecyclerView.ViewHolder>(protected var mContext: Context,
                                                                /**
                                                                 * 标准三件：数据源列表、Context、Inflater对象
                                                                 */
                                                                protected var mBeans: MutableList<T>) : RecyclerView.Adapter<H>() {
    var mInflater: LayoutInflater = LayoutInflater.from(mContext)

    val beans: List<T>
        get() = mBeans
    /**
     * 提供一个单击的Listener和一个长按的Listener
     */
    @JvmField
    protected var mOnItemClickListener: OnItemClickListener<T>? = null
    @JvmField
    protected var mOnItemLongClickListener: OnItemLongClickListener<T>? = null

    /**
     * 所有继承此类的Adapter都需要实现以下三个函数
     */
    //获取每个列表项的布局id
    protected abstract fun getLayoutId(viewType: Int): Int

    //初始化每个holder
    abstract fun createViewHolder(v: View, viewType: Int): H

    //用于绑定每个holder
    protected abstract fun bindHolder(holder: H, data: T?, position: Int)
    fun setOnItemClickListener(mOnItemClickLitener: OnItemClickListener<T>?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    fun setOnItemLongClickListener(mOnItemLongClickLitener: OnItemLongClickListener<T>?) {
        mOnItemLongClickListener = mOnItemLongClickLitener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        val v = mInflater.inflate(getLayoutId(viewType), parent, false)
        return createViewHolder(v, viewType)
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        try {
            val data = mBeans[position]
            bindHolder(holder, data, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 偏移量，即不算在mBeans内的头部元素的数量
     */
    val indexBias: Int
        get() = 0

    fun willNotifyNormalChange(): Boolean { //当位置不发生变化时，是否刷新该item
        return true
    }

    /**
     * 优雅地进行RecyclerView的刷新，自动识别新增、移动、删除
     *
     * @param newL 新的数据List
     */
    open fun notifyItemChangedSmooth(newL: List<T>) {
        val toInsert: MutableList<Int> = ArrayList() //记录变化的操作表，正表示加入，负表示删除
        val toRemove = Stack<Int>()
        val remains: MutableList<T> = ArrayList() //留下来的元素
        //找到要移除的
        for (i in mBeans.indices) {
            if (!newL.contains(mBeans[i])) toRemove.push(i)
        }
        //先处理删除,从后往前删
        while (toRemove.size > 0) {
            val index = toRemove.pop()
            mBeans.removeAt(index)
            notifyItemRemoved(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        //找到要插入的
        for (i in newL.indices) {
            val ei = newL[i]
            if (!mBeans.contains(ei)) toInsert.add(i) //新加入的
            else remains.add(ei)
        }
        for (i in toInsert.indices) {
            val index = toInsert[i]
            if (index > mBeans.size - 1) mBeans.add(newL[index]) else mBeans.add(index, newL[index])
            notifyItemInserted(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        for (ei in remains) { //保留的
            val oldIndex = mBeans.indexOf(ei)
            val newIndex = newL.indexOf(ei)
            if (oldIndex == newIndex && willNotifyNormalChange()) {
                mBeans[oldIndex] = newL[newIndex]
                notifyItemChanged(newIndex + indexBias)
            } else {
                mBeans.removeAt(oldIndex)
                val to = newL[newIndex]
                if (newIndex > mBeans.size - 1) mBeans.add(to) else mBeans.add(newIndex, to)
                notifyItemMoved(oldIndex + indexBias, newIndex + indexBias)
                notifyItemRangeChanged(Math.min(oldIndex, newIndex) + indexBias, mBeans.size + indexBias)
            }
        }
    }

    /**
     * 优雅地进行RecyclerView的刷新，自动识别新增、移动、删除
     *
     * @param newL             新的数据List
     * @param notifyNormalItem 对于那些位置不变的项目，是否原地刷新
     */
    public fun notifyItemChangedSmooth(newL: List<T>, notifyNormalItem: Boolean) {
        val toInsert: MutableList<Int> = ArrayList() //记录变化的操作表，正表示加入，负表示删除
        val toRemove = Stack<Int>()
        val remains: MutableList<T> = ArrayList() //留下来的元素
        //找到要移除的
        for (i in mBeans.indices) {
            if (!newL.contains(mBeans[i])) toRemove.push(i)
        }
        //先处理删除,从后往前删
        while (toRemove.size > 0) {
            val index = toRemove.pop()
            mBeans.removeAt(index)
            notifyItemRemoved(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        //找到要插入的
        for (i in newL.indices) {
            val ei = newL[i]
            if (!mBeans.contains(ei)) toInsert.add(i) //新加入的
            else remains.add(ei)
        }
        for (i in toInsert.indices) {
            val index = toInsert[i]
            if (index > mBeans.size - 1) mBeans.add(newL[index]) else mBeans.add(index, newL[index])
            notifyItemInserted(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        for (ei in remains) { //保留的
            val oldIndex = mBeans.indexOf(ei)
            val newIndex = newL.indexOf(ei)
            if (oldIndex == newIndex && notifyNormalItem) {
                mBeans[oldIndex] = newL[newIndex]
                notifyItemChanged(newIndex + indexBias)
            } else if (notifyNormalItem) {
                mBeans.removeAt(oldIndex)
                val to = newL[newIndex]
                if (newIndex > mBeans.size - 1) mBeans.add(to) else mBeans.add(newIndex, to)
                notifyItemMoved(oldIndex + indexBias, newIndex + indexBias)
                notifyItemRangeChanged(Math.min(oldIndex, newIndex) + indexBias, mBeans.size + indexBias)
            }
        }
    }

    /**
     * 优雅地进行RecyclerView的刷新，自动识别新增、移动、删除
     *
     * @param newL             新的数据List
     * @param notifyNormalItem 对于那些位置不变的项目，是否原地刷新
     * @param comparator       用于排序，比较两个Item的Comparator
     */
    fun notifyItemChangedSmooth(newL: List<T>, notifyNormalItem: Boolean, comparator: Comparator<T>) {
        val toInsert: MutableList<Int> = ArrayList() //记录变化的操作表，正表示加入，负表示删除
        val toRemove = Stack<Int>()
        val remains: MutableList<T> = ArrayList() //留下来的元素
        //找到要移除的
        for (i in mBeans.indices) {
            // boolean contains = false;
            if (!contains(newL, mBeans[i], comparator)) toRemove.push(i)
            // if(!newL.contains(mBeans.get(i))) toRemove.push(i);
        }
        //先处理删除,从后往前删
        while (toRemove.size > 0) {
            val index = toRemove.pop()
            mBeans.removeAt(index)
            notifyItemRemoved(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        //找到要插入的
        for (i in newL.indices) {
            val ei = newL[i]
            if (!contains(mBeans, ei, comparator)) toInsert.add(i) else remains.add(ei)
        }
        for (i in toInsert.indices) {
            val index = toInsert[i]
            if (index > mBeans.size - 1) mBeans.add(newL[index]) else mBeans.add(index, newL[index])
            notifyItemInserted(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        for (ei in remains) { //保留的
            val oldIndex = indexOf(mBeans, ei, comparator)
            val newIndex = indexOf(newL, ei, comparator)
            if (oldIndex == newIndex && notifyNormalItem) {
                mBeans[oldIndex] = newL[newIndex]
                notifyItemChanged(newIndex + indexBias)
            } else if (notifyNormalItem) {
                mBeans.removeAt(oldIndex)
                val to = newL[newIndex]
                if (newIndex > mBeans.size - 1) mBeans.add(to) else mBeans.add(newIndex, to)
                notifyItemMoved(oldIndex + indexBias, newIndex + indexBias)
                notifyItemRangeChanged(Math.min(oldIndex, newIndex) + indexBias, mBeans.size + indexBias)
            }
        }
    }

    /**
     * 优雅地进行RecyclerView的刷新，自动识别新增、移动、删除
     *
     * @param newL  新的数据List
     * @param judge 判定某一个Item，当其位置不变时，是否原地刷新
     */
    fun notifyItemChangedSmooth(newL: List<T>, judge: RefreshJudge<T>) {
        val toInsert: MutableList<Int> = ArrayList() //记录变化的操作表，正表示加入，负表示删除
        val toRemove = Stack<Int>()
        val remains: MutableList<T> = ArrayList() //留下来的元素
        //找到要移除的
        for (i in mBeans.indices) {
            if (!newL.contains(mBeans[i])) toRemove.push(i)
        }
        //先处理删除,从后往前删
        while (toRemove.size > 0) {
            val index = toRemove.pop()
            mBeans.removeAt(index)
            notifyItemRemoved(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        //找到要插入的
        for (i in newL.indices) {
            val ei = newL[i]
            if (!mBeans.contains(ei)) toInsert.add(i) //新加入的
            else remains.add(ei)
        }
        for (i in toInsert.indices) {
            val index = toInsert[i]
            if (index > mBeans.size - 1) mBeans.add(newL[index]) else mBeans.add(index, newL[index])
            notifyItemInserted(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        for (ei in remains) { //保留的
            val oldIndex = mBeans.indexOf(ei)
            val newIndex = newL.indexOf(ei)
            val willRefresh = judge.judge(mBeans[oldIndex], newL[newIndex])
            if (oldIndex == newIndex && willRefresh) {
                mBeans[oldIndex] = newL[newIndex]
                notifyItemChanged(newIndex + indexBias)
            } else if (oldIndex != newIndex) {
                mBeans.removeAt(oldIndex)
                val to = newL[newIndex]
                if (newIndex > mBeans.size - 1) mBeans.add(to) else mBeans.add(newIndex, to)
                notifyItemMoved(oldIndex + indexBias, newIndex + indexBias)
                notifyItemRangeChanged(Math.min(oldIndex, newIndex) + indexBias, mBeans.size + indexBias)
            }
        }
    }

    /**
     * 优雅地进行RecyclerView的刷新，自动识别新增、移动、删除
     *
     * @param newL       新的数据List
     * @param judge      判定某一个Item，当其位置不变时，是否原地刷新
     * @param comparator 用于排序，比较两个Item的Comparator
     */
    fun notifyItemChangedSmooth(newL: List<T>, judge: RefreshJudge<T>, comparator: Comparator<T>) {
        val toInsert: MutableList<Int> = ArrayList() //记录变化的操作表，正表示加入，负表示删除
        val toRemove = Stack<Int>()
        val remains: MutableList<T> = ArrayList() //留下来的元素
        //找到要移除的
        for (i in mBeans.indices) {
            // boolean contains = false;
            if (!contains(newL, mBeans[i], comparator)) toRemove.push(i)
            // if(!newL.contains(mBeans.get(i))) toRemove.push(i);
        }
        //先处理删除,从后往前删
        while (toRemove.size > 0) {
            val index = toRemove.pop()
            mBeans.removeAt(index)
            notifyItemRemoved(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        //找到要插入的
        for (i in newL.indices) {
            val ei = newL[i]
            if (!contains(mBeans, ei, comparator)) toInsert.add(i) else remains.add(ei)
        }
        for (i in toInsert.indices) {
            val index = toInsert[i]
            if (index > mBeans.size - 1) mBeans.add(newL[index]) else mBeans.add(index, newL[index])
            notifyItemInserted(index + indexBias)
            notifyItemRangeChanged(index + indexBias, mBeans.size + indexBias)
        }
        for (ei in remains) { //保留的
            val oldIndex = indexOf(mBeans, ei, comparator)
            val newIndex = indexOf(newL, ei, comparator)
            val willRefresh = judge.judge(mBeans[oldIndex], newL[newIndex])
            if (oldIndex == newIndex && willRefresh) {
                mBeans[oldIndex] = newL[newIndex]
                notifyItemChanged(newIndex + indexBias)
            } else if (oldIndex != newIndex) {
                mBeans.removeAt(oldIndex)
                val to = newL[newIndex]
                if (newIndex > mBeans.size - 1) mBeans.add(to) else mBeans.add(newIndex, to)
                notifyItemMoved(oldIndex + indexBias, newIndex + indexBias)
                notifyItemRangeChanged(Math.min(oldIndex, newIndex) + indexBias, mBeans.size + indexBias)
            }
        }
    }

    open fun notifyItemsAppended(newL: List<T>) {
        mBeans.addAll(newL)
        notifyItemRangeInserted(mBeans.size - newL.size, newL.size)
    }

    open fun notifyItemsPushHead(newL: List<T>) {
        mBeans.addAll(0, newL)
        notifyItemRangeInserted(0, newL.size)
    }

    fun notifyItemPushHead(item: T) {
        mBeans.add(0, item)
        notifyItemRangeInserted(0, 1)
    }

    fun notifyItemRemoveFromHead() {
        mBeans.removeAt(0)
        notifyItemRemoved(0)
    }

    fun notifyItemAppended(item: T) {
        mBeans.add(item)
        notifyItemInserted(mBeans.size - 1)
    }

    override fun getItemCount(): Int {
        return mBeans.size + indexBias
    }

    private fun contains(collection: List<T>, element: T, comparator: Comparator<T>): Boolean {
        return indexOf(collection, element, comparator) != -1
    }

    private fun indexOf(collection: List<T>, element: T, comparator: Comparator<T>): Int {
        for (t in collection) {
            if (comparator.compare(element, t) == 0) return collection.indexOf(t)
        }
        return -1
    }

    interface OnItemClickListener<T> {
        fun onItemClick(data: T, card: View?, position: Int)
    }

    interface OnItemLongClickListener<T> {
        fun onItemLongClick(data: T, view: View?, position: Int): Boolean
    }

    interface RefreshJudge<T> {
        fun judge(oldData: T, newData: T): Boolean
    }

}