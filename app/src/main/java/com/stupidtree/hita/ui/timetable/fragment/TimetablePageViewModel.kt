package com.stupidtree.hita.ui.timetable.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.Trigger
import java.util.*

class TimetablePageViewModel(application: Application) : AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)

    private val eventsController:MutableLiveData<Long> = MutableLiveData()
    val eventsOfThisWeek:LiveData<List<EventItem>> = Transformations.switchMap(eventsController){
        val from = Calendar.getInstance()
        val to = Calendar.getInstance()
        from.firstDayOfWeek = Calendar.MONDAY
        to.firstDayOfWeek = Calendar.MONDAY
        from.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
        from.set(Calendar.HOUR_OF_DAY,0)
        from.set(Calendar.MINUTE,0)
        to.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)
        to.set(Calendar.HOUR_OF_DAY,24)
        to.set(Calendar.MINUTE,0)
        return@switchMap timetableRepository.getEventsDuring(from.timeInMillis,to.timeInMillis)
    }

    fun startRefresh(date:Long){
        eventsController.value = date
    }
}