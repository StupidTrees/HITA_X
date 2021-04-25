package com.stupidtree.hita.theta.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.MTransformations
import com.stupidtree.hita.theta.data.repository.ArticleRepository
import com.stupidtree.hita.theta.data.repository.CommentRepository
import com.stupidtree.hita.theta.ui.comment.CommentRefreshTrigger
import com.stupidtree.hita.theta.ui.user.UserListViewModel.Companion.PAGE_SIZE
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class ArticleDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val articleRepo = ArticleRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val commentRepo = CommentRepository.getInstance(application)

    private val articleIdLiveData = MutableLiveData<String>()
    val articleLiveData = MTransformations.switchMap(articleIdLiveData) {
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap articleRepo.getArticleDetail(user.token!!, it, false)
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))

    }

    private val likeTrigger = MutableLiveData<Pair<String, Boolean>>()
    val likeResultLiveData = Transformations.switchMap(likeTrigger) {
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap Transformations.map(
                articleRepo.likeOrUnlikeLive(
                    user.token!!,
                    it.first,
                    it.second
                )
            ) { ds ->
                ds.data?.let { lr ->
                    articleLiveData.value?.data?.likeNum = lr.likeNum
                    articleLiveData.value?.data?.liked = lr.liked
                }
                return@map ds
            }
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
    }


    private val commentsTrigger = MutableLiveData<CommentRefreshTrigger>()
    val commentsLiveData = Transformations.switchMap(commentsTrigger) { pair ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap Transformations.map(
                commentRepo.getCommentsOfArticle(
                    user.token!!,
                    pair.id,
                    pair.pageSize,
                    pair.pageNum
                )
            ) {
                return@map it.setListAction(pair.action)
            }
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
    }


    private val deleteArticleTrigger = MutableLiveData<String>()
    val deleteArticleResult = Transformations.switchMap(deleteArticleTrigger) { id ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap Transformations.map(articleRepo.delete(user.token!!, id)) {
                return@map it.also {
                    it.data = id
                }
            }
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
    }

    private val deleteCommentTrigger = MutableLiveData<String>()
    val deleteCommentResult = Transformations.switchMap(deleteCommentTrigger) { id ->
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap Transformations.map(commentRepo.delete(user.token!!, id)) {
                return@map it.also {
                    it.data = id
                }
            }
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
    }

    fun startRefresh(articleId: String) {
        articleIdLiveData.value = articleId
    }

    fun like() {
        articleLiveData.value?.data?.let {
            likeTrigger.value = Pair(it.id, !it.liked)
        }
    }

    fun deleteArticle() {
        deleteArticleTrigger.value = articleIdLiveData.value
    }

    fun deleteComment(commentId: String) {
        deleteCommentTrigger.value = commentId
    }

    fun refreshAll(articleId: String) {
        commentsTrigger.value = CommentRefreshTrigger().also {
            it.pageSize = PAGE_SIZE
            it.action = DataState.LIST_ACTION.REPLACE_ALL
            it.pageNum = 1
            it.id = articleId
        }
    }

    fun loadMore(articleId: String) {
        commentsTrigger.value = CommentRefreshTrigger().also {
            it.pageSize = PAGE_SIZE
            it.action = DataState.LIST_ACTION.APPEND
            it.pageNum = (commentsTrigger.value?.pageNum ?: 1) + 1
            it.id = articleId
        }
    }
}