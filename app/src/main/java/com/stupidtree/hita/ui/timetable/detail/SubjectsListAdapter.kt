package com.stupidtree.hita.ui.timetable.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.graphics.*
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.databinding.DynamicSubjectListTitleBinding
import com.stupidtree.hita.databinding.DynamicSubjectsFootBinding
import com.stupidtree.hita.databinding.DynamicSubjectsItemBinding
import com.stupidtree.hita.ui.base.BaseCheckableListAdapter
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.timetable.detail.SubjectsListAdapter.SubjectViewHolder

@SuppressLint("ParcelCreator")
class SubjectsListAdapter(
    context: Context,
    subjects: MutableList<TermSubject>,
    val viewModel: TimetableDetailViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    BaseCheckableListAdapter<TermSubject, SubjectViewHolder>(context, subjects) {


    override fun getItemViewType(position: Int): Int {
        if (position == mBeans.size) return FOOT
        return if (mBeans[position].type == TermSubject.TYPE.TAG) TITLE else NORMAL
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


    class SubjectViewHolder(binding: ViewBinding) :
        BaseViewHolder<ViewBinding>(viewBinding = binding), CheckableViewHolder {

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


    override fun bindHolder(
        holder: SubjectViewHolder,
        data: TermSubject?,
        position: Int
    ) {
        super.bindHolder(holder, data, position)
        if (holder.binding is DynamicSubjectListTitleBinding) {
            val titleBinding = holder.binding as DynamicSubjectListTitleBinding
            titleBinding.name.text = mBeans[position].name
        } else if (holder.binding is DynamicSubjectsItemBinding) {
            val binding = holder.binding as DynamicSubjectsItemBinding
            binding.name.text = data?.name
            if (data?.color != null) {
                binding.icon.setColorFilter(data.color)
            } else {
                binding.icon.clearColorFilter()
            }
            data?.id?.let {
                viewModel.getSubjectProgress(it).observe(lifecycleOwner) {
                    binding.progress.progress = (it.first / (it.second.toFloat()) * 100).toInt()
                }
            }
            data?.let {
                binding.progress.progressTintList = ColorStateList.valueOf(it.color)
                val c = Color.argb(
                    Color.alpha(it.color) / 4,
                    Color.red(it.color),
                    Color.green(it.color),
                    Color.blue(it.color)
                )
                binding.progress.backgroundTintMode = PorterDuff.Mode.SRC_IN
                binding.progress.backgroundTintList = ColorStateList.valueOf(c)
            }

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
            val t =
                if (TextUtils.isEmpty(data?.school)) mContext.getString(R.string.unknown_department) else data?.school
            binding.label.text = t
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