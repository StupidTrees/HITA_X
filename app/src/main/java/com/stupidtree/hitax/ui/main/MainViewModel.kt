package com.stupidtree.hitax.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.stupiduser.data.model.UserLocal
import com.stupidtree.component.data.Trigger
import com.stupidtree.stupiduser.data.repository.LocalUserRepository

class MainViewModel(application: Application) : AndroidViewModel(application){
    /**
     * 仓库区
     */
    private val localUserRepository = LocalUserRepository.getInstance(application)

    private val localUserController = MutableLiveData<Trigger>()
    val loggedInUserLiveData:LiveData<UserLocal> = Transformations.switchMap(localUserController){
        val res = MutableLiveData<UserLocal>()
        res.value = localUserRepository.getLoggedInUser()
        return@switchMap res
    }


    fun startRefreshUser(){
        localUserController.value = Trigger.actioning
    }
}