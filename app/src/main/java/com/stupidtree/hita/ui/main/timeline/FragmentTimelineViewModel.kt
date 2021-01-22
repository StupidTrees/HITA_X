package com.stupidtree.hita.ui.main.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.Trigger
import java.util.*

class FragmentTimelineViewModel(application: Application) : AndroidViewModel(application){

    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val todayEventsController:MutableLiveData<Trigger> = MutableLiveData()
    val todayEventsLiveData:LiveData<List<EventItem>> = Transformations.switchMap(todayEventsController){
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY,0)
        now.set(Calendar.MINUTE,0)
        val from = now.timeInMillis
        now.add(Calendar.DATE,1)
        val to = now.timeInMillis
        return@switchMap timetableRepository.getEventsDuring(from,to)
    }


    fun startRefresh(){
        todayEventsController.value = Trigger.actioning
    }
}