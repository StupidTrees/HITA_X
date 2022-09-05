package com.stupidtree.hitax.ui.event.add

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TeacherInfoRepository
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.databinding.DialogBottomAddEventBinding
import com.stupidtree.hitax.ui.widgets.PopUpPickCourseTime
import com.stupidtree.style.widgets.DialogAutoEditText
import com.stupidtree.style.widgets.DialogSelectableLiveList
import com.stupidtree.style.widgets.TransparentModeledBottomSheetDialog

class PopupAddEvent(private val addSubjectMode:Boolean = false) :
    TransparentModeledBottomSheetDialog<AddEventViewModel, DialogBottomAddEventBinding>() {

    var initTimetable: Timetable? = null
    var initSubject: TermSubject? = null


    override fun getViewModelClass(): Class<AddEventViewModel> {
        return AddEventViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_add_event
    }

    override fun initViewBinding(v: View): DialogBottomAddEventBinding {
        return DialogBottomAddEventBinding.bind(v)
    }

    fun setInitTimetable(timetable: Timetable?): PopupAddEvent {
        initTimetable = timetable
        return this
    }

    fun setInitSubject(subject: TermSubject): PopupAddEvent {
        initSubject = subject
        return this
    }


    @SuppressLint("SetTextI18n")
    override fun initViews(view: View) {
        isCancelable = false
        binding?.title?.setText(if(addSubjectMode) R.string.add_subject else R.string.ade_title)
        binding?.cancel?.setOnClickListener {
            dismiss()
        }
        viewModel.doneLiveData.observe(this) {
            if (it) {
                binding?.adeBtDone?.show()
            } else {
                binding?.adeBtDone?.hide()
            }
        }
        viewModel.teacherLiveData.observe(this) {
            binding?.pickTeacher?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickTeacherIcon?.setColorFilter(getColorPrimary())
                binding?.pickTeacherText?.setTextColor(getColorPrimary())
                binding?.pickTeacher?.setCardBackgroundColor(getColorPrimary())
                binding?.pickTeacherText?.text = it.data
                binding?.pickTeacherCancel?.visibility = View.VISIBLE
            } else {
                binding?.pickTeacherText?.text = getString(R.string.ade_pick_teacher)
                binding?.pickTeacher?.setCardBackgroundColor(
                    getColorPrimary()
                )
                binding?.pickTeacherText?.setTextColor(getTextColorSecondary())
                // binding?.pickTeacherIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal));
                binding?.pickTeacherCancel?.visibility = View.GONE
                binding?.pickTeacherIcon?.clearColorFilter()
            }
        }
        viewModel.nameLiveData.observe(this) {

        }
        viewModel.locationLiveData.observe(this) {
            binding?.pickLocation?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickLocationIcon?.setColorFilter(getColorPrimary())
                binding?.pickLocationText?.setTextColor(getColorPrimary())
                binding?.pickLocation?.setCardBackgroundColor(getColorPrimary())
                binding?.pickLocationText?.text = it.data
                binding?.pickLocationCancel?.visibility = View.VISIBLE
            } else {
                binding?.pickLocationText?.text = getString(R.string.ade_pick_location)
                binding?.pickLocationText?.postInvalidate()
                binding?.pickLocation?.setCardBackgroundColor(getTextColorSecondary())
                binding?.pickLocationText?.setTextColor(getTextColorSecondary())
                binding?.pickLocationIcon?.clearColorFilter()
                //binding?.pickLocationIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
                binding?.pickLocationCancel?.visibility = View.GONE
            }
        }
        viewModel.subjectLiveData.observe(this) {
            binding?.pickSubject?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED||it.state == DataState.STATE.SPECIAL) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickSubjectIcon?.setColorFilter(getColorPrimary())
                binding?.pickSubjectText?.setTextColor(getColorPrimary())
                binding?.pickSubject?.setCardBackgroundColor(getColorPrimary())
                binding?.pickSubjectText?.text = it.data?.name
                binding?.name?.setText(it.data?.name)
            } else {
                binding?.pickSubjectText?.text = getString(R.string.ade_pick_subject)
                binding?.pickSubjectText?.postInvalidate()
                binding?.pickSubject?.setCardBackgroundColor(getTextColorSecondary())
                binding?.pickSubjectText?.setTextColor(getTextColorSecondary())
                binding?.pickSubjectIcon?.clearColorFilter()
                //binding?.pickSubjectIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
            }
        }
        viewModel.timetableLiveData.observe(this) {
            binding?.pickTimetable?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickTimetableIcon?.setColorFilter(getColorPrimary())
                binding?.pickTimetableText?.setTextColor(getColorPrimary())
                binding?.pickTimetable?.setCardBackgroundColor(getColorPrimary())
                binding?.pickTimetableText?.text = it.data?.name
            } else {
                binding?.pickTimetableText?.text = getString(R.string.ade_pick_timetable)
                binding?.pickTimetableText?.postInvalidate()
                binding?.pickTimetable?.setCardBackgroundColor(getTextColorSecondary())
                binding?.pickTimetableText?.setTextColor(getTextColorSecondary())
                binding?.pickTimetableIcon?.clearColorFilter()
                //binding?.pickSubjectIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
            }
        }
        viewModel.timeRangeLiveDate.observe(this) {
            binding?.pickTime?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickTime?.setCardBackgroundColor(getColorPrimary())
                binding?.pickTimeIcon?.setColorFilter(getColorPrimary())
                binding?.timeShow?.setTextColor(getColorPrimary())
                it.data?.let { ct ->
                    val t1 =
                        resources.getStringArray(R.array.dow1)[ct.dow - 1].toString() + " " + ct.begin + "-" + ct.end
                    val set = HashSet<Int>()
                    val frags = mutableListOf<String>()
                    for (i in ct.weeks) {
                        set.add(i)
                    }
                    for (s in set) {
                        if (set.contains(s - 1)) continue
                        var length = 0
                        var ts = s
                        while (set.contains(ts + 1)) {
                            length++
                            ts++
                        }
                        when (ts) {
                            s -> {
                                frags.add("$s")
                            }
                            s + 1 -> {
                                frags.add("$s")
                                frags.add("$ts")
                            }
                            else -> {
                                frags.add("$s-$ts")
                            }
                        }
                    }
                    binding?.timeShow?.text = "${frags.joinToString(", ")}周 $t1 节"
                }
            } else {
                binding?.timeShow?.text = getString(R.string.ade_set_time_period)
                binding?.pickTime?.setCardBackgroundColor(
                    getTextColorSecondary()
                )
                binding?.timeShow?.setTextColor(getTextColorSecondary())
                binding?.pickTimeIcon?.clearColorFilter()
                //pickTimeIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
            }
        }
        viewModel.teacherLiveData.observe(this) {
            binding?.pickTeacher?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickTeacher?.setCardBackgroundColor(getColorPrimary())
                binding?.pickTeacherIcon?.setColorFilter(getColorPrimary())
                binding?.pickTeacherText?.setTextColor(getColorPrimary())
                it.data?.let { ct ->
                    binding?.pickTeacherText?.text = ct
                }
            } else {
                binding?.pickTeacherText?.text = getString(R.string.ade_pick_teacher)
                binding?.pickTeacher?.setCardBackgroundColor(
                    getTextColorSecondary()
                )
                binding?.pickTeacherText?.setTextColor(getTextColorSecondary())
                binding?.pickTeacherIcon?.clearColorFilter()
                //pickTeacherIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
            }
        }

        viewModel.locationLiveData.observe(this) {
            binding?.pickLocation?.visibility =
                if (it.state == DataState.STATE.FETCH_FAILED) View.GONE else View.VISIBLE
            if (it.state == DataState.STATE.SUCCESS) {
                binding?.pickLocation?.setCardBackgroundColor(getColorPrimary())
                binding?.pickLocationIcon?.setColorFilter(getColorPrimary())
                binding?.pickLocationText?.setTextColor(getColorPrimary())
                it.data?.let { ct ->
                    binding?.pickLocationText?.text = ct
                }
            } else {
                binding?.pickLocationText?.text = getString(R.string.ade_pick_location)
                binding?.pickLocation?.setCardBackgroundColor(
                    getTextColorSecondary()
                )
                binding?.pickLocationText?.setTextColor(getTextColorSecondary())
                binding?.pickLocationIcon?.clearColorFilter()
                //pickLocationIcon.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.color_control_normal), PorterDuff.Mode.SRC_IN);
            }
        }
        binding?.pickTimetable?.setOnClickListener {
            DialogSelectableLiveList<Timetable>().setTitle(R.string.ade_pick_timetable)
                .setInitValue(viewModel.timetableLiveData.value?.data)
                .setDataLoader(object : DialogSelectableLiveList.DataLoader<Timetable> {
                    override fun loadData(): LiveData<List<DialogSelectableLiveList.ItemData<Timetable>>> {
                        return Transformations.switchMap(
                            TimetableRepository.getInstance(activity!!.application).getTimetables()
                        ) {
                            val res = mutableListOf<DialogSelectableLiveList.ItemData<Timetable>>()
                            for (data: Timetable in it) {
                                res.add(DialogSelectableLiveList.ItemData(data.name, data))
                            }
                            return@switchMap MutableLiveData(res)
                        }
                    }
                }).setOnConfirmListener(object :
                    DialogSelectableLiveList.OnConfirmListener<Timetable> {
                    override fun onConfirm(title: String?, key: Timetable) {
                        viewModel.timetableLiveData.value = DataState(key)
                    }
                }).show(childFragmentManager, "set_timetable")
        }

        binding?.pickSubject?.setOnClickListener {
            DialogSelectableLiveList<TermSubject>().setTitle(R.string.ade_pick_subject)
                .setInitValue(viewModel.subjectLiveData.value?.data)
                .setDataLoader(object : DialogSelectableLiveList.DataLoader<TermSubject> {
                    override fun loadData(): LiveData<List<DialogSelectableLiveList.ItemData<TermSubject>>> {
                        return Transformations.switchMap(
                            SubjectRepository.getInstance(activity!!.application)
                                .getSubjects(viewModel.timetableLiveData.value?.data?.id ?: "")
                        ) {
                            val res =
                                mutableListOf<DialogSelectableLiveList.ItemData<TermSubject>>()
                            for (data: TermSubject in it) {
                                res.add(DialogSelectableLiveList.ItemData(data.name, data))
                            }
                            return@switchMap MutableLiveData(res)
                        }
                    }
                }).setOnConfirmListener(object :
                    DialogSelectableLiveList.OnConfirmListener<TermSubject> {
                    override fun onConfirm(title: String?, key: TermSubject) {
                        viewModel.subjectLiveData.value = DataState(key)
                    }
                }).show(childFragmentManager, "set_subject")
        }
        binding?.pickTime?.setOnClickListener {
            viewModel.timetableLiveData.value?.data?.let { tt ->
                PopUpPickCourseTime().setInitialValue(tt, viewModel.timeRangeLiveDate.value?.data)
                    .setSelectListener(object : PopUpPickCourseTime.OnTimeSelectedListener {
                        override fun onSelected(data: CourseTime) {
                            if(data.weeks.isEmpty()) viewModel.timetableLiveData.value = DataState(DataState.STATE.NOTHING)
                            else viewModel.timeRangeLiveDate.value = DataState(data)
                        }
                    }).show(childFragmentManager, "pick_course_time")
            }

        }

        binding?.pickTeacherCancel?.setOnClickListener {
            viewModel.teacherLiveData.value = DataState(DataState.STATE.NOTHING)
        }
        binding?.pickTeacher?.setOnClickListener {
            DialogAutoEditText().setTitle(getString(R.string.ade_pick_teacher))
                .setOnConfirmListener(object : DialogAutoEditText.OnConfirmListener {
                    override fun OnConfirm(content: String) {
                        viewModel.teacherLiveData.value = DataState(content)
                    }

                }).setInitValue(viewModel.teacherLiveData.value?.data ?: "")
                .setDataLoader(object : DialogAutoEditText.DataLoader {
                    override fun loadData(str: String): LiveData<List<String>> {
                        return Transformations.switchMap(
                            TeacherInfoRepository.getInstance(activity!!.application)
                                .searchTeachers(str)
                        ) {
                            val r = mutableListOf<String>()
                            it.data?.let { dt ->
                                for (t in dt) {
                                    r.add(t.name)
                                }
                            }
                            return@switchMap MutableLiveData(r)
                        }
                    }

                }).show(childFragmentManager, "pick_teacher")
        }


        binding?.pickLocationCancel?.setOnClickListener {
            viewModel.locationLiveData.value = DataState(DataState.STATE.NOTHING)
        }
        binding?.pickLocation?.setOnClickListener {
            DialogAutoEditText().setTitle(getString(R.string.ade_pick_location))
                .setOnConfirmListener(object : DialogAutoEditText.OnConfirmListener {
                    override fun OnConfirm(content: String) {
                        viewModel.locationLiveData.value = DataState(content)
                    }

                }).setInitValue(viewModel.locationLiveData.value?.data ?: "")
                .setDataLoader(object : DialogAutoEditText.DataLoader {
                    override fun loadData(str: String): LiveData<List<String>> {
                        return TimetableRepository.getInstance(activity!!.application)
                            .searchLocation(str)
                    }

                }).show(childFragmentManager, "pick_location")
        }

        binding?.adeBtDone?.setOnClickListener {
            viewModel.createEvent()
            dismiss()
        }
        binding?.name?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.nameLiveData.value = p0.toString()
            }

        })
        viewModel.init(addSubjectMode,initTimetable, initSubject)
    }
}