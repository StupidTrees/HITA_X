package com.stupidtree.hita.theta.ui.topic.detail

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.repository.TopicRepository
import com.stupidtree.hita.theta.ui.list.ArticleListFragment
import com.stupidtree.hita.theta.ui.list.activity.ArticleListViewModel
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class TopicDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val topicRepo = TopicRepository.getInstance(application)
    private val userRepo = LocalUserRepository.getInstance(application)

    private val topicIdLiveData = MutableLiveData<String>()

    val topicLiveData = Transformations.switchMap(topicIdLiveData) {
        val user = userRepo.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap topicRepo.getTopic(user.token!!, it)
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    fun startRefresh(topicId: String) {
        topicIdLiveData.value = topicId
    }



}