package com.stupidtree.hita.theta.ui.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.ArticleRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class CreateArticleViewModel(application: Application) : AndroidViewModel(application) {
    private val articleRepository = ArticleRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)

    private val postController = MutableLiveData<CreatePostForm>()
    val postResult = Transformations.switchMap(postController) {
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap articleRepository.postArticle(user.token!!, it.content,repostIdLiveData.value)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }

    }

    private val repostIdLiveData = MutableLiveData<String?>()
    val repostArticleLiveData = Transformations.switchMap(repostIdLiveData){
        val user = localUserRepository.getLoggedInUser()
        if (user.isValid() && !it.isNullOrEmpty()) {
            return@switchMap articleRepository.getArticleDetail(user.token!!,it,false)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    fun createArticle(content: String) {
        postController.value = CreatePostForm().also {
            it.content = content
        }
    }

    fun refreshRepostArticle(repostId:String?){
        repostIdLiveData.value = repostId
    }
}