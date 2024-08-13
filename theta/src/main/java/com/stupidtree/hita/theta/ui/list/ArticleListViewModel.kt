package com.stupidtree.hita.theta.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.ArticleRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class ArticleListViewModel(application: Application) : AndroidViewModel(application) {
    private val articleRepository = ArticleRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)

    private val refreshController = MutableLiveData<PageRefreshTrigger>()
    val articlesLiveData = refreshController.switchMap{ pair ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap articleRepository.getArticles(
                    user.token!!,
                    pair.mode,
                    pair.beforeTime,
                    pair.afterTime,
                    pair.pageSize,
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
            it.beforeTime = Long.MAX_VALUE
            it.afterTime = 0
            it.mode = mode
            it.extra = extra
            it.pageSize = PAGE_SIZE
        }
    }

    fun getNew(mode: String, afterTime: Long, extra: String) {
        refreshController.value = PageRefreshTrigger().also {
            it.action = DataState.LIST_ACTION.PUSH_HEAD
            it.beforeTime = Long.MAX_VALUE
            it.afterTime = afterTime
            it.mode = mode
            it.pageSize = PAGE_SIZE
            it.extra = extra
        }
    }

    fun loadMore(mode: String, beforeTime: Long, extra: String) {
        refreshController.value = PageRefreshTrigger().also {
            it.action = DataState.LIST_ACTION.APPEND
            it.beforeTime = beforeTime
            it.afterTime = 0
            it.mode = mode
            it.pageSize = PAGE_SIZE
            it.extra = extra
        }
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}