package com.stupidtree.hita.ui.main.timetable.inner

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
    var dataHashCode:Int = 0

    val startDateLiveDate:MutableLiveData<Long> = MutableLiveData()
    val eventsOfThisWeek:LiveData<List<EventItem>> = Transformations.switchMap(startDateLiveDate){
        val to = it + 1000*60*60*24*7
        return@switchMap timetableRepository.getEventsDuring(it,to)
    }

    fun setStartDate(date:Long){

        if(startDateLiveDate.value != date){
            startDateLiveDate.value = date
        }
    }
}