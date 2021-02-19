package com.stupidtree.hita.ui.subject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.repository.SubjectRepository
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.StringTrigger

class SubjectViewModel(application: Application) : AndroidViewModel(application) {


    /**
     * 仓库区
     */
    private val subjectRepository = SubjectRepository.getInstance(application)
    private val timetableRepository = TimetableRepository.getInstance(application)
    /**
     * LiveData区
     */
    private val subjectController = MutableLiveData<String>()
    val subjectLiveData: LiveData<TermSubject> = Transformations.switchMap(subjectController){
        return@switchMap subjectRepository.getSubjectById(it)
    }

    val classesLiveData: LiveData<List<EventItem>> = Transformations.switchMap(subjectController){
        return@switchMap timetableRepository.getClassesOfSubject(it)
    }

    val teachersLiveData:LiveData<List<String>> = Transformations.switchMap(subjectLiveData){
        return@switchMap subjectRepository.getTeachersOfSubject(it.timetableId,it.id)
    }

    fun startRefresh(id: String) {
        subjectController.value = id
    }
}