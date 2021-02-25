package com.stupidtree.hitax.ui.eas.imp

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.databinding.ActivityEasImportListItemBinding
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.BaseViewHolder

@SuppressLint("ParcelCreator")
class TimetableStructureListAdapter(mContext: Context, mBeans: MutableList<TimePeriodInDay>) :
        BaseListAdapter<TimePeriodInDay, TimetableStructureListAdapter.SHolder>(
                mContext, mBeans
        ) {


    class SHolder(viewBinding: ActivityEasImportListItemBinding) :
            BaseViewHolder<ActivityEasImportListItemBinding>(viewBinding)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityEasImportListItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): SHolder {
        return SHolder(viewBinding = viewBinding as ActivityEasImportListItemBinding)
    }

    override fun bindHolder(holder: SHolder, data: TimePeriodInDay?, position: Int) {
        if (position == mBeans.size - 1) {
            holder.binding.divider2.visibility = View.GONE
        } else {
            holder.binding.divider2.visibility = View.VISIBLE
        }
        holder.binding.item.setOnClickListener {
            data?.let { it1 -> mOnItemClickListener?.onItemClick(it1, it, position) }
        }
        holder.binding.title.text =
                mContext.getString(R.string.schedule_list_item_pattern, position + 1)
        data?.let {
            holder.binding.subtitle.text = it.toString()
        }
    }
}