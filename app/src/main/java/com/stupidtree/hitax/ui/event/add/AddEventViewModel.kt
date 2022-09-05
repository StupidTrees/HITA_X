package com.stupidtree.hitax.ui.event.add

import android.app.Application
import android.media.metrics.Event
import androidx.lifecycle.*
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.hitax.data.repository.SubjectRepository
import com.stupidtree.hitax.data.repository.TimetableRepository
import java.sql.Timestamp
import java.time.DayOfWeek
import java.util.*

class AddEventViewModel(application: Application) : AndroidViewModel(application) {
    val eventRepo = TimetableRepository.getInstance(application)
    val subjectRepo = SubjectRepository.getInstance(application)
    val timetableLiveData = MutableLiveData<DataState<Timetable>>()
    val subjectLiveData = MediatorLiveData<DataState<TermSubject>>()
    val timeRangeLiveDate = MediatorLiveData<DataState<CourseTime>>()
    val nameLiveData = MediatorLiveData<String?>()

    val locationLiveData = MediatorLiveData<DataState<String>>()
    val teacherLiveData = MediatorLiveData<DataState<String>>()

    val doneLiveData = MediatorLiveData<Boolean>()

    var addSubject: Boolean = false

    init {
        doneLiveData.addSource(subjectLiveData) {
            checkDone()
        }
        doneLiveData.addSource(nameLiveData) {
            checkDone()
        }
        doneLiveData.addSource(timetableLiveData) {
            checkDone()
        }
        doneLiveData.addSource(timeRangeLiveDate) {
            checkDone()
        }
        subjectLiveData.addSource(timetableLiveData) {
            if (it.state == DataState.STATE.SUCCESS) {
                if (addSubject) {
                    subjectLiveData.value = DataState(DataState.STATE.SPECIAL)
                } else if (initSubject != null) {
                    subjectLiveData.value = DataState(initSubject!!)
                    initSubject = null
                } else {
                    subjectLiveData.value = DataState(DataState.STATE.NOTHING)
                }

            } else {
                subjectLiveData.value = DataState(DataState.STATE.FETCH_FAILED)
            }
        }
        timeRangeLiveDate.addSource(subjectLiveData) {
            if (it.state == DataState.STATE.SUCCESS || it.state == DataState.STATE.SPECIAL) {
                if (timeRangeLiveDate.value?.state != DataState.STATE.SUCCESS) timeRangeLiveDate.value =
                    DataState(DataState.STATE.NOTHING)
            } else {
                timeRangeLiveDate.value = DataState(DataState.STATE.FETCH_FAILED)
            }
        }
        teacherLiveData.addSource(timeRangeLiveDate) {
            if (it.state == DataState.STATE.SUCCESS) {
                if (teacherLiveData.value?.state != DataState.STATE.SUCCESS) teacherLiveData.value =
                    DataState(DataState.STATE.NOTHING)
            } else {
                teacherLiveData.value = DataState(DataState.STATE.FETCH_FAILED)
            }
        }
        locationLiveData.addSource(timeRangeLiveDate) {
            if (it.state == DataState.STATE.SUCCESS) {
                if (locationLiveData.value?.state != DataState.STATE.SUCCESS) locationLiveData.value =
                    DataState(DataState.STATE.NOTHING)
            } else {
                locationLiveData.value = DataState(DataState.STATE.FETCH_FAILED)
            }
        }
    }

    private fun checkDone() {
        val boo = timetableLiveData.value?.state == DataState.STATE.SUCCESS
                && (subjectLiveData.value?.state == DataState.STATE.SUCCESS || subjectLiveData.value?.state == DataState.STATE.SPECIAL)
                && timeRangeLiveDate.value?.state == DataState.STATE.SUCCESS
                && !nameLiveData.value.isNullOrEmpty()
        doneLiveData.value = boo
    }

    var initSubject: TermSubject? = null
    fun init(addSubject: Boolean, timetable: Timetable?, subject: TermSubject?) {
        if (timetable == null) {
            timetableLiveData.value = DataState(DataState.STATE.NOTHING)
        } else {
            timetableLiveData.value = DataState(timetable)
        }
        initSubject = subject
        this.addSubject = addSubject
    }


    fun createEvent() {
        var maxEndTime: Long = 0
        val data = mutableListOf<EventItem>()

        timetableLiveData.value?.data?.let { timetable ->
            var subject: TermSubject? = null
            if (addSubject) {
                subject = TermSubject()
                subject.name = nameLiveData.value ?: ""
                subject.timetableId = timetable.id
                if (addSubject) subjectRepo.actionSaveSubjectInfo(subject)
            } else {
                subject = subjectLiveData.value?.data
            }
            timeRangeLiveDate.value?.data?.let { range ->
                subject?.let {
                    for (w in range.weeks) {
                        val ei = EventItem()
                        ei.type = EventItem.TYPE.CLASS
                        ei.name = nameLiveData.value ?: ""
                        ei.timetableId = timetable.id
                        ei.subjectId = subject.id
                        ei.place = locationLiveData.value?.data ?: ""
                        ei.teacher = teacherLiveData.value?.data ?: ""
                        ei.fromNumber = range.begin
                        ei.lastNumber = range.end - range.begin + 1
                        val se = timetable.getTimestamps(w, range.dow, range.begin, range.end)
                        ei.from = Timestamp(se[0])
                        ei.to = Timestamp(se[1])
                        maxEndTime = maxEndTime.coerceAtLeast(ei.to.time)
                        data.add(ei)
                    }
                }
            }
        }

        eventRepo.actionAddEvents(data)
        timetableLiveData.value?.data?.let { timetable ->
            if (maxEndTime > timetable.endTime.time) {
                val c = Calendar.getInstance()
                c.timeInMillis = maxEndTime
                c.firstDayOfWeek = Calendar.MONDAY
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                c.set(Calendar.HOUR_OF_DAY, 23)
                c.set(Calendar.MINUTE, 59)
                timetable.endTime.time = c.timeInMillis
                eventRepo.actionSaveTimetable(timetable)
            }
        }

    }
}