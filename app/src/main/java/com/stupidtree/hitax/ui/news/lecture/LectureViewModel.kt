package com.stupidtree.hitax.ui.news.lecture

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.repository.AdditionalRepository

class LectureViewModel(application: Application) : AndroidViewModel(application) {

    val pageSize = 10
    val additionalRepo = AdditionalRepository.getInstance(application)

    val pageOffset = MutableLiveData<Pair<Int, Boolean>>()
    val listData = pageOffset.switchMap{ trigger ->
        return@switchMap additionalRepo.getLectures(pageSize, trigger.first).map{
            if (trigger.second) { //loadMore
                it.listAction = DataState.LIST_ACTION.APPEND
            } else {
                it.listAction = DataState.LIST_ACTION.REPLACE_ALL
            }
            return@map it
        }
    }


    fun refresh() {
        pageOffset.value = Pair(0, false)
    }

    fun loadMore() {
        pageOffset.value = Pair((pageOffset.value?.first ?: 0) + pageSize, true)
    }
}