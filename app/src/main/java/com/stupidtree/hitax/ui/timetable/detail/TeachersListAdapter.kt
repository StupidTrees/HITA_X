package com.stupidtree.hitax.ui.timetable.detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.databinding.DynamicTeacherBinding
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.BaseViewHolder

@SuppressLint("ParcelCreator")
class TeachersListAdapter constructor(mContext: Context, mBeans: MutableList<TeacherInfo>) :
    BaseListAdapter<TeacherInfo, TeachersListAdapter.THolder>(mContext, mBeans) {


    inner class THolder(itemView: DynamicTeacherBinding) :
        BaseViewHolder<DynamicTeacherBinding>(itemView)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return DynamicTeacherBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): THolder {
        return THolder(viewBinding as DynamicTeacherBinding)
    }

    override fun bindHolder(holder: THolder, data: TeacherInfo?, position: Int) {
        holder.binding.teacherSubject.text = data?.subjectName
        holder.binding.teacherName.text = data?.name
        holder.binding.teacherCard.setOnClickListener { v ->
            mOnItemClickListener?.onItemClick(data, v, position)
        }
    }
}
