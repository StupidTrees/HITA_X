package com.stupidtree.hita.theta.ui.comment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.CommentRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class CreateCommentViewModel(application: Application) : AndroidViewModel(application) {

    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val commentRepository = CommentRepository.getInstance(application)
    private val createCommentController = MutableLiveData<CreateCommentRequest>()

    val createCommentResult = createCommentController.switchMap{
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap commentRepository.postComment(
                user.token!!,
                it.content,
                it.toArticleId,
                it.toUserId,
                it.toCommentId,
                it.contextId
            )
        }
        return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))

    }


    fun postComment(content:String,toUserId:String,toArticleId:String,contextId:String?,toCommentId:String?){
        createCommentController.value = CreateCommentRequest().also {
            it.content = content
            it.toUserId = toUserId
            it.contextId = contextId
            it.toArticleId = toArticleId
            it.toCommentId = toCommentId
        }
    }
}