package com.stupidtree.hita.theta.data.source.web

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.LiveDataCallAdapter
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.service.CommentService
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.util.HttpUtils
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CommentWebSource : BaseWebSource<CommentService>(
    Retrofit.Builder()
        .addCallAdapterFactory(LiveDataCallAdapter.LiveDataCallAdapterFactory.INSTANCE)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://hita.store:39999").build()
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
        contextId:String?

    ): LiveData<DataState<Boolean>> {
        return Transformations.map(
            service.postComment(
                HttpUtils.getHeaderAuth(token), content, articleId, toCommentId, toUserId,contextId
            )
        ) { input ->
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
        return Transformations.map(
            service.getCommentsOfArticle(
                HttpUtils.getHeaderAuth(token), articleId, pageSize,pageNum
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: listOf())
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

    fun getCommentsOfComment(
        token: String,
        commentId: String,
        pageSize: Int,
        pageNum:Int
    ): LiveData<DataState<List<Comment>>> {
        return Transformations.map(
            service.getCommentsOfComment(
                HttpUtils.getHeaderAuth(token), commentId,pageSize,pageNum
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: listOf())
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

    fun getCommentInfo(
        token: String,
        commentId: String
    ): LiveData<DataState<Comment>> {
        return Transformations.map(
            service.getCommentInfo(
                HttpUtils.getHeaderAuth(token), commentId
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: Comment())
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
        return Transformations.map(
            CommentWebSource.service.likeOrUnlikeLive(
                HttpUtils.getHeaderAuth(token), articleId, like
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: LikeResult())
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
}