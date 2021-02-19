package com.stupidtree.hita.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.service.UserLocal
import com.stupidtree.hita.data.repository.LocalUserRepository
import com.stupidtree.hita.ui.base.Trigger

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