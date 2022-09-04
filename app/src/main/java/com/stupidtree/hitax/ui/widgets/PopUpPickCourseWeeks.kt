package com.stupidtree.hitax.ui.widgets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.DialogBottomPickCourseWeeksBinding
import com.stupidtree.hitax.ui.event.add.CourseTime
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class PopUpPickCourseWeeks : TransparentBottomSheetDialog<DialogBottomPickCourseWeeksBinding>() {
    var weeksLiveData: MutableLiveData<List<Boolean>> = MutableLiveData()
    var onTimeSelectedListener: OnWeeksSelectedListener? = null
    var initTimetable: Timetable? = null
    var initWeeks: List<Int>? = null

    init {
        isCancelable = false
    }

    interface OnWeeksSelectedListener {
        fun onSelected(data: List<Int>)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_pick_course_weeks
    }


    override fun initViews(v: View) {
        val listAdapter = PickWeekListAdapter(requireActivity() as BaseActivity<*, *>)
        binding.weekList.adapter = listAdapter
        binding.weekList.layoutManager = GridLayoutManager(context, 5)
        weeksLiveData.observe(this) {
            listAdapter.notifyData(it)
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.done.setOnClickListener {
            val r = CourseTime()
            onTimeSelectedListener?.onSelected(listAdapter.getSelectedWeeks())
            dismiss()
        }
    }


    fun setInitialValue(timetable: Timetable?, weeks:List<Int>?): PopUpPickCourseWeeks {
        initTimetable = timetable
        initWeeks = weeks
        return this
    }

    fun setSelectListener(ls: OnWeeksSelectedListener): PopUpPickCourseWeeks {
        onTimeSelectedListener = ls
        return this
    }


    override fun onStart() {
        super.onStart()
        var weeksValue = mutableListOf<Boolean>()
        initTimetable?.let {
            val weeks = mutableListOf<Boolean>()
            for (i in 0 until it.getWeekNumber(it.endTime.time)) weeks.add(false)
            weeksValue = weeks
        }
        initWeeks?.let { initCourseTime ->
            for (i in initCourseTime) {
                if (i > weeksValue.size) for (x in weeksValue.size until i) weeksValue.add(false)
                weeksValue[i - 1] = true
            }
        }
        weeksLiveData.value = weeksValue
    }


    internal class PickWeekListAdapter(val mContext: BaseActivity<*, *>) :
        RecyclerView.Adapter<PickWeekListAdapter.mViewHolder?>() {

        val weeks: MutableList<Boolean> = mutableListOf()

        fun notifyData(data: List<Boolean>) {
            weeks.clear()
            weeks.addAll(data)
            notifyDataSetChanged()
        }

        fun getSelectedWeeks(): List<Int> {
            val res = mutableListOf<Int>()
            for (i in 0 until weeks.size) {
                if (weeks[i]) res.add(i + 1)
            }
            return res
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
            val v: View =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.dynamic_pick_week_item, parent, false)
            return mViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: mViewHolder, position: Int) {
            holder.text.text = (position + 1).toString() + ""
            if (position == weeks.size) {
                holder.card.setCardBackgroundColor(
                    mContext.getTextColorSecondary()
                )
                holder.text.setTextColor(
                    mContext.getTextColorSecondary()
                )
                holder.text.text = "＋"
                holder.card.setOnClickListener {
                    for (i in 0..4) {
                        weeks.add(false)
                        notifyItemChanged(weeks.size - 2)
                        notifyItemInserted(weeks.size - 1)
                    }
                }
            } else {
                if (!weeks[position]) {
                    holder.card.setCardBackgroundColor(
                        mContext.getTextColorSecondary()
                    )
                    holder.text.setTextColor(
                        mContext.getTextColorSecondary()
                    )
                } else {
                    holder.card.setCardBackgroundColor(mContext.getColorPrimary())
                    holder.text.setTextColor(mContext.getColorPrimary())
                }
                holder.card.setOnClickListener {
                    weeks[position] = !weeks[position]
                    notifyItemChanged(position)
                }
            }
        }

        override fun getItemCount(): Int {
            return weeks.size + 1
        }

        internal inner class mViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var card: CardView
            var text: TextView

            init {
                card = itemView.findViewById(R.id.card)
                text = itemView.findViewById(R.id.text)
            }
        }
    }

    override fun initViewBinding(v: View): DialogBottomPickCourseWeeksBinding {
        return DialogBottomPickCourseWeeksBinding.bind(v)
    }

}