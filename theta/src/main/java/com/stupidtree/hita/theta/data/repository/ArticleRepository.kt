package com.stupidtree.hita.theta.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class ArticleRepository(val application: Application) {
    val articleWebSource = ArticleWebSource.getInstance(application)

    fun postArticle(
        token: String,
        content: String,
        repostId: String?
    ): LiveData<DataState<Boolean>> {
        return articleWebSource.postArticle(
            token,
            content,
            repostId
        )
    }

    fun postArticle(
        token: String,
        content: String,
        urls: List<String>,
        repostId: String?
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
                                urlCompressed
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