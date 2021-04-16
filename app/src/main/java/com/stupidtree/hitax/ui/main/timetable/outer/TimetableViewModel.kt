package com.stupidtree.hitax.ui.main.timetable.outer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.TimetableRepository
import com.stupidtree.hitax.data.repository.TimetableStyleRepository
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.ui.main.timetable.outer.TimeTablePagerAdapter.Companion.WEEK_MILLS
import com.stupidtree.hitax.ui.main.timetable.outer.TimetableFragment.Companion.WINDOW_SIZE
import java.util.*

class TimetableViewModel(application: Application) :
        AndroidViewModel(application) {
    private val timetableRepository = TimetableRepository.getInstance(application)
    private val timetableStyleRepository = TimetableStyleRepository.getInstance(application)

    private val timetableController = MutableLiveData<Trigger>()
    val timetableLiveData: LiveData<List<Timetable>> = Transformations.switchMap(timetableController) {
        return@switchMap timetableRepository.getTimetables()
    }
    val startDateLiveData: LiveData<Int>
        get() = timetableStyleRepository.startDateLiveData


    val currentPageStartDate: MutableLiveData<Long> = MutableLiveData()


    fun startRefresh() {
        timetableController.value = Trigger.actioning
    }

    fun addStartDate(offset: Long) {
        currentPageStartDate.value?.let {
            currentPageStartDate.value = it + offset
        }
    }

    fun initWindow(): Long {
        val windowStart = Calendar.getInstance()
        windowStart.firstDayOfWeek = Calendar.MONDAY
        windowStart[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        windowStart[Calendar.HOUR_OF_DAY] = 0
        windowStart[Calendar.MINUTE] = 0
        windowStart[Calendar.SECOND] = 0
        windowStart[Calendar.MILLISECOND] = 0
        currentPageStartDate.value = windowStart.timeInMillis
        return windowStart.timeInMillis - WEEK_MILLS * (WINDOW_SIZE / 2)
    }

}