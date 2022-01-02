package com.stupidtree.hitax.ui.eas.score

import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.databinding.DialogBottomScoresPickerBinding
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class ScoreDetailFragment(
    private val score: CourseScoreItem
): TransparentBottomSheetDialog<DialogBottomScoresPickerBinding>(){
    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_scores_picker
    }

    override fun initViewBinding(v: View): DialogBottomScoresPickerBinding {
        return DialogBottomScoresPickerBinding.bind(v)
    }

    override fun initViews(v: View) {
        binding.scoreCredits.text = score.credits.toString()
        binding.scoreAssessMethod.text = score.assessMethod
        binding.scoreCategory.text = score.courseCategory
        binding.scoreCode.text = score.courseCode
        binding.scoreHours.text = score.hours.toString()
        binding.scorePorperty.text = score.courseProperty
        binding.scoreSchoolName.text = score.schoolName
        binding.title.text = score.courseName
    }
}