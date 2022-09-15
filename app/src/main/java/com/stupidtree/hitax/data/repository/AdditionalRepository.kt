package com.stupidtree.hitax.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.source.web.StaticWebSource
import com.stupidtree.hitax.data.source.web.additional.AdditionalSource
import com.stupidtree.hitax.data.source.web.service.AdditionalService

class AdditionalRepository internal constructor(application: Application) {
    private val additionalWebSource:AdditionalService = AdditionalSource()

    fun getLectures(pageSize:Int,pageOffset:Int): LiveData<DataState<List<Map<String,String>>>> {
        return additionalWebSource.getLectures(pageSize,pageOffset)
    }

    fun getNewsMeta(link:String): LiveData<DataState<Map<String,String>>> {
        return additionalWebSource.getNewsMeta(link)
    }


    companion object {
        @Volatile
        private var instance: AdditionalRepository? = null
        fun getInstance(application: Application): AdditionalRepository {
            synchronized(AdditionalRepository::class.java) {
                if (instance == null) instance = AdditionalRepository(application)
                return instance!!
            }
        }
    }
}