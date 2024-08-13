package com.stupidtree.hitax.ui.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TimetableRepository

class EventItemViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val subjectRepository = SubjectRepository.getInstance(application)
    private val timetableRepository =TimetableRepository.getInstance(application)

    /**
     * 数据区
     */
    val eventItemLiveData: MutableLiveData<EventItem> = MutableLiveData()

    val progressLiveData: LiveData<Pair<Int, Int>> = eventItemLiveData.switchMap {
        return@switchMap subjectRepository.getProgressOfSubject(it.subjectId, it.from.time)
    }


    fun delete(){
        eventItemLiveData.value?.let {
            timetableRepository.actionDeleteEvents(listOf(it))
        }

    }
}