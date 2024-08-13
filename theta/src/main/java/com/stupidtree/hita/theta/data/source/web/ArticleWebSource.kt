package com.stupidtree.hita.theta.data.source.web

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.model.StarResult
import com.stupidtree.hita.theta.data.model.VoteResult
import com.stupidtree.hita.theta.data.source.web.service.ArticleService
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.util.HttpUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


class ArticleWebSource(context: Context) : BaseWebSource<ArticleService>(
    context
) {
    override fun getServiceClass(): Class<ArticleService> {
        return ArticleService::class.java
    }

    fun postArticle(
        token: String,
        content: String,
        repostId: String?,
        topicId: String?,
        filePaths: List<String>,
        asAttitude: Boolean,
        anonymous: Boolean
    ): LiveData<DataState<Boolean>> {
        val params: HashMap<String, RequestBody> = HashMap()
        for (f in filePaths) {
            val file = File(f)
            params["files\"; filename=\"" + file.name + ""] =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        }
        return service.postArticle(
            params,
            HttpUtils.getHeaderAuth(token),
            content,
            repostId,
            topicId,
            asAttitude,
            anonymous
        ).map { input ->
            when (input.code) {
                SUCCESS -> return@map DataState(DataState.STATE.SUCCESS)
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    fun postArticle(
        token: String,
        content: String,
        repostId: String?,
        topicId: String?,
        asAttitude: Boolean,
        anonymous: Boolean
    ): LiveData<DataState<Boolean>> {
        return service.postArticle(
            HttpUtils.getHeaderAuth(token), content, repostId, topicId, asAttitude, anonymous
        ).map { input ->
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

    fun getArticleInfo(
        token: String,
        articleId: String?,
        digOrigin: Boolean = false
    ): LiveData<DataState<Article>> {
        return service.getArticleInfo(
            HttpUtils.getHeaderAuth(token), articleId, digOrigin
        ).map { input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: Article())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    fun getArticleInfoCall(
        token: String,
        articleId: String?,
        digOrigin: Boolean = false
    ): Call<ApiResponse<Article>> {
        return service.getArticleInfoCall(HttpUtils.getHeaderAuth(token), articleId, digOrigin)
    }

    fun getArticles(
        token: String,
        mode: String,
        beforeTime: Long,
        afterTime: Long,
        pageSize: Int,
        extra: String
    ): LiveData<DataState<List<Article>>> {
        return service.getArticles(
            HttpUtils.getHeaderAuth(token), mode, beforeTime, afterTime, pageSize, extra
        ).map { input ->
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


    fun likeOrUnlike(
        token: String,
        articleId: String,
        like: Boolean
    ): Call<ApiResponse<LikeResult>> {
        return service.likeOrUnlike(
            HttpUtils.getHeaderAuth(token), articleId, like
        )
    }


    fun delete(
        token: String,
        articleId: String
    ): LiveData<DataState<Any>> {
        return service.delete(
            HttpUtils.getHeaderAuth(token), articleId
        ).map { input ->
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

    fun likeOrUnlikeLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<LikeResult>> {
        return service.likeOrUnlikeLive(
            HttpUtils.getHeaderAuth(token), articleId, like
        ).map { input ->
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

    fun vote(
        token: String,
        articleId: String,
        up: Boolean
    ): Call<ApiResponse<VoteResult>> {
        return service.vote(
            HttpUtils.getHeaderAuth(token), articleId, up
        )
    }

    fun voteLive(
        token: String,
        articleId: String,
        up: Boolean
    ): LiveData<DataState<VoteResult>> {
        return service.voteLive(
            HttpUtils.getHeaderAuth(token), articleId, up
        ).map { input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: VoteResult())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }


    fun starOrUnstarLive(
        token: String,
        articleId: String,
        star: Boolean
    ): LiveData<DataState<StarResult>> {
        return service.starOrUnstarLive(
            HttpUtils.getHeaderAuth(token), articleId, star
        ).map { input ->
            when (input.code) {
                SUCCESS -> return@map DataState(input.data ?: StarResult())
                codes.TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(
                    DataState.STATE.FETCH_FAILED,
                    input.message
                )
            }
        }
    }

    companion object {
        var instance: ArticleWebSource? = null
        fun getInstance(context: Context): ArticleWebSource {
            synchronized(ArticleWebSource::class.java) {
                if (instance == null) {
                    instance = ArticleWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}