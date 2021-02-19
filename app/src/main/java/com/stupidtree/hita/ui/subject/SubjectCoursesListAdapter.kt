package com.stupidtree.hita.ui.subject

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.databinding.DynamicSubjectCourseitemBinding
import com.stupidtree.hita.databinding.DynamicSubjectCourseitemPassedBinding
import com.stupidtree.hita.databinding.DynamicSubjectCourseitemTagBinding
import com.stupidtree.hita.ui.base.BaseCheckableListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.utils.TimeUtils
import java.util.*

@SuppressLint("ParcelCreator")
class SubjectCoursesListAdapter(context: Context, list: MutableList<EventItem>) :
    BaseCheckableListAdapter<EventItem, BaseViewHolder<*>>(
        context,
        list
    ) {

    override fun getItemViewType(position: Int): Int {
        return when {
            mBeans[position].type === EventItem.TYPE.TAG -> TAG
            TimeUtils.passed(mBeans[position].to) -> PASSED
            else -> TODO
        }
    }

    override fun bindHolder(holder: BaseViewHolder<*>, data: EventItem?, position: Int) {
        super.bindHolder(holder, data, position)
        val c = Calendar.getInstance()
        data?.from?.time?.let {
            c.timeInMillis = it
        }
        if (holder is TagViewHolder) {
            if (data?.name == "more") {
                holder.binding.icon.rotation = 0f
            } else {
                holder.binding.icon.rotation = 180f
            }
            holder.binding.item.setOnClickListener { data?.let { it1 ->
                mOnItemClickListener?.onItemClick(
                    it1,it,position)
            } }
        } else if (holder is NormalViewHolder) {
            holder.binding.subjectCourselistMonth.text =
                TimeUtils.getDateString(mContext, c, true, TimeUtils.TTY_REPLACE)
            if (isEditMode) holder.binding.icon.visibility = GONE
            else holder.binding.icon.visibility = VISIBLE
            holder.binding.item.setOnClickListener { data?.let { it1 ->
                mOnItemClickListener?.onItemClick(
                    it1,it,position)
            } }
        } else if (holder is PassedViewHolder) {
            holder.binding.subjectCourselistMonth.text =
                TimeUtils.getDateString(mContext, c, true, TimeUtils.TTY_REPLACE)
            if (isEditMode) holder.binding.icon.visibility = GONE
            else holder.binding.icon.visibility = VISIBLE
            holder.binding.item.setOnClickListener { data?.let { it1 ->
                mOnItemClickListener?.onItemClick(
                    it1,it,position)
            } }
        }

    }


    class NormalViewHolder(viewBinding: DynamicSubjectCourseitemBinding) :
        BaseViewHolder<DynamicSubjectCourseitemBinding>(
            viewBinding
        ), CheckableViewHolder {
        override fun showCheckBox() {
            binding.check.visibility = VISIBLE
        }

        override fun hideCheckBox() {
            binding.check.visibility = GONE
        }

        override fun toggleCheck() {
            binding.check.toggle()
        }

        override fun setChecked(boolean: Boolean) {
            binding.check.isChecked = boolean
        }

        override fun setInternalOnLongClickListener(listener: View.OnLongClickListener) {
            binding.item.setOnLongClickListener(listener)
        }

        override fun setInternalOnClickListener(listener: View.OnClickListener) {
            binding.item.setOnClickListener { listener }
        }

        override fun setInternalOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
            binding.check.setOnCheckedChangeListener(listener)
        }
    }

    class PassedViewHolder(viewBinding: DynamicSubjectCourseitemPassedBinding) :
        BaseViewHolder<DynamicSubjectCourseitemPassedBinding>(
            viewBinding
        ),CheckableViewHolder {
        override fun showCheckBox() {
            binding.check.visibility = VISIBLE
        }

        override fun hideCheckBox() {
            binding.check.visibility = GONE
        }

        override fun toggleCheck() {
            binding.check.toggle()
        }

        override fun setChecked(boolean: Boolean) {
            binding.check.isChecked = boolean
        }

        override fun setInternalOnLongClickListener(listener: View.OnLongClickListener) {
            binding.item.setOnLongClickListener(listener)
        }

        override fun setInternalOnClickListener(listener: View.OnClickListener) {
            binding.item.setOnClickListener { listener }
        }

        override fun setInternalOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
            binding.check.setOnCheckedChangeListener(listener)
        }
    }

    class TagViewHolder(viewBinding: DynamicSubjectCourseitemTagBinding) :
        BaseViewHolder<DynamicSubjectCourseitemTagBinding>(
            viewBinding
        )

    companion object {
        private const val PASSED = 672
        private const val TODO = 222
        private const val TAG = 914
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return when (viewType) {
            TAG -> DynamicSubjectCourseitemTagBinding.inflate(mInflater, parent, false)
            TODO -> DynamicSubjectCourseitemBinding.inflate(mInflater, parent, false)
            else -> DynamicSubjectCourseitemPassedBinding.inflate(mInflater, parent, false)
        }
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): BaseViewHolder<*> {
        return when (viewBinding) {
            is DynamicSubjectCourseitemBinding -> NormalViewHolder(viewBinding)
            is DynamicSubjectCourseitemPassedBinding -> PassedViewHolder(viewBinding)
            else -> TagViewHolder(viewBinding as DynamicSubjectCourseitemTagBinding)
        }
    }

}