package com.stupidtree.hita.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hita.R
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.main.timetable.outer.TimeTablePagerAdapter.Companion.WEEK_MILLS
import java.lang.NumberFormatException
import java.sql.Time
import java.util.*

class TimetableRepository(val application: Application) {
    private val eventItemDao = AppDatabase.getDatabase(application).eventItemDao()
    private val timetableDao = AppDatabase.getDatabase(application).timetableDao()
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()

    /**
     * 获取[from,to)内的事件
     */
    fun getEventsDuring(from: Long, to: Long): LiveData<List<EventItem>> {
        return eventItemDao.getEventsDuring(from, to)
    }

    /**
     * 获取[from,to)内的事件，包含颜色
     */
    fun getEventsDuringWithColor(from: Long, to: Long): LiveData<List<EventItem>> {
        val res = MediatorLiveData<List<EventItem>>()
        res.addSource(eventItemDao.getEventsDuring(from, to)) { events ->
            val subjects = mutableSetOf<String>()
            for (e in events) {
                if (e.subjectId.isNotBlank()) {
                    subjects.add(e.subjectId)
                }
            }
            res.addSource(subjectDao.getSubjectColorsWithId(subjects)) { colors ->
                val map = mutableMapOf<String, Int>()
                for (color in colors) {
                    map[color.id] = color.color
                }
                for (e in events) {
                    map[e.subjectId]?.let {
                        e.color = it
                    }
                }
                res.value = events
            }
        }
        return res
    }

    fun getClassesOfSubject(subjectId: String): LiveData<List<EventItem>> {
        return eventItemDao.getClassesOfSubject(subjectId)
    }

    /**
     * 获取所有课表
     */
    fun getTimetables(): LiveData<List<Timetable>> {
        return timetableDao.getTimetables()
    }

    fun getTimetablesById(id: String): LiveData<Timetable> {
        return timetableDao.getTimetableById(id)
    }

    fun actionDeleteTimetables(timetables: List<Timetable>) {
        val ids = mutableListOf<String>()
        for (tt in timetables) {
            ids.add(tt.id)
        }
        Thread {
            timetableDao.deleteTimetablesSync(timetables)
            eventItemDao.deleteEventsFromTimetablesSync(ids)
            subjectDao.deleteSubjectsFromTimetablesSync(ids)
        }.start()
    }

    fun actionNewTimetable() {
        Thread {
            val defaultTables =
                timetableDao.getTimetableNamesWithDefaultSync(application.getString(R.string.default_timetable_name) + "%")
            var max = 0
            for (tt in defaultTables) {
                val i = try {
                    tt.replace(application.getString(R.string.default_timetable_name), "").toInt()
                } catch (e: NumberFormatException) {
                    null
                }
                if (i != null && i > max) {
                    max = i
                }
            }
            val newTable = Timetable()
            newTable.startTime.time = System.currentTimeMillis()
            newTable.endTime.time = System.currentTimeMillis()+WEEK_MILLS
            newTable.name =
                application.getString(R.string.default_timetable_name) + (max + 1).toString()
            timetableDao.saveTimetable(newTable)
        }.start()
    }

    fun actionSaveTimetable(timetable: Timetable) {
        Thread {
            timetableDao.saveTimetable(timetable)
        }.start()
    }

    fun actionChangeTimetableStructure(timetable: Timetable, tp: TimePeriodInDay, position: Int) {
        timetable.setScheduleStructure(tp, position)
        Thread {
            timetableDao.saveTimetable(timetable)
            val fromToChange = eventItemDao.getClassAtFromNumberSync(timetable.id, position + 1)
            val tmp = Calendar.getInstance()
            for (e in fromToChange) {
                tmp.timeInMillis = e.from.time
                tmp.set(Calendar.HOUR_OF_DAY, tp.from.hour)
                tmp.set(Calendar.MINUTE, tp.from.minute)
                e.from.time = tmp.timeInMillis
            }
            eventItemDao.saveEvents(fromToChange)

            val endToChange = eventItemDao.getClassAtToNumberSync(timetable.id, position + 1)
            for (e in endToChange) {
                tmp.timeInMillis = e.to.time
                tmp.set(Calendar.HOUR_OF_DAY, tp.to.hour)
                tmp.set(Calendar.MINUTE, tp.to.minute)
                e.to.time = tmp.timeInMillis
            }
            eventItemDao.saveEvents(endToChange)

        }.start()
    }


    companion object {
        private var instance: TimetableRepository? = null
        fun getInstance(application: Application): TimetableRepository {
            if (instance == null) instance = TimetableRepository(application)
            return instance!!
        }
    }
}