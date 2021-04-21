package com.stupidtree.hitax.ui.timetable.detail

import android.content.*
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.databinding.ActivityTimetableDetailBinding
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.hitax.ui.eas.imp.TimetableStructureListAdapter
import com.stupidtree.hitax.ui.widgets.PopUpCalendarPicker
import com.stupidtree.hitax.ui.widgets.PopUpEditText
import com.stupidtree.hitax.ui.widgets.PopUpTimePeriodPicker
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.EditModeHelper
import com.stupidtree.hitax.utils.TextTools
import com.stupidtree.style.widgets.PopUpText
import java.util.*
import kotlin.Comparator

class TimetableDetailActivity :
    BaseActivity<TimetableDetailViewModel, ActivityTimetableDetailBinding>() {

    private var subjectsAdapter: SubjectsListAdapter? = null
    private var teachersListAdapter: TeachersListAdapter? = null
    private lateinit var scheduleStructureAdapter: TimetableStructureListAdapter
    private var editModeHelper: EditModeHelper<TermSubject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbarActionBack(binding.toolbar)
    }


    override fun initViews() {
        bindLiveData()
        binding.usercenterSubjectsList.setItemViewCacheSize(20)
        subjectsAdapter = SubjectsListAdapter(this, mutableListOf(), viewModel, this)
        binding.usercenterSubjectsList.layoutManager = LinearLayoutManager(this)
        binding.usercenterSubjectsList.adapter = subjectsAdapter
        teachersListAdapter = TeachersListAdapter(this, mutableListOf())
        binding.teachersList.adapter = teachersListAdapter
        binding.teachersList.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        teachersListAdapter?.setOnItemClickListener(object:BaseListAdapter.OnItemClickListener<TeacherInfo>{
            override fun onItemClick(data: TeacherInfo?, card: View?, position: Int) {
                ActivityUtils.searchFor(getThis(),data?.name,ActivityUtils.SearchType.TEACHER)
            }

        })
        subjectsAdapter?.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<TermSubject> {

            override fun onItemClick(data: TermSubject?, card: View?, position: Int) {
                if (data != null) {
                    ActivityUtils.startSubjectActivity(getThis(), data.id)
                }
            }
        })
        subjectsAdapter?.setOnItemLongClickListener(object :
            BaseListAdapter.OnItemLongClickListener<TermSubject> {

            override fun onItemLongClick(
                data: TermSubject?,
                view: View?,
                position: Int
            ): Boolean {
                if (data?.type == TermSubject.TYPE.TAG) return false
                editModeHelper?.activateEditMode(position)
                return true
            }
        })
        subjectsAdapter?.let {
            editModeHelper = EditModeHelper(
                this,
                it,
                object : EditModeHelper.EditableContainer<TermSubject> {

                    override fun onEditClosed() {
                        binding.subjectLabel.visibility = View.VISIBLE
                    }

                    override fun onEditStarted() {
                        binding.subjectLabel.visibility = View.GONE
                    }

                    override fun onItemCheckedChanged(
                        position: Int,
                        checked: Boolean,
                        currentSelected: Int
                    ) {

                    }

                    override fun onDelete(toDelete: Collection<TermSubject>?) {
                        editModeHelper?.closeEditMode()
                        toDelete?.let { it1 -> viewModel.startDeleteSubjects(it1) }
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
                            viewModel.startChangeTimetableStartTime(c.timeInMillis)
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
        binding.reset.setOnClickListener {
            PopUpText().setTitle(R.string.dialog_title_random_allocate)
                .setOnConfirmListener(object : PopUpText.OnConfirmListener {
                    override fun OnConfirm() {
                        viewModel.startResetSubjectColors()
                    }

                }).show(supportFragmentManager, "sure")
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
                subjectsAdapter?.notifyItemChangedSmooth(
                    it,
                    object : BaseListAdapter.RefreshJudge<TermSubject> {
                        override fun judge(oldData: TermSubject, newData: TermSubject): Boolean {
                            return oldData.name != newData.name || oldData.color != newData.color
                        }
                    },
                    Comparator { o1, o2 ->
                        return@Comparator o1.name.compareTo(o2.name)
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
}