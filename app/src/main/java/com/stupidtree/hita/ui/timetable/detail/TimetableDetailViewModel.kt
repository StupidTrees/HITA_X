package com.stupidtree.hita.ui.timetable.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.repository.TimetableRepository
import com.stupidtree.hita.ui.base.StringTrigger

class TimetableDetailViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val timetableRepository = TimetableRepository.getInstance(application)


    private val timetableController: MutableLiveData<StringTrigger> = MutableLiveData()
    val timetableLiveData: LiveData<Timetable> = Transformations.switchMap(timetableController) {
        return@switchMap timetableRepository.getTimetablesById(it.data)
    }


    /**
     * 方法
     */
    fun startRefresh(id:String){
        timetableController.value = StringTrigger.getActioning(id)
    }
}