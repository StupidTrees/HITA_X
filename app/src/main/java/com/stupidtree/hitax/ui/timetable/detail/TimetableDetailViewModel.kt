package com.stupidtree.hitax.ui.timetable.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.StringTrigger
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TimetableRepository

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

    val subjectsLiveData: LiveData<List<TermSubject>> =
        timetableController.switchMap{
            return@switchMap subjectsRepository.getSubjects(it.data)
        }
    val teacherInfoLiveData: LiveData<MutableList<TeacherInfo>> =
        timetableController.switchMap{
            return@switchMap subjectsRepository.getTeachersInfo(it.data)
        }
    val timetableLiveData: LiveData<Timetable> = timetableController.switchMap{
        return@switchMap timetableRepository.getTimetablesById(it.data)
    }

    private val exportController = MutableLiveData<Timetable>()
    val exportToICSResult =  exportController.switchMap{
        return@switchMap timetableRepository.exportToICS(it.name?:"课表",it.id)
    }


    /**
     * 方法
     */

    fun getSubjectProgress(subjectId: String): LiveData<Pair<Int, Int>> {
        return subjectsRepository.getProgressOfSubject(subjectId, System.currentTimeMillis())
    }

    fun startRefresh(id: String) {
        timetableController.value = StringTrigger.getActioning(id)
    }

    fun startSaveTimetableInfo() {
        timetableLiveData.value?.let { timetableRepository.actionSaveTimetable(it) }
    }

    fun startChangeTimetableStructure(tp: TimePeriodInDay, position: Int) {
        timetableLiveData.value?.let {
            timetableRepository.actionChangeTimetableStructure(it, tp, position)
        }
    }

    fun startChangeTimetableStartTime(startTime: Long) {
        timetableLiveData.value?.let {
            timetableRepository.actionChangeTimetableStartDate(it, startTime)
        }
    }

    fun startResetSubjectColors() {
        timetableLiveData.value?.let {
            subjectsRepository.actionResetSubjectColors(it.id)
        }
    }

    fun startDeleteSubjects(subjects: Collection<TermSubject>) {
        subjectsRepository.actionDeleteSubjects(subjects.toList())
    }

    fun startChangeSubjectColor(subjectId:String,color:Int) {
        subjectsRepository.actionChangeSubjectColor(subjectId,color)
    }
    fun exportToIcs() {
        timetableLiveData.value?.let {
            exportController.value = it
        }

    }


}