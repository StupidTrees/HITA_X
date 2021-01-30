package com.stupidtree.hita.ui.main.timetable.outer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.Trigger

class TimetableViewModel(application: Application) :
    AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)

    private val timetableController = MutableLiveData<Trigger>()
    val timetableLiveData:LiveData<List<Timetable>> = Transformations.switchMap(timetableController){
        return@switchMap timetableRepository.getTimetables()
    }

    fun startRefresh(){
        timetableController.value = Trigger.actioning
    }

}