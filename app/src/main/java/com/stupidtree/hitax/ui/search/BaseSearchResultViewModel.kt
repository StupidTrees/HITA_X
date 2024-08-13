package com.stupidtree.hitax.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState

abstract class BaseSearchResultViewModel<T>(application: Application) :
    AndroidViewModel(application) {


    private val searchTriggerLiveData: MutableLiveData<SearchTrigger> = MutableLiveData()
    val searchResultLiveData: LiveData<DataState<List<T>>> = searchTriggerLiveData.switchMap {
            return@switchMap doSearch(it)
        }


    protected abstract fun doSearch(trigger: SearchTrigger): LiveData<DataState<List<T>>>

    fun startSearch(text: String, pageSize: Int, pageNum: Int, append: Boolean) {
        searchTriggerLiveData.value = SearchTrigger.getActioning(text, pageSize, pageNum, append)
    }

    fun changeSearchText(text: String): Boolean {
        val old = searchTriggerLiveData.value
        if (text != old?.text && text.isNotBlank()) {
            searchTriggerLiveData.value =
                SearchTrigger.getActioning(text, old?.pageSize?:0, 0, false)
            return true
        }
        return false

    }
}