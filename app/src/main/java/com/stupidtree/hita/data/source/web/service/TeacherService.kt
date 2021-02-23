package com.stupidtree.hita.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.search.teacher.TeacherSearched
import java.util.*

interface TeacherService {

    /**
     * 获取教师信息
     */
    fun getTeacherProfile(
        teacherId: String,
        teacherUrl: String
    ): LiveData<DataState<Map<String, String>>>


    /**
     * 获取教师介绍页信息
     */
    fun getTeacherPages(
        teacherId: String
    ): LiveData<DataState<Map<String, String>>>


    /**
     * 搜索教师
     */
    fun searchTeachers(text:String):LiveData<DataState<List<TeacherSearched>>>
}