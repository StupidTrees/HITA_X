package com.stupidtree.hitax.ui.eas.classroom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.ActivityEasClassroomItemBinding
import com.stupidtree.hitax.ui.base.BaseActivity
import com.stupidtree.hitax.ui.base.BaseListAdapter
import com.stupidtree.hitax.ui.base.BaseViewHolder
import com.stupidtree.hitax.utils.TimeTools

@SuppressLint("ParcelCreator")
class EmptyClassroomListAdapter(
    mContext: Context,
    mBeans: MutableList<ClassroomItem>,
    val viewModel: EmptyClassroomViewModel
) :
    BaseListAdapter<ClassroomItem, EmptyClassroomListAdapter.KHolder>(
        mContext, mBeans
    ) {


    private fun getState(data: ClassroomItem): String {
        viewModel.timetableStructureLiveData.value?.data?.let {
            val current = TimeTools.getCurrentScheduleNumber(it)
            val currentDow: Int = TimeTools.getDow(System.currentTimeMillis())
            for (je in data.scheduleList) {
                val num: Int = je.optInt("XJ") * 10
                val dow: Int = je.optInt("XQJ")
                if (num == current && currentDow == dow) return "被占"
                else if (num == current + 5 && currentDow == dow)
                    return "将占"
            }
            return "空闲"
        }
        return "未知"
    }

    class KHolder(itemView: ActivityEasClassroomItemBinding) :
        BaseViewHolder<ActivityEasClassroomItemBinding>(
            itemView
        )

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityEasClassroomItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): KHolder {
        return KHolder(viewBinding as ActivityEasClassroomItemBinding)
    }

    override fun bindHolder(holder: KHolder, data: ClassroomItem?, position: Int) {
        holder.binding.name.text = data?.name
        holder.binding.capacity.text = data?.capacity.toString()
        holder.binding.state.text = data?.let { getState(it) }
        if (holder.binding.state.text != "空闲") {
            holder.binding.state.setBackgroundResource(R.drawable.element_rounded_button_bg_grey)
            holder.binding.state.setTextColor(Color.GRAY)
        } else if (mContext is BaseActivity<*, *>) {
            holder.binding.state.setBackgroundResource(R.drawable.element_rounded_button_bg_primary_light)
            holder.binding.state.setTextColor((mContext as BaseActivity<*, *>).getColorPrimary())
        }

        holder.binding.card.setOnClickListener { view ->
            mOnItemClickListener?.onItemClick(
                data,
                view,
                position
            )
        }
    }


}