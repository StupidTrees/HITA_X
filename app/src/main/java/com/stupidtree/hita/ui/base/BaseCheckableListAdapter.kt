package com.stupidtree.hita.ui.base

import android.content.Context
import android.view.View
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseCheckableListAdapter<T, C : RecyclerView.ViewHolder> internal constructor(mContext: Context, mBeans: MutableList<T>) : BaseListAdapter<T, C>(mContext, mBeans) {
    var isEditMode = false
    private var onItemSelectedListener: OnItemSelectedListener? = null
    var checkedItem: HashSet<T>? = null
        private set

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    fun selectAll(): Boolean {
        var someThingAdded = false
        for (ei in mBeans) {
            if (checkedItem!!.add(ei)) someThingAdded = true
        }
        if (!someThingAdded) return false
        notifyDataSetChanged()
        return true
    }

    fun selectAllSmooth(): Boolean {
        var someThingAdded = false
        for (ei in mBeans) {
            if (checkedItem!!.add(ei)) someThingAdded = true
        }
        if (!someThingAdded) return false
        notifyItemChangedSmooth(ArrayList(mBeans))
        //  notifyDataSetChanged();
        return true
    }

    fun activateEditSmooth(initPos: Int) {
        isEditMode = true
        checkedItem = HashSet()
        checkedItem?.add(mBeans[initPos])
        notifyItemChangedSmooth(ArrayList(mBeans))
        // notifyDataSetChanged();
    }

    fun activateEdit(initPos: Int) {
        isEditMode = true
        checkedItem = HashSet()
        checkedItem?.add(mBeans[initPos])
        notifyDataSetChanged()
    }

    fun closeEditSmooth() {
        isEditMode = false
        notifyItemChangedSmooth(ArrayList(mBeans))
    }

    fun closeEdit() {
        isEditMode = false
        notifyDataSetChanged()
    }


    override fun bindHolder(holder: C, data: T?, position: Int) {
        if (holder !is CheckableViewHolder) return
        if (mOnItemLongClickListener != null) holder.setInternalOnLongClickListener { v ->
            if (isEditMode) false else
                mOnItemLongClickListener?.onItemLongClick(mBeans[position], v, position) == true
        }
        if (mOnItemClickListener != null) holder.setInternalOnClickListener { view ->
            if (isEditMode) {
                holder.toggleCheck()
            } else mOnItemClickListener?.onItemClick(mBeans[position], view, position)
        }
        if (isEditMode) {
            holder.showCheckBox()
        } else {
            holder.hideCheckBox()
        }
        if (isEditMode) {
            holder.setInternalOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) data?.let { checkedItem!!.add(it) } else checkedItem!!.remove(data)
                if (onItemSelectedListener != null) onItemSelectedListener!!.onItemSelected(buttonView, isChecked, position, checkedItem!!.size)
            }
            holder.setChecked(checkedItem!!.contains(data))
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(v: View?, checked: Boolean, position: Int, selectedNum: Int)
    }

    interface CheckableViewHolder {
        fun showCheckBox()
        fun hideCheckBox()
        fun toggleCheck()
        fun setChecked(boolean: Boolean)
        fun setInternalOnLongClickListener(listener: View.OnLongClickListener)
        fun setInternalOnClickListener(listener: View.OnClickListener)
        fun setInternalOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener)
    }
}