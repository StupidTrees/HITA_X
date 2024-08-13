package com.stupidtree.hita.theta.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.model.Topic
import com.stupidtree.hita.theta.data.repository.TopicRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class ArticleNavigationViewModel(application: Application) : AndroidViewModel(application) {
    private val topicRepo = TopicRepository.getInstance(application)
    private val localUserRepo = LocalUserRepository.getInstance(application)

    private val hotTopicController = MutableLiveData<String>()
    val hotTopicsLiveData: LiveData<DataState<List<Topic>>> = hotTopicController.switchMap{
            val user = localUserRepo.getLoggedInUser()
            if (user.isValid()) {
                return@switchMap topicRepo.getTopics(user.token!!, "hot", 0, 0, "")
            } else {
                return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
            }
        }


    fun startRefresh() {
        hotTopicController.value = ""
    }
}