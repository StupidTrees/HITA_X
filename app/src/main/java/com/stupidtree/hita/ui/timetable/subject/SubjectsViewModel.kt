package com.stupidtree.hita.ui.timetable.subject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.repository.SubjectRepository
import com.stupidtree.hita.ui.base.StringTrigger

class SubjectsViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * 仓库区
     */
    private val subjectsRepository = SubjectRepository.getInstance(application)


    private val timetableIdLiveData = MutableLiveData<StringTrigger>()

    val subjectsLiveData:LiveData<MutableList<Pair<TermSubject,Float>>> = Transformations.switchMap(timetableIdLiveData){
        return@switchMap subjectsRepository.getSubjectsAndProgress(it.data)
    }

    private val subjectRepository = SubjectRepository.getInstance(application)

    val teacherInfoLiveData:LiveData<MutableList<TeacherInfo>> = Transformations.switchMap(timetableIdLiveData){
        return@switchMap subjectRepository.getTeachersInfo(it.data)
    }


    fun startRefresh(timetableId:String){
        timetableIdLiveData.value = StringTrigger.getActioning(timetableId)
    }

}