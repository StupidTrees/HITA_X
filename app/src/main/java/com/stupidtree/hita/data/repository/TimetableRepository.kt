package com.stupidtree.hita.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.ui.base.DataState
import java.util.*

class TimetableRepository(application: Application) {
    private val eventItemDao = AppDatabase.getDatabase(application).eventItemDao()

    /**
     * 获取[from,to)内的事件
     */
    fun getEventsDuring(from:Long,to:Long): LiveData<List<EventItem>> {
        Log.e("from-to", "$from,$to")
        return eventItemDao.getEventsDuring(from,to)
    }

    companion object {
        private var instance: TimetableRepository? = null
        fun getInstance(application: Application): TimetableRepository {
            if (instance == null) instance = TimetableRepository(application)
            return instance!!
        }
    }
}