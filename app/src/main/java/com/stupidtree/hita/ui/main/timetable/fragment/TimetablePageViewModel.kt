package com.stupidtree.hita.ui.main.timetable.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.repository.TimetableRepository
import java.util.*

class TimetablePageViewModel(application: Application) : AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)

    val startDateLiveDate:MutableLiveData<Long> = MutableLiveData()
    val eventsOfThisWeek:LiveData<List<EventItem>> = Transformations.switchMap(startDateLiveDate){
        val from = Calendar.getInstance()
        val to = Calendar.getInstance()
        from.timeInMillis = it
        to.timeInMillis = it
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

    fun setStartDate(date:Long){
        startDateLiveDate.value = date
    }
}