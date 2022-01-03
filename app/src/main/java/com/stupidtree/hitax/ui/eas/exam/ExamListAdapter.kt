package com.stupidtree.hitax.ui.eas.exam

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.data.model.eas.ExamItem
import com.stupidtree.hitax.databinding.ActivityEasExamBinding
import com.stupidtree.hitax.databinding.ActivityEasExamListItemBinding
import com.stupidtree.hitax.ui.eas.classroom.EmptyClassroomViewModel
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder


@SuppressLint("ParcelCreator")
class ExamListAdapter (
        mContext: Context,
        mBeans: MutableList<ExamItem>,
    ):
    BaseListAdapter<ExamItem, ExamListAdapter.SHolder>(
        mContext, mBeans
    ) {
    class SHolder(viewBinding: ActivityEasExamListItemBinding) :
        BaseViewHolder<ActivityEasExamListItemBinding>(viewBinding)
    override fun bindHolder(
        holder: ExamListAdapter.SHolder,
        data: ExamItem?,
        position: Int
    ) {
        if (position == mBeans.size - 1) {
            holder.binding.divider2.visibility = View.GONE
        } else {
            holder.binding.divider2.visibility = View.VISIBLE
        }
        holder.binding.title.text = data?.courseName
        holder.binding.time.text = data?.examDate
        holder.binding.item.setOnClickListener{view -> mOnItemClickListener?.onItemClick(data, view, position)}
    }

    override fun createViewHolder(
        viewBinding: ViewBinding,
        viewType: Int
    ): ExamListAdapter.SHolder {
        return ExamListAdapter.SHolder(viewBinding = viewBinding as ActivityEasExamListItemBinding)
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityEasExamListItemBinding.inflate(mInflater,parent,false)
    }
}