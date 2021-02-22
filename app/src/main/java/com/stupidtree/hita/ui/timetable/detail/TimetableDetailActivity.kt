package com.stupidtree.hita.ui.timetable.detail

import android.content.*
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.databinding.ActivityTimetableDetailBinding
import com.stupidtree.hita.ui.base.BaseActivityWithReceiver
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.eas.imp.TimetableStructureListAdapter
import com.stupidtree.hita.ui.timetable.subject.SubjectsListAdapter
import com.stupidtree.hita.ui.timetable.subject.TeachersListAdapter
import com.stupidtree.hita.ui.widgets.PopUpCalendarPicker
import com.stupidtree.hita.ui.widgets.PopUpEditText
import com.stupidtree.hita.ui.widgets.PopUpTimePeriodPicker
import com.stupidtree.hita.utils.ActivityUtils
import com.stupidtree.hita.utils.EditModeHelper
import com.stupidtree.hita.utils.TextTools
import java.util.*
import kotlin.Comparator

class TimetableDetailActivity :
    BaseActivityWithReceiver<TimetableDetailViewModel, ActivityTimetableDetailBinding>() {

    private var subjectsAdapter: SubjectsListAdapter? = null
    private var teachersListAdapter: TeachersListAdapter? = null
    private lateinit var scheduleStructureAdapter: TimetableStructureListAdapter
    private var editModeHelper: EditModeHelper<Pair<TermSubject, Float>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
        setToolbarActionBack(binding.toolbar)
    }

    override fun getIntentFilter(): IntentFilter {
        val re = IntentFilter()
        re.addAction("TIMETABLE_CHANGED")
        return re
    }


    override fun initViews() {
        bindLiveData()
        binding.usercenterSubjectsList.setItemViewCacheSize(20)
        subjectsAdapter = SubjectsListAdapter(this, mutableListOf())
        binding.usercenterSubjectsList.layoutManager = LinearLayoutManager(this)
        binding.usercenterSubjectsList.adapter = subjectsAdapter
        teachersListAdapter = TeachersListAdapter(this, mutableListOf())
        binding.teachersList.adapter = teachersListAdapter
        binding.teachersList.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        subjectsAdapter?.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<Pair<TermSubject, Float>> {

            override fun onItemClick(data: Pair<TermSubject, Float>?, card: View?, position: Int) {
                if (data != null) {
                    ActivityUtils.startSubjectActivity(getThis(), data.first.id)
                }
            }
        })
        subjectsAdapter?.setOnItemLongClickListener(object :
            BaseListAdapter.OnItemLongClickListener<Pair<TermSubject, Float>> {

            override fun onItemLongClick(
                data: Pair<TermSubject, Float>?,
                view: View?,
                position: Int
            ): Boolean {
                if (data?.first?.type == TermSubject.TYPE.TAG) return false
                editModeHelper?.activateEditMode(position)
                return true
            }
        })
        subjectsAdapter?.let {
            editModeHelper = EditModeHelper(
                this,
                it,
                object : EditModeHelper.EditableContainer<Pair<TermSubject, Float>> {

                    override fun onEditClosed() {
                        binding.titleSubject.visibility = View.VISIBLE
                    }

                    override fun onEditStarted() {
                        binding.titleSubject.visibility = View.GONE
                    }

                    override fun onItemCheckedChanged(
                        position: Int,
                        checked: Boolean,
                        currentSelected: Int
                    ) {

                    }

                    override fun onDelete(toDelete: Collection<Pair<TermSubject, Float>>?) {
                    }

                })
        }
        editModeHelper?.init(this, R.id.edit_layout, R.layout.edit_mode_bar_3)
        editModeHelper?.smoothSwitch = true
        scheduleStructureAdapter = TimetableStructureListAdapter(this, mutableListOf())
        binding.scheduleStructure.adapter = scheduleStructureAdapter
        binding.scheduleStructure.layoutManager = LinearLayoutManager(this)
        scheduleStructureAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<TimePeriodInDay> {
            override fun onItemClick(data: TimePeriodInDay?, card: View?, position: Int) {
                if (data == null) return
                PopUpTimePeriodPicker().setInitialValue(data.from, data.to)
                    .setDialogTitle(R.string.pick_time_period)
                    .setOnDialogConformListener(object :
                        PopUpTimePeriodPicker.OnDialogConformListener {
                        override fun onClick(
                            timePeriodInDay: TimePeriodInDay
                        ) {
                            viewModel.startChangeTimetableStructure(timePeriodInDay, position)

                        }

                    }).show(supportFragmentManager, "pick")
            }

        })

        binding.cardDate.onCardClickListener = View.OnClickListener {
            PopUpCalendarPicker().setInitValue(viewModel.timetableLiveData.value?.startTime?.time)
                .setOnConfirmListener(object : PopUpCalendarPicker.OnConfirmListener {
                    override fun onConfirm(c: Calendar) {
                        viewModel.timetableLiveData.value?.let {
                            c.firstDayOfWeek = Calendar.MONDAY
                            c[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
                            it.startTime.time = c.timeInMillis
                            viewModel.startSaveTimetableInfo()
                        }
                    }
                }).show(supportFragmentManager, "pick")

        }
        binding.cardName.onCardClickListener = View.OnClickListener {
            viewModel.timetableLiveData.value?.let {
                PopUpEditText().setTitle(R.string.notifi_curriculum_set_name)
                    .setText(it.name)
                    .setOnConfirmListener(object : PopUpEditText.OnConfirmListener {
                        override fun OnConfirm(text: String) {
                            if (text.isBlank()) return
                            it.name = text
                            viewModel.startSaveTimetableInfo()
                        }

                    }).show(supportFragmentManager, "pick")
            }

        }
    }

    private fun bindLiveData() {
        viewModel.timetableLiveData.observe(this) {
            binding.collapse.title = it.name
            binding.cardName.setTitle(it.name)
            val c = Calendar.getInstance()
            c.timeInMillis = it.startTime.time
            binding.cardDate.setTitle(TextTools.getNormalDateText(getThis(), c))
            scheduleStructureAdapter.notifyItemChangedSmooth(it.scheduleStructure)
        }

        viewModel.subjectsLiveData.observe(this) {
            if (subjectsAdapter?.beans?.isNullOrEmpty() == true) {
                subjectsAdapter?.notifyDataSetChanged(it)
                binding.usercenterSubjectsList.scheduleLayoutAnimation()
            } else {
                subjectsAdapter?.notifyItemChangedSmooth(it, false, Comparator { o1, o2 ->
                    return@Comparator if (o1.second != o2.second) 1 else o1.first.name.compareTo(
                        o2.first.name
                    )
                })
            }
        }
        viewModel.teacherInfoLiveData.observe(this) {
            if (teachersListAdapter?.beans?.isNullOrEmpty() == true) {
                teachersListAdapter?.notifyDataSetChanged(it)
                binding.teachersList.scheduleLayoutAnimation()
            } else {
                teachersListAdapter?.notifyItemChangedSmooth(it)
            }
        }

    }


    override fun onStart() {
        super.onStart()
        intent.getStringExtra("id")?.let {
            viewModel.startRefresh(it)
        }
    }


    override fun initViewBinding(): ActivityTimetableDetailBinding {
        return ActivityTimetableDetailBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<TimetableDetailViewModel> {
        return TimetableDetailViewModel::class.java
    }

    override var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }
}