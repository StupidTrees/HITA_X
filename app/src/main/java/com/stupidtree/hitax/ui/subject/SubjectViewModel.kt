package com.stupidtree.hitax.ui.subject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TimetableRepository

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
    val subjectLiveData: LiveData<TermSubject> = Transformations.switchMap(subjectController) {
        return@switchMap subjectRepository.getSubjectById(it)
    }
    val timetableLiveData:LiveData<Timetable> = Transformations.switchMap(subjectLiveData){
        return@switchMap timetableRepository.getTimetablesById(it.timetableId)
    }

    val classesLiveData: LiveData<List<EventItem>> = Transformations.switchMap(subjectController) {
        return@switchMap timetableRepository.getClassesOfSubject(it)
    }

    val teachersLiveData: LiveData<List<String>> = Transformations.switchMap(subjectLiveData) {
        return@switchMap subjectRepository.getTeachersOfSubject(it.timetableId, it.id)
    }


    fun startRefresh(id: String) {
        subjectController.value = id
    }

    fun startSaveSubject() {
        subjectLiveData.value?.let { it1 -> subjectRepository.actionSaveSubjectInfo(it1) }
    }

    fun deleteCourses(list:Collection<EventItem>) {
       timetableRepository.actionDeleteEvents(list)
    }
}