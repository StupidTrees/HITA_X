package com.stupidtree.hita.theta.data.source.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.LiveDataCallAdapter
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.service.ArticleService
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.util.HttpUtils
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ArticleWebSource : BaseWebSource<ArticleService>(
    Retrofit.Builder()
        .addCallAdapterFactory(LiveDataCallAdapter.LiveDataCallAdapterFactory.INSTANCE)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://hita.store:39999").build()
) {
    override fun getServiceClass(): Class<ArticleService> {
        return ArticleService::class.java
    }

    fun postArticle(
        token: String,
        content: String,
        repostId: String?
    ): LiveData<DataState<Boolean>> {
        return Transformations.map(
            service.postArticle(
                HttpUtils.getHeaderAuth(token), content, repostId
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

    fun getArticleInfo(
        token: String,
        articleId: String?,
        digOrigin: Boolean = false
    ): LiveData<DataState<Article>> {
        return Transformations.map(
            service.getArticleInfo(
                HttpUtils.getHeaderAuth(token), articleId, digOrigin
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState(input.data ?: Article())
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
        extra:String
    ): LiveData<DataState<List<Article>>> {
        return Transformations.map(
            service.getArticles(
                HttpUtils.getHeaderAuth(token), mode, beforeTime, afterTime, pageSize,extra
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
            service.likeOrUnlikeLive(
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