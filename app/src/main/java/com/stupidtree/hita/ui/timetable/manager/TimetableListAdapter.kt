package com.stupidtree.hita.ui.timetable.manager

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.databinding.DynamicCurriculumAddBinding
import com.stupidtree.hita.databinding.DynamicCurriculumItemBinding
import com.stupidtree.hita.ui.base.BaseCheckableListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.utils.TimeUtils


@SuppressLint("ParcelCreator")
class TimetableListAdapter(context: Context, mBeans: MutableList<Timetable>) :
        BaseCheckableListAdapter<Timetable, RecyclerView.ViewHolder>(context, mBeans) {


    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return if (viewType == TYPE_ITEM) DynamicCurriculumItemBinding.inflate(mInflater, parent, false)
        else DynamicCurriculumAddBinding.inflate(mInflater, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mBeans.size) TYPE_ADD
        else TYPE_ITEM
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            CHolder(viewBinding as DynamicCurriculumItemBinding)
        } else {
            AHolder(viewBinding as DynamicCurriculumAddBinding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindHolder(holder: RecyclerView.ViewHolder, data: Timetable?, position: Int) {
        super.bindHolder(holder, data, position)
        if (holder is CHolder) {
            holder.binding.title.text = data?.name
            holder.binding.subtitle.text = TimeUtils.printDate(data?.startTime?.time)
            data?.startTime?.time?.let {
                holder.binding.icon.setImageResource(
                        when (TimeUtils.getSeason(it)) {
                            TimeUtils.SEASON.SPRING -> R.drawable.ic_spring
                            TimeUtils.SEASON.SUMMER -> R.drawable.ic_summer
                            TimeUtils.SEASON.AUTUMN -> R.drawable.ic_autumn
                            else -> R.drawable.ic_winter
                        }
                )
            }
        } else if (holder is AHolder) {
            holder.binding.card.setOnClickListener {
                mOnItemClickListener?.onItemClick(null, it, position)
            }
        }


    }


    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    inner class CHolder(itemView: DynamicCurriculumItemBinding) :
            BaseViewHolder<DynamicCurriculumItemBinding>(itemView), CheckableViewHolder {
        override fun showCheckBox() {
            binding.check.visibility = View.VISIBLE
        }

        override fun hideCheckBox() {
            binding.check.visibility = View.GONE
        }

        override fun toggleCheck() {
            binding.check.toggle()
        }

        override fun setChecked(boolean: Boolean) {
            binding.check.isChecked = boolean
        }

        override fun setInternalOnLongClickListener(listener: View.OnLongClickListener) {
            binding.card.setOnLongClickListener(listener)
        }

        override fun setInternalOnClickListener(listener: View.OnClickListener) {
            binding.card.setOnClickListener(listener)
        }

        override fun setInternalOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
            binding.check.setOnCheckedChangeListener(listener)
        }


    }

    inner class AHolder(viewBinding: DynamicCurriculumAddBinding) : BaseViewHolder<DynamicCurriculumAddBinding>(viewBinding)

    companion object {
        const val TYPE_ADD = 301
        const val TYPE_ITEM = 300
    }
}
