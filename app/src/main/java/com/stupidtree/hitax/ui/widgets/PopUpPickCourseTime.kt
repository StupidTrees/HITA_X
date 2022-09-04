package com.stupidtree.hitax.ui.widgets

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.DialogBottomPickCourseTimeBinding
import com.stupidtree.hitax.ui.event.add.CourseTime
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class PopUpPickCourseTime : TransparentBottomSheetDialog<DialogBottomPickCourseTimeBinding>() {
    var rangeLiveData: MutableLiveData<Triple<Int, Int, Int>> = MutableLiveData()
    var onTimeSelectedListener: OnTimeSelectedListener? = null
    var initCourseTime: CourseTime? = null

    init {
        isCancelable = false
    }

    interface OnTimeSelectedListener {
        fun onSelected(data: CourseTime)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_pick_course_time
    }

    override fun initViewBinding(v: View): DialogBottomPickCourseTimeBinding {
        return DialogBottomPickCourseTimeBinding.bind(v)
    }

    override fun initViews(v: View) {

        val dows: MutableList<String> = ArrayList()
        for (str in requireActivity().resources.getStringArray(R.array.dow2)) dows.add(str)
        binding.pickdow.setEntries(dows)
        val times = mutableListOf<String>()
        val periodTemp = getString(R.string.period)
        for (i in 0..11) {
            times.add(String.format(periodTemp, i + 1))
        }
        binding.pickfromt.setEntries(times)
        binding.picktot.setEntries(times)

        rangeLiveData.observe(this) {
            binding.pickdow.currentIndex = it.first - 1
            binding.picktot.currentIndex = it.third - 1
            binding.pickfromt.currentIndex = it.second - 1
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.done.setOnClickListener {
            val r = CourseTime()
            r.dow = binding.pickdow.currentIndex + 1
            r.begin = binding.pickfromt.currentIndex + 1
            r.end = binding.picktot.currentIndex + 1
            onTimeSelectedListener?.onSelected(r)
            dismiss()
        }
        binding.pickfromt.setOnWheelChangedListener { _, _, newIndex ->
            if (binding.picktot.currentIndex < newIndex) binding.picktot.currentIndex = newIndex
        }
        binding.picktot.setOnWheelChangedListener { _, _, newIndex ->
            if (binding.pickfromt.currentIndex > newIndex) binding.pickfromt.currentIndex = newIndex
        }
    }


    fun setInitialValue(courseTime: CourseTime?): PopUpPickCourseTime {
        initCourseTime = courseTime
        return this
    }

    fun setSelectListener(ls: OnTimeSelectedListener): PopUpPickCourseTime {
        onTimeSelectedListener = ls
        return this
    }


    override fun onStart() {
        super.onStart()
        initCourseTime?.let { initCourseTime ->
            rangeLiveData.value =
                Triple(initCourseTime.dow, initCourseTime.begin, initCourseTime.end)
        }
    }

}