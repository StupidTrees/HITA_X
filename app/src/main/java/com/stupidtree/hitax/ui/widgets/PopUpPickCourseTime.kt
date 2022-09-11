package com.stupidtree.hitax.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.databinding.DialogBottomPickCourseTimeBinding
import com.stupidtree.hitax.databinding.DynamicAddEventDateitemBinding
import com.stupidtree.hitax.ui.event.add.CourseTime
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import com.stupidtree.style.widgets.TransparentBottomSheetDialog
import com.stupidtree.style.widgets.TransparentDialog
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class PopUpPickCourseTime(val timetable: Timetable) :
    TransparentDialog<DialogBottomPickCourseTimeBinding>() {
    var rangeLiveData: MutableLiveData<Pair<Int, TimePeriodInDay>> = MutableLiveData()
    var weeksLiveData: MutableLiveData<List<Boolean>> = MutableLiveData()
    var selectedTimesLiveData: MutableLiveData<List<Long>> = MutableLiveData()
    var onTimeSelectedListener: OnTimeSelectedListener? = null
    var initTimetable: Timetable? = null
    var initCourseTime: CourseTime? = null
    private var listAdapter: PickWeekListAdapter? = null
    private var datesAdapter: SelectedDateAdapter? = null

    interface OnTimeSelectedListener {
        fun onSelected(data: CourseTime)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_pick_course_time
    }

    override fun initViewBinding(v: View): DialogBottomPickCourseTimeBinding {
        return DialogBottomPickCourseTimeBinding.bind(v)
    }


    private fun bindLiveData() {
        weeksLiveData.observe(this) {
            listAdapter?.notifyData(it)
        }
        rangeLiveData.observe(this) {
            binding.pickdow.currentIndex = it.first - 1
            val period = timetable.transformCourseNumber(it.second)
            binding.picktot.currentIndex = period.second - 1
            binding.pickfromt.currentIndex = period.first - 1
        }
        selectedTimesLiveData.observe(this) {
            val dt = mutableListOf<String>()
            for (time in it) {
                val c = Calendar.getInstance()
                c.timeInMillis = time
                context?.let { it1 ->
                    dt.add(TimeTools.getDateString(it1, c, true, TimeTools.TTY_NONE))
                }
            }
            datesAdapter?.notifyItemChangedSmooth(dt, notifyNormalItem = false)

        }
    }

    override fun initViews(v: View) {
        val dows: MutableList<String> = ArrayList()
        for (str in requireActivity().resources.getStringArray(R.array.dow2)) dows.add(str)
        binding.pickdow.setEntries(dows)
        listAdapter = PickWeekListAdapter(requireActivity() as BaseActivity<*, *>)
        binding.weekList.adapter = listAdapter
        binding.weekList.layoutManager = GridLayoutManager(context, 5)
        listAdapter?.onItemCheckListener = object : PickWeekListAdapter.OnItemCheckListener {
            override fun onItemChecked(position: Int, checked: Boolean) {
                selectedTimesLiveData.value = getSelectedDates()
            }
        }
        datesAdapter = context?.let { SelectedDateAdapter(it, mutableListOf()) }
        binding.dateList.adapter = datesAdapter
        binding.dateList.layoutManager = ChipsLayoutManager.newBuilder(context)
            .setOrientation(ChipsLayoutManager.HORIZONTAL)
            .setMaxViewsInRow(4)
            .build()
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.done.setOnClickListener {
            val r = CourseTime()
            r.dow = binding.pickdow.currentIndex + 1
            r.period = timetable.transformTimePeriod(
                binding.pickfromt.currentIndex + 1,
                binding.picktot.currentIndex + 1
            )
            r.weeks = listAdapter?.getSelectedWeeks() ?: listOf()
            if (r.weeks.isEmpty()) {
                Toast.makeText(context, R.string.ade_pick_weeks, Toast.LENGTH_SHORT).show()
            } else {
                onTimeSelectedListener?.onSelected(r)
                dismiss()
            }
        }
        binding.pickfromt.setOnWheelChangedListener { _, _, newIndex ->
            if (binding.picktot.currentIndex < newIndex) binding.picktot.currentIndex = newIndex
            initTimetable?.let { tt ->
                binding.fromTime.text = tt.scheduleStructure[newIndex].from.toString()
            }
        }
        binding.picktot.setOnWheelChangedListener { _, _, newIndex ->
            if (binding.pickfromt.currentIndex > newIndex) binding.pickfromt.currentIndex = newIndex
            initTimetable?.let { tt ->
                binding.toTime.text = tt.scheduleStructure[newIndex].to.toString()
            }
        }
        binding.pickdow.setOnWheelChangedListener { view, oldIndex, newIndex ->
            selectedTimesLiveData.value = getSelectedDates()
        }
        bindLiveData()
    }


    private fun getSelectedDates(): List<Long> {
        val res = mutableListOf<Long>()
        for (wk in listAdapter?.getSelectedWeeks() ?: listOf()) {
            initTimetable?.getTimestamps(
                wk,
                binding.pickdow.currentIndex + 1,
                binding.pickfromt.currentIndex + 1,
                binding.picktot.currentIndex + 1
            )?.let {
                res.add(it[0])
            }
        }
        return res
    }

    fun setInitialValue(timetable: Timetable?, courseTime: CourseTime?): PopUpPickCourseTime {
        initTimetable = timetable
        initCourseTime = courseTime
        return this
    }

    fun setSelectListener(ls: OnTimeSelectedListener): PopUpPickCourseTime {
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
            val times = mutableListOf<String>()
            val periodTemp = getString(R.string.period)
            for (i in 0 until it.scheduleStructure.size) {
                times.add(String.format(periodTemp, i + 1))
            }
            binding.pickfromt.setEntries(times)
            binding.picktot.setEntries(times)
            if (it.scheduleStructure.isNotEmpty()) {
                binding.fromTime.text = it.scheduleStructure[0].from.toString()
                binding.toTime.text = it.scheduleStructure[0].to.toString()
            }
        }

        initCourseTime?.let { initCourseTime ->
            rangeLiveData.value =
                Pair(initCourseTime.dow, initCourseTime.period)
            for (i in initCourseTime.weeks) {
                if (i > weeksValue.size) for (x in weeksValue.size until i) weeksValue.add(false)
                weeksValue[i - 1] = true
            }
        }

        weeksLiveData.value = weeksValue
    }


    internal class PickWeekListAdapter(val mContext: BaseActivity<*, *>) :
        RecyclerView.Adapter<PickWeekListAdapter.mViewHolder?>() {

        var onItemCheckListener: OnItemCheckListener? = null

        interface OnItemCheckListener {
            fun onItemChecked(position: Int, checked: Boolean)
        }

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
                    onItemCheckListener?.onItemChecked(position, weeks[position])
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


    class SelectedDateAdapter(mContext: Context, mBeans: MutableList<String>) :
        BaseListAdapter<String, SelectedDateAdapter.DHolder>(
            mContext, mBeans
        ) {


        class DHolder(viewBinding: DynamicAddEventDateitemBinding) :
            BaseViewHolder<DynamicAddEventDateitemBinding>(
                viewBinding
            )

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DynamicAddEventDateitemBinding.inflate(mInflater, parent, false)
        }

        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): DHolder {
            return DHolder(viewBinding as DynamicAddEventDateitemBinding)
        }

        override fun bindHolder(holder: DHolder, data: String?, position: Int) {
            holder.binding.date.text = data
        }
    }

}