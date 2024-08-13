package com.stupidtree.hitax.ui.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.repository.TeacherInfoRepository

class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * 仓库区
     */
    private val teacherInfoRepository = TeacherInfoRepository.getInstance(application)

    /**
     * 数据区
     */
    val teacherKeyLiveData = MutableLiveData<TeacherKey>()

    val teacherProfileLiveData: LiveData<DataState<Map<String, String>>> = teacherKeyLiveData.switchMap{
            return@switchMap teacherInfoRepository.getTeacherProfile(it.id, it.url)
        }

    val teacherPagesLiveData:LiveData<DataState<Map<String,String>>> = teacherKeyLiveData.switchMap{
        return@switchMap teacherInfoRepository.getTeacherPages(it.id)
    }


    /**
     * 方法
     */
    fun startRefresh(teacherId:String,teacherUrl:String,teacherName:String?){
        val teacherKey = TeacherKey()
        teacherKey.id = teacherId
        teacherKey.name = teacherName?:""
        teacherKey.url = teacherUrl
        teacherKeyLiveData.value = teacherKey
    }
}
