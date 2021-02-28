package com.stupidtree.hitax.ui.main.timetable.inner

import android.app.Application
import androidx.lifecycle.*
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository
import com.stupidtree.hitax.utils.MTransformations
import java.util.*

class TimetablePageViewModel(application: Application) : AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val timetableStyleRepository = TimetableStyleRepository.getInstance(application)

    var dataHashCode: Int = 0
    val startDateLiveData: MutableLiveData<Long> = MutableLiveData()
    private val eventsOfThisWeek: LiveData<List<EventItem>> = Transformations.switchMap(startDateLiveData) {
        val to = it + 1000 * 60 * 60 * 24 * 7
        return@switchMap timetableRepository.getEventsDuringWithColor(it, to)
    }
    private val timetableStyleLiveData: LiveData<TimetableStyleSheet> = timetableStyleRepository.getStyleSheetLiveData()


    val timetableViewLiveData = MTransformations.map(eventsOfThisWeek,timetableStyleLiveData){
        return@map it
    }



    fun setStartDate(date: Long, force: Boolean = false) {
        val old = startDateLiveData.value ?: 0
        if (date < old || date > old + 1000 * 60 * 60 * 24 * 7) {
            startDateLiveData.value = date
        }
    }
}