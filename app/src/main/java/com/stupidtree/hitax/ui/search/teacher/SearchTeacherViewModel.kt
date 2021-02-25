package com.stupidtree.hitax.ui.search.teacher

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.hitax.data.repository.TeacherInfoRepository
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.search.BaseSearchResultViewModel
import com.stupidtree.hitax.ui.search.SearchTrigger

class SearchTeacherViewModel(application: Application) : BaseSearchResultViewModel<TeacherSearched>(
    application
) {

    private val teacherInfoRepository = TeacherInfoRepository.getInstance(application)
    override fun doSearch(trigger: SearchTrigger): LiveData<DataState<List<TeacherSearched>>> {
        return teacherInfoRepository.searchTeachers(trigger.text)
    }
}