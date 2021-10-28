package com.stupidtree.hita.theta.ui.comment.reply

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.MTransformations
import com.stupidtree.hita.theta.data.repository.CommentRepository
import com.stupidtree.hita.theta.ui.comment.CommentRefreshTrigger
import com.stupidtree.hita.theta.ui.list.ArticleListViewModel.Companion.PAGE_SIZE
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class CommentReplyViewModel(application: Application) : AndroidViewModel(application) {
    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val commentRepo = CommentRepository.getInstance(application)

    private val commentIdLiveData = MutableLiveData<String>()
    val commentLiveData = MTransformations.switchMap(commentIdLiveData) {
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap commentRepo.getCommentInfo(user.token!!, it)
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))

    }

    private val likeTrigger = MutableLiveData<Pair<String, Boolean>>()
    val likeResultLiveData = Transformations.switchMap(likeTrigger) {
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap Transformations.map(
                commentRepo.likeOrUnlikeLive(
                    user.token!!,
                    it.first,
                    it.second
                )
            ) { ds ->
                ds.data?.let { lr ->
                    commentLiveData.value?.data?.likeNum = lr.likeNum
                    commentLiveData.value?.data?.liked = lr.liked
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
                commentRepo.getCommentsOfComment(
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
        commentIdLiveData.value = articleId
    }

    fun like() {
        commentLiveData.value?.data?.let {
            likeTrigger.value = Pair(it.id, !it.liked)
        }
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
            it.pageNum = (commentsTrigger.value?.pageNum?:1)+1
            it.id = articleId
        }
    }

    fun deleteComment(commentId: String) {
        deleteCommentTrigger.value = commentId
    }

}