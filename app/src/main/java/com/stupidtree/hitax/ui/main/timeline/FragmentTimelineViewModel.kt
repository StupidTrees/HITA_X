package com.stupidtree.hitax.ui.main.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.repository.TimetableRepository
import java.util.Calendar

class FragmentTimelineViewModel(application: Application) : AndroidViewModel(application){

    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)

    /**
     * 数据区
     */
    private val todayEventsController:MutableLiveData<Trigger> = MutableLiveData()
    val todayEventsLiveData:LiveData<List<EventItem>> =  todayEventsController.switchMap{
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY,0)
        now.set(Calendar.MINUTE,0)
        val from = now.timeInMillis
        now.add(Calendar.DATE,1)
        val to = now.timeInMillis
        return@switchMap timetableRepository.getEventsDuring(from,to)
    }

    val weekEventsLiveData:LiveData<List<EventItem>> = todayEventsController.switchMap{
        return@switchMap timetableRepository.getEventsAfter(System.currentTimeMillis(),4)
    }


    fun startRefresh(){
        todayEventsController.value = Trigger.actioning
    }
}