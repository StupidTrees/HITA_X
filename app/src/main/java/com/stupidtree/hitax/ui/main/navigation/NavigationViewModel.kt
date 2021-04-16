package com.stupidtree.hitax.ui.main.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.component.data.Trigger

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val timetableRepository = TimetableRepository.getInstance(application)

    private val recentTimetableController = MutableLiveData<Trigger>()
    val recentTimetableLiveData: LiveData<Timetable?> =
        Transformations.switchMap(recentTimetableController) {
            return@switchMap timetableRepository.getRecentTimetable()
        }
    val timetableCountLiveData: LiveData<Int> =
        Transformations.switchMap(recentTimetableController) {
            return@switchMap timetableRepository.getTimetableCount()
        }
    fun startRefresh(){
        recentTimetableController.value = Trigger.actioning
    }
}