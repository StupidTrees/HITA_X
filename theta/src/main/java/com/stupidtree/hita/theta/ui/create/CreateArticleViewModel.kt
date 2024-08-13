package com.stupidtree.hita.theta.ui.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.ArticleRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class CreateArticleViewModel(application: Application) : AndroidViewModel(application) {
    private val articleRepository = ArticleRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)

    private val postController = MutableLiveData<CreatePostForm>()
    val postResult = postController.switchMap{
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap if (it.urls.isNotEmpty()) {
                articleRepository.postArticle(
                    user.token!!,
                    it.content,
                    it.urls,
                    repostIdLiveData.value,
                    topicIdLiveData.value?.first,
                    asAttitudeLiveData.value==true,
                    anonymousLiveData.value==true
                )
            } else {
                articleRepository.postArticle(
                    user.token!!,
                    it.content,
                    repostIdLiveData.value,
                    topicIdLiveData.value?.first,
                    asAttitudeLiveData.value==true,
                    anonymousLiveData.value==true
                )
            }

        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }

    }


    private val repostIdLiveData = MutableLiveData<String?>()
    val repostArticleLiveData = repostIdLiveData.switchMap{
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid() && !it.isNullOrEmpty()) {
            return@switchMap articleRepository.getArticleDetail(user.token!!, it, false)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }


    val topicIdLiveData = MutableLiveData<Pair<String, String>?>()
    val asAttitudeLiveData = MutableLiveData<Boolean>()
    val anonymousLiveData = MutableLiveData<Boolean>()


    val imageUriLiveData = MutableLiveData<List<String>>(mutableListOf())

    fun createArticle(content: String) {
        postController.value = CreatePostForm().also {
            it.content = content
            imageUriLiveData.value?.let { urls ->
                it.urls = urls
            }

        }
    }

    fun refreshRepostArticle(repostId: String?) {
        repostIdLiveData.value = repostId
    }

    fun removeImage(position: Int) {
        val newL = imageUriLiveData.value?.toMutableList()
        newL?.removeAt(position)
        imageUriLiveData.value = newL
    }

    fun addImage(url: String) {
        val newL = imageUriLiveData.value?.toMutableList()
        newL?.add(url)
        imageUriLiveData.value = newL
    }

    fun setTopicInfo(id: String, name: String) {
        topicIdLiveData.value = Pair(id, name)
    }
}