package com.stupidtree.hitax.ui.main.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.MTransformations
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository
import com.stupidtree.hitax.ui.main.timetable.TimetableFragment.Companion.WEEK_MILLS
import com.stupidtree.hitax.ui.main.timetable.TimetableFragment.Companion.WINDOW_SIZE
import java.util.Calendar

class TimetableViewModel(application: Application) :
    AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val timetableStyleRepository = TimetableStyleRepository.getInstance(application)

    private val timetableController = MutableLiveData<Trigger>()
    val timetableLiveData: LiveData<List<Timetable>> = timetableController.switchMap{
            return@switchMap timetableRepository.getTimetables()
        }
    val startTimeLiveData: LiveData<Int>
        get() = timetableStyleRepository.startTimeLiveData

    var currentPageStartDate: MutableLiveData<Long>
    var currentIndex = 0
    var startIndex = 0

    private val timetableStyleLiveData: LiveData<TimetableStyleSheet> =
        timetableStyleRepository.getStyleSheetLiveData()
    val windowEventsData: MutableList<MediatorLiveData<Pair<List<EventItem>, TimetableStyleSheet>>> =
        mutableListOf()
    val windowStartData: MutableList<MutableLiveData<Long>> = mutableListOf()
    val windowHashesData = mutableListOf<Int>()

    init {
        val ws = Calendar.getInstance()
        ws.firstDayOfWeek = Calendar.MONDAY
        ws[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        ws[Calendar.HOUR_OF_DAY] = 0
        ws[Calendar.MINUTE] = 0
        ws[Calendar.SECOND] = 0
        ws[Calendar.MILLISECOND] = 0
        currentPageStartDate = MutableLiveData(ws.timeInMillis)
        for (i in 0 until WINDOW_SIZE) {
            windowHashesData.add(0)
            val startLD = MutableLiveData<Long>()
            windowStartData.add(startLD)
            val eventsRawData =  startLD.switchMap{
                return@switchMap timetableRepository.getEventsDuringWithColor(
                    it,
                    it + WEEK_MILLS
                )
            }
            val eventsData = MTransformations.switchMap(eventsRawData,timetableStyleLiveData){
                return@switchMap MutableLiveData(it)
            }
            windowEventsData.add(eventsData)
        }
    }

    fun startRefresh() {
        timetableController.value = Trigger.actioning
    }

    fun addStartDate(offset: Long) {
        currentPageStartDate.value?.let {
            currentPageStartDate.value = it + offset
        }
    }

}