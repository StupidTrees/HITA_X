package com.stupidtree.hita.ui.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hita.data.model.timetable.EventItem

class EventItemViewModel(application: Application) :AndroidViewModel(application){
    val eventItemLiveData:MutableLiveData<EventItem> = MutableLiveData()
}