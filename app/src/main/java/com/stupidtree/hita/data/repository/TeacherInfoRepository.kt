package com.stupidtree.hita.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.EventItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.model.timetable.Timetable
import com.stupidtree.hita.data.source.preference.EasPreferenceSource
import com.stupidtree.hita.data.source.web.TeacherWebSource
import com.stupidtree.hita.data.source.web.eas.EASource
import com.stupidtree.hita.data.source.web.service.EASService
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.search.teacher.TeacherSearched
import com.stupidtree.hita.utils.LiveDataUtils
import com.stupidtree.hita.utils.TimeTools.getDateAtWOT
import java.sql.Timestamp
import java.util.*

class TeacherInfoRepository internal constructor(application: Application) {


    fun getTeacherProfile(
        teacherId: String,
        teacherUrl: String
    ): LiveData<DataState<Map<String, String>>> {
        return TeacherWebSource.getTeacherProfile(teacherId,teacherUrl)
    }

    fun getTeacherPages(
        teacherId: String
    ): LiveData<DataState<Map<String, String>>> {
        return TeacherWebSource.getTeacherPages(teacherId)
    }


    fun searchTeachers(text:String):LiveData<DataState<List<TeacherSearched>>>{
        return TeacherWebSource.searchTeachers(text)
    }
    companion object {
        @Volatile
        private var instance: TeacherInfoRepository? = null
        fun getInstance(application: Application): TeacherInfoRepository {
            synchronized(TeacherInfoRepository::class.java) {
                if (instance == null) instance = TeacherInfoRepository(application)
                return instance!!
            }
        }
    }
}