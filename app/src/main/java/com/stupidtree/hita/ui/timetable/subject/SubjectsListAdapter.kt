package com.stupidtree.hita.ui.timetable.subject

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.databinding.DynamicSubjectListTitleBinding
import com.stupidtree.hita.databinding.DynamicSubjectsFootBinding
import com.stupidtree.hita.databinding.DynamicSubjectsItemBinding
import com.stupidtree.hita.ui.base.BaseCheckableListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.timetable.subject.SubjectsListAdapter.SubjectViewHolder
import com.stupidtree.hita.utils.ColorBox

@SuppressLint("ParcelCreator")
class SubjectsListAdapter(context: Context, subjects: MutableList<Pair<TermSubject, Float>>) :
        BaseCheckableListAdapter<Pair<TermSubject, Float>, SubjectViewHolder>(context, subjects) {


    override fun getItemViewType(position: Int): Int {
        if (position == mBeans.size) return FOOT
        return if (mBeans[position].first.type == TermSubject.TYPE.TAG) TITLE else NORMAL
    }

    override fun getItemCount(): Int {
        return mBeans.size
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return when (viewType) {
            NORMAL -> DynamicSubjectsItemBinding.inflate(mInflater, parent, false)
            TITLE -> DynamicSubjectListTitleBinding.inflate(mInflater, parent, false)
            else -> DynamicSubjectsFootBinding.inflate(mInflater, parent, false)
        }
    }



    class SubjectViewHolder(binding: ViewBinding) : BaseViewHolder<ViewBinding>(viewBinding = binding), CheckableViewHolder {

        override fun showCheckBox() {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).check.visibility = View.VISIBLE
            }
        }

        override fun hideCheckBox() {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).check.visibility = View.GONE
            }
        }

        override fun toggleCheck() {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).check.toggle()
            }
        }

        override fun setChecked(boolean: Boolean) {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).check.isChecked = boolean
            }
        }

        override fun setInternalOnLongClickListener(listener: View.OnLongClickListener) {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).item.setOnLongClickListener(listener)
            }
        }

        override fun setInternalOnClickListener(listener: View.OnClickListener) {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).item.setOnClickListener(listener)
            }
        }

        override fun setInternalOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
            if (binding is DynamicSubjectsItemBinding) {
                (binding as DynamicSubjectsItemBinding).check.setOnCheckedChangeListener(listener)
            }
        }

    }

    companion object {
        private const val NORMAL = 967
        private const val TITLE = 971
        private const val FOOT = 608
    }


    override fun bindHolder(holder: SubjectViewHolder, data: Pair<TermSubject, Float>?, position: Int) {
        super.bindHolder(holder, data, position)
        if (holder.binding is DynamicSubjectListTitleBinding) {
            val titleBinding = holder.binding as DynamicSubjectListTitleBinding
            titleBinding.name.text = mBeans[position].first.name
        } else if (holder.binding is DynamicSubjectsItemBinding) {
            val binding = holder.binding as DynamicSubjectsItemBinding
            var color = -1
            //val colorfulMode = timetableSP.getBoolean("subjects_color_enable", false)
//            if (colorfulMode) {
//                color = ColorBox.getSubjectColor(timetableSP, data?.first?.name)
//            }
            binding.name.text = data?.first?.name
            if (color != -1) binding.icon.setColorFilter(color) else binding.icon.clearColorFilter()
//            val finalColor = color
//            binding.icon.setOnClickListener(View.OnClickListener {
//                if (!colorfulMode) return@OnClickListener
//                ColorPickerDialog(mContext)
//                        .initColor(finalColor).show(object : OnColorSelectedListener() {
//                            fun OnSelected(color: Int) {
//                                ColorBox.changeSubjectColor(timetableSP, s.getName(), color)
//                                binding.icon.setColorFilter(color)
//                            }
//                        })
//            })
            val t = if (TextUtils.isEmpty(data?.first?.school)) mContext.getString(R.string.unknown_department) else data?.first?.school
            binding.label.text = t
            if (data != null) {
                binding.progress.progress = (data.second * 100).toInt()
            }
            if (isEditMode) {
                binding.icon.visibility = View.GONE
            } else {
                binding.icon.visibility = View.VISIBLE
            }
        }
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(viewBinding)
    }
}