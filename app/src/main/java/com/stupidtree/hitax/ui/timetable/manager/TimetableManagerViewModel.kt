package com.stupidtree.hitax.ui.timetable.manager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository

class TimetableManagerViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)
    val timetablesLiveData:LiveData<List<Timetable>> = timetableRepository.getTimetables()



    fun startDeleteTimetables(timetables:List<Timetable>){
        timetableRepository.actionDeleteTimetables(timetables)
    }

    fun startNewTimetable(){
        timetableRepository.actionNewTimetable()
    }
}