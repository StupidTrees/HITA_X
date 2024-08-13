package com.stupidtree.hita.theta.ui.topic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.TopicRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class TopicListViewModel(application: Application) : AndroidViewModel(application) {
    private val topicRepository = TopicRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)

    private val refreshController = MutableLiveData<PageRefreshTrigger>()
    val topicsLiveData = refreshController.switchMap { pair ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap topicRepository.getTopics(
                    user.token!!,
                    pair.mode,
                    pair.pageSize,
                    pair.pageNum,
                    pair.extra

            ).map{
                return@map it.setListAction(pair.action)
            }
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    fun refreshAll(mode: String, extra: String) {
        refreshController.value = PageRefreshTrigger().also {
            it.action = DataState.LIST_ACTION.REPLACE_ALL
            it.mode = mode
            it.extra = extra
            it.pageSize = PAGE_SIZE
            it.pageNum = 1
        }
    }

    fun loadMore(mode: String, extra: String) {
        refreshController.value = PageRefreshTrigger().also {
            it.action = DataState.LIST_ACTION.APPEND
            it.mode = mode
            it.pageSize = PAGE_SIZE
            it.pageNum = (refreshController.value?.pageNum ?: 1) + 1
            it.extra = extra
        }
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}