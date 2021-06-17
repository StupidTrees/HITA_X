package com.stupidtree.hitax.ui.main.timetable.outer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.MTransformations
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.ui.main.timetable.inner.TimetableStyleSheet
import com.stupidtree.hitax.ui.main.timetable.outer.TimeTablePagerAdapter.Companion.WEEK_MILLS
import com.stupidtree.hitax.ui.main.timetable.outer.TimetableFragment.Companion.WINDOW_SIZE
import com.stupidtree.hitax.utils.TimeTools
import java.util.*

class TimetableViewModel(application: Application) :
    AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val timetableStyleRepository = TimetableStyleRepository.getInstance(application)


    private val timetableController = MutableLiveData<Trigger>()
    val timetableLiveData: LiveData<List<Timetable>> = Transformations.switchMap(timetableController) {
            return@switchMap timetableRepository.getTimetables()
        }
    val startTimeLiveData: LiveData<Int> get() = timetableStyleRepository.startDateLiveData

    private val timetableStyleLiveData: LiveData<TimetableStyleSheet> =
        timetableStyleRepository.getStyleSheetLiveData()

    var startIndex = 0
    val windowStartDate: MutableLiveData<Long> = MutableLiveData()


    val eventsLiveData: MutableList<LiveData<Pair<List<EventItem>, TimetableStyleSheet>>> =
        mutableListOf()
    val eventsTriggers: MutableList<MutableLiveData<Long>> = mutableListOf()

    init {
        val windowStart = Calendar.getInstance()
        windowStart.firstDayOfWeek = Calendar.MONDAY
        windowStart[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        windowStart[Calendar.HOUR_OF_DAY] = 0
        windowStart[Calendar.MINUTE] = 0
        windowStart[Calendar.SECOND] = 0
        windowStart[Calendar.MILLISECOND] = 0
        val sd = windowStart.timeInMillis - WEEK_MILLS * (WINDOW_SIZE / 2)
        windowStartDate.value = sd
        for (i in 0 until WINDOW_SIZE) {
            val trigger = MutableLiveData(sd + i * WEEK_MILLS)
            eventsTriggers.add(trigger)
            eventsLiveData.add(Transformations.switchMap(trigger) {
                return@switchMap MTransformations.switchMap(
                    timetableRepository.getEventsDuringWithColor(
                        it,
                        it + WEEK_MILLS
                    ), timetableStyleLiveData
                ) { pair ->
                    MutableLiveData(pair)
                }
            }
            )
        }
    }


    fun startRefresh() {
        timetableController.value = Trigger.actioning
    }

    fun scrollPrev() {
        windowStartDate.value = (windowStartDate.value ?: 0) - WEEK_MILLS
        Log.e("prev",TimeTools.printDate(windowStartDate.value?:0))
        val toChange = (startIndex + WINDOW_SIZE - 1) % WINDOW_SIZE
        eventsTriggers[toChange].value = windowStartDate.value ?: 0
        startIndex = toChange
    }

    fun scrollNext() {
        windowStartDate.value = (windowStartDate.value ?: 0) + WEEK_MILLS
        Log.e("next",TimeTools.printDate(windowStartDate.value?:0))
        val toChange = startIndex % WINDOW_SIZE
        eventsTriggers[toChange].value = (windowStartDate.value ?: 0) + WEEK_MILLS * (WINDOW_SIZE - 1)
        startIndex = (startIndex + 1) % WINDOW_SIZE
    }

    fun resetAll(date: Long) {
        var newWindowStart = date - WEEK_MILLS * (WINDOW_SIZE / 2)
        windowStartDate.value = newWindowStart
        for (i in 0 until WINDOW_SIZE) {
            eventsTriggers[(i + startIndex) % WINDOW_SIZE].value = newWindowStart
            newWindowStart += WEEK_MILLS
        }
    }

}