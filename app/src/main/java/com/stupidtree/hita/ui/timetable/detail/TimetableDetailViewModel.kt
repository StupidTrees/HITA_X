package com.stupidtree.hita.ui.timetable.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.repository.SubjectRepository
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.StringTrigger
import com.stupidtree.hita.ui.timetable.subject.TeacherInfo

class TimetableDetailViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val subjectsRepository = SubjectRepository.getInstance(application)



    /**
     * 数据区
     */
    private val timetableController: MutableLiveData<StringTrigger> = MutableLiveData()

    val subjectsLiveData:LiveData<List<TermSubject>> = Transformations.switchMap(timetableController){
        return@switchMap subjectsRepository.getSubjects(it.data)
    }
    val teacherInfoLiveData:LiveData<MutableList<TeacherInfo>> = Transformations.switchMap(timetableController){
        return@switchMap subjectsRepository.getTeachersInfo(it.data)
    }
    val timetableLiveData: LiveData<Timetable> = Transformations.switchMap(timetableController) {
        return@switchMap timetableRepository.getTimetablesById(it.data)
    }



    /**
     * 方法
     */
    fun startRefresh(id:String){
        timetableController.value = StringTrigger.getActioning(id)
    }

    fun startSaveTimetableInfo(){
        timetableLiveData.value?.let { timetableRepository.actionSaveTimetable(it) }
    }

    fun startChangeTimetableStructure(tp:TimePeriodInDay,position:Int){
        timetableLiveData.value?.let {
            timetableRepository.actionChangeTimetableStructure(it,tp,position)
        }
    }

    fun startResetSubjectColors(){
        timetableLiveData.value?.let {
            subjectsRepository.actionResetSubjectColors(it.id)
        }
    }

    fun getSubjectProgress(subjectId:String):LiveData<Pair<Int,Int>>{
        return subjectsRepository.getProgressOfSubject(subjectId,System.currentTimeMillis())
    }
}