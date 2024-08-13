package com.stupidtree.hita.theta.data.source.web

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.service.CommentService
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.util.HttpUtils
import retrofit2.Call

class CommentWebSource(context: Context) : BaseWebSource<CommentService>(
    context
) {
    override fun getServiceClass(): Class<CommentService> {
        return CommentService::class.java
    }

    fun postComment(
        token: String,
        content: String,
        toUserId: String,
        articleId: String,
        toCommentId: String?,
        contextId: String?

    ): LiveData<DataState<Boolean>> {
        return service.postComment(
                HttpUtils.getHeaderAuth(token), content, articleId, toCommentId, toUserId, contextId
            ).map{ input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(DataState.STATE.SUCCESS)
                    codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(
                        DataState.STATE.FETCH_FAILED,
                        input.message
                    )
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    fun getCommentsOfArticle(
        token: String,
        articleId: String,
        pageSize: Int,
        pageNum: Int
    ): LiveData<DataState<List<Comment>>> {
        return service.getCommentsOfArticle(
                HttpUtils.getHeaderAuth(token), articleId, pageSize, pageNum
            ).map{ input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: listOf())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    fun getCommentsOfComment(
        token: String,
        commentId: String,
        pageSize: Int,
        pageNum: Int
    ): LiveData<DataState<List<Comment>>> {
        return service.getCommentsOfComment(
                HttpUtils.getHeaderAuth(token), commentId, pageSize, pageNum
            ).map{ input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: listOf())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    fun getCommentInfo(
        token: String,
        commentId: String
    ): LiveData<DataState<Comment>> {
        return service.getCommentInfo(
                HttpUtils.getHeaderAuth(token), commentId
            ).map{ input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: Comment())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    fun likeOrUnlike(
        token: String,
        articleId: String,
        like: Boolean
    ): Call<ApiResponse<LikeResult>> {
        return service.likeOrUnlike(
            HttpUtils.getHeaderAuth(token), articleId, like
        )
    }

    fun likeOrUnlikeLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<LikeResult>> {
        return service.likeOrUnlikeLive(
                HttpUtils.getHeaderAuth(token), articleId, like
            ).map{ input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: LikeResult())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }
    fun delete(
        token: String,
        commentId: String
    ): LiveData<DataState<Any>> {
        return service.delete(
                HttpUtils.getHeaderAuth(token), commentId
            ).map{ input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: "")
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    companion object {
        var instance: CommentWebSource? = null
        fun getInstance(context: Context): CommentWebSource {
            synchronized(CommentWebSource::class.java) {
                if (instance == null) {
                    instance = CommentWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}