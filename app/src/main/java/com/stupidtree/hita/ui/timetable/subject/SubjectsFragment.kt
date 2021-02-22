package com.stupidtree.hita.ui.timetable.subject

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.hita.R
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.databinding.FragmentTimetableSubjetsBinding
import com.stupidtree.hita.ui.base.BaseListAdapter
import com.stupidtree.hita.ui.eas.imp.TimetableStructureListAdapter
import com.stupidtree.hita.ui.timetable.FragmentTimeTableChild
import com.stupidtree.hita.ui.widgets.PopUpTimePeriodPicker
import com.stupidtree.hita.utils.ActivityUtils
import com.stupidtree.hita.utils.EditModeHelper
import java.util.*
import kotlin.Comparator

class SubjectsFragment : FragmentTimeTableChild<SubjectsViewModel, FragmentTimetableSubjetsBinding>(){


    private fun bindLiveData() {

    }

    override fun initViews(view: View) {
        bindLiveData()

    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun initViewBinding(): FragmentTimetableSubjetsBinding {
        return FragmentTimetableSubjetsBinding.inflate(layoutInflater)
    }


    override fun getViewModelClass(): Class<SubjectsViewModel> {
        return SubjectsViewModel::class.java
    }


    override fun refresh() {
//        root?.getCurriculum()?.let {
//            viewModel.startRefresh(it.id)
//        }
    }
}