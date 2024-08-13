package com.stupidtree.hita.theta.ui.me

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.data.repository.MessageRepository
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val messageRepository = MessageRepository.getInstance(application)
    private val localUserRepo = LocalUserRepository.getInstance(application)
    private val refreshTrigger = MutableLiveData<String>()

    val repostNumLiveData = refreshTrigger.switchMap{
        val user = localUserRepo.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap messageRepository.countUnread(user.token!!, "repost")
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    val likeNumLiveData = refreshTrigger.switchMap {
        val user = localUserRepo.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap messageRepository.countUnread(user.token!!, "like")
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    val commentNumLiveData =  refreshTrigger.switchMap{
        val user = localUserRepo.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap messageRepository.countUnread(user.token!!, "comment")
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    val followNumLiveData = refreshTrigger.switchMap{
        val user = localUserRepo.getLoggedInUser()
        if (user.isValid()) {
            return@switchMap messageRepository.countUnread(user.token!!, "follow")
        } else {
            return@switchMap MutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }

    fun startRefresh() {
        refreshTrigger.value = ""
    }
}