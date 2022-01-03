package com.stupidtree.hitax.ui.eas.exam

import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.eas.ExamItem
import com.stupidtree.hitax.databinding.DialogBottomExamPickerBinding
import com.stupidtree.hitax.databinding.DialogBottomScoresPickerBinding
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class ExamDetailFragment (
    private val Exam: ExamItem
): TransparentBottomSheetDialog<DialogBottomExamPickerBinding>(){
    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_exam_picker
    }

    override fun initViewBinding(v: View): DialogBottomExamPickerBinding {
        return DialogBottomExamPickerBinding.bind(v)
    }

    override fun initViews(v: View) {
        binding.title.text = Exam.courseName
        binding.examCampus.text = Exam.campusName
        binding.examLocation.text = Exam.examLocation
        binding.examTerm.text = Exam.termName
        binding.examTime.text = Exam.examTime
        binding.examType.text = Exam.examType
    }
}