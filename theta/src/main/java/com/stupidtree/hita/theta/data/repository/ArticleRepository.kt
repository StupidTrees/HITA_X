package com.stupidtree.hita.theta.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.model.StarResult
import com.stupidtree.hita.theta.data.model.VoteResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class ArticleRepository(val application: Application) {
    val articleWebSource = ArticleWebSource.getInstance(application)

    fun postArticle(
        token: String,
        content: String,
        repostId: String?,
        topicId: String?,
        asAttitude: Boolean,
        anonymous:Boolean
    ): LiveData<DataState<Boolean>> {
        return articleWebSource.postArticle(
            token,
            content,
            repostId,
            topicId, asAttitude,anonymous
        )
    }

    fun postArticle(
        token: String,
        content: String,
        urls: List<String>,
        repostId: String?,
        topicId: String?,
        asAttitude: Boolean,
        anonymous:Boolean
    ): LiveData<DataState<Boolean>> {
        val result = MediatorLiveData<DataState<Boolean>>()
        val urlCompressed = mutableListOf<String>()
        Luban.with(application)
            .setTargetDir(application.getExternalFilesDir("image")!!.absolutePath)
            .load(urls)
            .setCompressListener(object : OnCompressListener {
                //设置回调
                override fun onStart() {
                    Log.e("luban", "开始压缩")
                }

                override fun onSuccess(file: File) {
                    Log.e("luban", "压缩成功")
//                    val requestFile =
//                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
//                    //构造一个图片格式的POST表单
                    urlCompressed.add(file.path)
                    if (urlCompressed.size == urls.size) {
                        Log.e("urls", urlCompressed.toString())
                        result.addSource(
                            articleWebSource.postArticle(
                                token,
                                content,
                                repostId,
                                topicId,
                                urlCompressed, asAttitude,anonymous
                            )
                        ) {
                            result.value = it
                        }
                    }
//                    val body = MultipartBody.Part.createFormData("upload", file.name, requestFile)


                }

                override fun onError(e: Throwable) {
                    Log.e("luban", "压缩失败")
                }
            }).launch() //启动压缩
        return result
    }

    fun getArticles(
        token: String,
        mode: String,
        beforeTime: Long,
        afterTime: Long,
        pageSize: Int,
        extra: String
    ): LiveData<DataState<List<Article>>> {
        return articleWebSource.getArticles(token, mode, beforeTime, afterTime, pageSize, extra)
    }


    fun getArticleDetail(
        token: String,
        articleId: String,
        digOrigin: Boolean
    ): LiveData<DataState<Article>> {
        return articleWebSource.getArticleInfo(token, articleId, digOrigin)
    }

    fun likeOrUnlikeLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<LikeResult>> {
        return articleWebSource.likeOrUnlikeLive(token, articleId, like)
    }

    fun voteLive(
        token: String,
        articleId: String,
        up: Boolean
    ): LiveData<DataState<VoteResult>> {
        return articleWebSource.voteLive(token, articleId, up)
    }

    fun starOrUnstarLive(
        token: String,
        articleId: String,
        like: Boolean
    ): LiveData<DataState<StarResult>> {
        return articleWebSource.starOrUnstarLive(token, articleId, like)
    }

    fun delete(
        token: String,
        articleId: String
    ): LiveData<DataState<Any>> {
        return articleWebSource.delete(token, articleId)
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