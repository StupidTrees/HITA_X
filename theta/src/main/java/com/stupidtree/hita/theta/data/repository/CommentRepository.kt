package com.stupidtree.hita.theta.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource
import com.stupidtree.hita.theta.data.source.web.CommentWebSource

class CommentRepository(application: Application) {


    fun postComment(
        token: String,
        content: String,
        articleId: String,
        toUserId: String,
        toCommentId: String?,
        contextId:String?
    ): LiveData<DataState<Boolean>> {
        return CommentWebSource.postComment(
            token,
            content, toUserId, articleId, toCommentId,contextId
        )
    }

    fun getCommentsOfArticle(
        token: String,
        articleId: String,
        pageSize: Int,
        pageNum:Int
    ): LiveData<DataState<List<Comment>>> {
        return CommentWebSource.getCommentsOfArticle(
            token, articleId,pageSize,pageNum
        )
    }

    fun getCommentsOfComment(
        token: String,
        commentId: String,
        pageSize: Int,
        pageNum:Int
    ): LiveData<DataState<List<Comment>>> {
        return CommentWebSource.getCommentsOfComment(
            token, commentId,pageSize,pageNum
        )
    }

    fun getCommentInfo(
        token: String,
        commentId: String
    ): LiveData<DataState<Comment>> {
        return CommentWebSource.getCommentInfo(
            token, commentId
        )
    }
    fun likeOrUnlikeLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<LikeResult>> {
        return CommentWebSource.likeOrUnlikeLive(token, articleId, like)
    }
    companion object {
        @JvmStatic
        var instance: CommentRepository? = null
            private set

        fun getInstance(application: Application): CommentRepository {
            if (instance == null) {
                instance = CommentRepository(application)
            }
            return instance!!
        }

    }

}