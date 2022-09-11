package com.stupidtree.hitax.ui.subject

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.databinding.ActivitySubjectBinding
import com.stupidtree.hitax.ui.event.add.PopupAddEvent
import com.stupidtree.style.widgets.PopUpFloatPicker
import com.stupidtree.hitax.utils.ActivityUtils
import com.stupidtree.hitax.utils.EditModeHelper
import com.stupidtree.hitax.utils.EventsUtils
import com.stupidtree.hitax.utils.TimeTools
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.widgets.PopUpSelectableList
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

class SubjectActivity : BaseActivity<SubjectViewModel, ActivitySubjectBinding>(),
    EditModeHelper.EditableContainer<EventItem> {
    var firstEnterCourse = true
    private lateinit var listAdapter: SubjectCoursesListAdapter
    lateinit var editModeHelper: EditModeHelper<EventItem>
    var isCourseExpanded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbarActionBack(binding.toolbar)
    }

    override fun initViews() {
        initCourseList()
        initInfoList()
        viewModel.subjectLiveData.observe(this) {
            binding.collapse.title = it.name
            binding.cardType.setTitle(getSubjectTypeName(it.type))
            binding.cardCredit.setTitle(getSubjectCreditKey(it))
        }
        viewModel.classesLiveData.observe(this) {
            if (it.isEmpty()) {
                isCourseExpanded = false
                listAdapter.notifyItemChangedSmooth(it)
            } else if (isCourseExpanded) {
                val temp: MutableList<EventItem> = ArrayList(it)
                temp.add(EventItem.getTagInstance("less"))
                listAdapter.notifyItemChangedSmooth(temp)
            } else {
                val max = it.size.coerceAtMost(5)
                val temp: MutableList<EventItem> = ArrayList(it.subList(0, max))
                if (it.size > 5) temp.add(EventItem.getTagInstance("more"))
                if (max > 0) listAdapter.notifyItemChangedSmooth(
                    temp,
                    true
                ) { o1, o2 ->
                    if (o1.type === EventItem.TYPE.TAG && o2.type === EventItem.TYPE.TAG) 0 else o1.compareTo(
                        o2
                    )
                }
            }
            if (firstEnterCourse) {
                binding.subjectRecycler.scheduleLayoutAnimation()
                firstEnterCourse = false
            }
            var finished = 0
            var unfinished = 0
            for (ei in it) {
                if (TimeTools.passed(ei.to)) finished++ else unfinished++
            }
            val percentage = finished.toFloat() * 100.0f / (finished + unfinished).toFloat()
            val va: ValueAnimator =
                ValueAnimator.ofInt(binding.subjectProgress.progress, percentage.toInt())
            va.duration = 700
            va.interpolator = DecelerateInterpolator(2f)
            va.addUpdateListener { animation ->
                binding.subjectProgress.progress = animation.animatedValue as Int
                binding.subjectProgressTitle.text =
                    getString(R.string.percentage, animation.animatedValue as Int)
            }
            va.startDelay = 160
            va.start()
        }
        viewModel.timetableLiveData.observe(this) { data ->
            binding.timetableName.text = data.name
            binding.timetableName.setTextColor(
                ContextCompat.getColor(this,when (TimeTools.getSeason(data.startTime.time)) {
                    TimeTools.SEASON.SPRING -> R.color.spring_text
                    TimeTools.SEASON.SUMMER -> R.color.summer_text
                    TimeTools.SEASON.AUTUMN -> R.color.autumn_text
                    else -> R.color.winter_text
                })
            )
            binding.timetableBg.setCardBackgroundColor(
                ContextCompat.getColor(this,when (TimeTools.getSeason(data.startTime.time)) {
                    TimeTools.SEASON.SPRING -> R.color.spring
                    TimeTools.SEASON.SUMMER -> R.color.summer
                    TimeTools.SEASON.AUTUMN -> R.color.autumn
                    else -> R.color.winter
                })
            )
            binding.timetableIcon.setImageResource(
                when (TimeTools.getSeason(data.startTime.time)) {
                    TimeTools.SEASON.SPRING -> R.drawable.season_spring
                    TimeTools.SEASON.SUMMER -> R.drawable.season_summer
                    TimeTools.SEASON.AUTUMN -> R.drawable.season_autumn
                    else -> R.drawable.season_winter
                }
            )

        }
        viewModel.teachersLiveData.observe(this) {
            val sb = StringBuilder()
            for (str in it) {
                sb.append(str).append(" ")
            }
            binding.cardTeacher.setTitle(sb.toString())
        }

//        delete!!.setOnClickListener { v ->
//            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//            val ad: AlertDialog =
//                AlertDialog.Builder(getThis()).setTitle(getString(R.string.delete_subject))
//                    .setMessage(getString(R.string.dialog_message_delete_subject))
//                    .setPositiveButton(
//                        getString(R.string.button_confirm),
//                        object : DialogInterface.OnClickListener {
//                            override fun onClick(dialog: DialogInterface, which: Int) {
//                                deleteSubjectTask(this@ActivitySubject, subject.getName()).execute()
//                            }
//                        }).setNegativeButton(getString(R.string.button_cancel), null).create()
//            ad.show()
//        }
//        addCourse!!.setOnClickListener(View.OnClickListener {
//            if (subject == null) return@OnClickListener
//            if (tempWholeCourses!!.size > 0) {
//                val first: EventItem = tempWholeCourses!![0]
//                FragmentAddEvent().setInitialType("course")
//                    .setInitialTag2(first.getTag2())
//                    .setInitialTag3(first.getTag3())
//                    .setInitialExtraName(subject.getName())
//                    .setTabSwitchable(false)
//                    .setExtraEditable(false)
//                    .show(getSupportFragmentManager(), "fae")
//            } else {
//                FragmentAddEvent().setInitialType("course")
//                    .setInitialExtraName(subject.getName())
//                    .setTabSwitchable(false)
//                    .setExtraEditable(false)
//                    .show(getSupportFragmentManager(), "fae")
//            }
//        })
    }

    private fun initInfoList() {
        binding.cardType.onCardClickListener = View.OnClickListener {
            viewModel.subjectLiveData.value?.let { subject ->
                PopUpSelectableList<TermSubject.TYPE>()
                    .setListData(
                        listOf(
                            getString(R.string.subject_exam),
                            getString(R.string.not_counted_in_GPA),
                            getString(R.string.subject_mooc)
                        ),
                        listOf(
                            TermSubject.TYPE.COM_A,
                            TermSubject.TYPE.COM_B,
                            TermSubject.TYPE.MOOC
                        )
                    )
                    .setInitValue(subject.type)
                    .setTitle(R.string.subject_type)
                    .setOnConfirmListener(object :
                        PopUpSelectableList.OnConfirmListener<TermSubject.TYPE> {
                        override fun onConfirm(title: String?, key: TermSubject.TYPE) {
                            subject.type = key
                            viewModel.startSaveSubject()
                        }

                    })
                    .show(supportFragmentManager, "pick")
            }
        }
        binding.cardTeacher.onCardClickListener = View.OnClickListener {
            viewModel.teachersLiveData.value?.let {
                val sb = it.joinToString(separator = ",")
                ActivityUtils.searchFor(
                    getThis(), sb, ActivityUtils.SearchType.TEACHER
                )
            }

        }
        binding.cardCredit.onCardClickListener = View.OnClickListener {

            viewModel.subjectLiveData.value?.let {
                PopUpFloatPicker()
                    .setDialogTitle(R.string.subject_credit)
                    .setInitialValue(
                        it.credit
                    )
                    .setOnDialogConformListener(object : PopUpFloatPicker.OnDialogConformListener {
                        override fun onClick(result: Float) {
                            it.credit = result
                            viewModel.startSaveSubject()
                        }
                    })
                    .show(supportFragmentManager, "pick")
            }

        }
        binding.timetableCard.setOnClickListener {
            viewModel.timetableLiveData.value?.let {
                ActivityUtils.startTimetableDetailActivity(this, it.id)
            }

        }
    }


    private fun getSubjectTypeName(type: TermSubject.TYPE): String {
        return when (type) {
            TermSubject.TYPE.MOOC -> getString(R.string.subject_mooc)
            TermSubject.TYPE.COM_A -> getString(
                R.string.subject_exam
            )
            else -> getString(R.string.not_counted_in_GPA)
        }
    }

    private fun getSubjectCreditKey(subject: TermSubject): String {
        val pt = Pattern.compile("[0-9]*[.]*[0-9]*")
        val matcher = pt.matcher(subject.credit.toString())
        val df = DecimalFormat("0.0")
        if (matcher.find()) {
            val c = matcher.group(0)
            if (TextUtils.isEmpty(c)) return "0.0"
            val d = java.lang.Double.valueOf(c)
            return df.format(d)
        }
        return "0.0"
    }

    override fun onBackPressed() {
        when (editModeHelper.isEditMode) {
            true -> editModeHelper.closeEditMode()
            else -> super.onBackPressed()
        }
    }

    private fun initCourseList() {
        listAdapter = SubjectCoursesListAdapter(this, mutableListOf())
        binding.subjectRecycler.adapter = listAdapter
        binding.subjectRecycler.layoutManager = ChipsLayoutManager.newBuilder(this)
            .setOrientation(ChipsLayoutManager.HORIZONTAL)
            .setMaxViewsInRow(4)
            .build()
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<EventItem> {
            override fun onItemClick(data: EventItem?, card: View?, position: Int) {
                data?.let {
                    if (it.type === EventItem.TYPE.TAG) {
                        toggleCourseExpand()
                    } else {
                        EventsUtils.showEventItem(getThis(), it)
                    }
                }
            }
        })
        listAdapter.setOnItemLongClickListener(object :
            BaseListAdapter.OnItemLongClickListener<EventItem> {
            override fun onItemLongClick(data: EventItem?, view: View?, position: Int): Boolean {
                if (editModeHelper.isEditMode) return false
                editModeHelper.activateEditMode(position)
                return true
            }
        })
        editModeHelper = EditModeHelper(this, listAdapter, this)
        editModeHelper.init(this, R.id.edit_bar, R.layout.edit_mode_bar_2)
        editModeHelper.smoothSwitch = true
        editModeHelper.closeEditMode()
        binding.courseAdd.setOnClickListener {
            viewModel.subjectLiveData.value?.let { ts ->
                viewModel.timetableLiveData.value?.let { tt ->
                    PopupAddEvent().setInitTimetable(tt).setInitSubject(ts)
                        .show(supportFragmentManager, "add_event")
                }

            }

        }
    }


    fun toggleCourseExpand() {
        val comparator: Comparator<EventItem> = Comparator<EventItem> { o1, o2 ->
            if (o1.type === EventItem.TYPE.TAG && o2.type === EventItem.TYPE.TAG) 0
            else o1.compareTo(o2)
        }
        val refreshJudge: BaseListAdapter.RefreshJudge<EventItem> =
            object : BaseListAdapter.RefreshJudge<EventItem> {
                override fun judge(oldData: EventItem, newData: EventItem): Boolean {
                    return newData.type === EventItem.TYPE.TAG
                }
            }
        viewModel.classesLiveData.value?.let {
            isCourseExpanded = if (isCourseExpanded) {
                val max = it.size.coerceAtMost(5)
                val temp: MutableList<EventItem> = ArrayList(it.subList(0, max))
                if (it.size > 5) temp.add(EventItem.getTagInstance("more"))
                if (max > 0) listAdapter.notifyItemChangedSmooth(temp, refreshJudge, comparator)
                false
            } else {
                val temp: MutableList<EventItem> = ArrayList(it)
                temp.add(EventItem.getTagInstance("less"))
                listAdapter.notifyItemChangedSmooth(temp, refreshJudge, comparator)
                true
            }
        }
    }


    override fun onStart() {
        super.onStart()
        intent.getStringExtra("subjectId")?.let { viewModel.startRefresh(it) }
    }

    override fun initViewBinding(): ActivitySubjectBinding {
        return ActivitySubjectBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<SubjectViewModel> {
        return SubjectViewModel::class.java
    }

    override fun onEditClosed() {

    }

    override fun onEditStarted() {

    }

    override fun onItemCheckedChanged(position: Int, checked: Boolean, currentSelected: Int) {

    }

    override fun onDelete(toDelete: Collection<EventItem>?) {
        toDelete?.let { viewModel.deleteCourses(it) }
        editModeHelper.closeEditMode()
    }
}