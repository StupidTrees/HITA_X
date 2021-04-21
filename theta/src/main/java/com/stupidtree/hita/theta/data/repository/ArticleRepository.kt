package com.stupidtree.hita.theta.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource

class ArticleRepository(application: Application) {


    fun postArticle(
        token: String,
        content: String,
        repostId: String?
    ): LiveData<DataState<Boolean>> {
        return ArticleWebSource.postArticle(
            token,
            content,
            repostId
        )
    }

    fun getArticles(
        token: String,
        mode: String,
        beforeTime: Long,
        afterTime: Long,
        pageSize: Int,
        extra:String
    ): LiveData<DataState<List<Article>>> {
        return ArticleWebSource.getArticles(token, mode, beforeTime, afterTime, pageSize,extra)
    }


    fun getArticleDetail(
        token: String,
        articleId: String,
        digOrigin: Boolean
    ): LiveData<DataState<Article>> {
        return ArticleWebSource.getArticleInfo(token, articleId, digOrigin)
    }

    fun likeOrUnlikeLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<LikeResult>> {
        return ArticleWebSource.likeOrUnlikeLive(token, articleId, like)
    }

    companion object {
        @JvmStatic
        var instance: ArticleRepository? = null
            private set

        fun getInstance(application: Application): ArticleRepository {
            if (instance == null) {
                instance = ArticleRepository(application)
            }
            return instance!!
        }

    }

}