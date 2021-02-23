package com.stupidtree.hita.ui.search.teacher

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.hita.data.repository.TeacherInfoRepository
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.search.BaseSearchResultViewModel
import com.stupidtree.hita.ui.search.SearchTrigger

class SearchTeacherViewModel(application: Application) : BaseSearchResultViewModel<TeacherSearched>(
    application
) {

    private val teacherInfoRepository = TeacherInfoRepository.getInstance(application)
    override fun doSearch(trigger: SearchTrigger): LiveData<DataState<List<TeacherSearched>>> {
        return teacherInfoRepository.searchTeachers(trigger.text)
    }
}