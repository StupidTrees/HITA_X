package com.stupidtree.hitax.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.hitax.data.source.web.TeacherWebSource
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.ui.search.teacher.TeacherSearched

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