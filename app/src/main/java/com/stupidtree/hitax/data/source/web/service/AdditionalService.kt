package com.stupidtree.hitax.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.eas.*
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.ui.eas.classroom.BuildingItem
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem
import java.util.*

interface AdditionalService {


    fun getLectures(
        pageSize:Int,
        pageOffset:Int
    ):LiveData<DataState<List<Map<String,String>>>>


    fun getNewsMeta(
        link:String
    ):LiveData<DataState<Map<String,String>>>

}