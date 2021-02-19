package com.stupidtree.hita.ui.timetable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.repository.TimetableRepository

class TimetableManagerViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)


    val currentTimetableLiveData:MutableLiveData<Timetable?> = MutableLiveData()

    val timetablesLiveData:LiveData<List<Timetable>> = timetableRepository.getTimetables()
}