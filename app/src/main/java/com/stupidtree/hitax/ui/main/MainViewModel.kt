package com.stupidtree.hitax.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.stupiduser.data.model.UserLocal
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.utils.LiveDataUtils
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.data.repository.ManagerRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val managerRepository = ManagerRepository.getInstance(application)

    /**
     * LiveData
     */
    private val localUserController = MutableLiveData<Trigger>()
    val loggedInUserLiveData: LiveData<UserLocal> = Transformations.switchMap(localUserController) {
        val res = MutableLiveData<UserLocal>()
        res.value = localUserRepository.getLoggedInUser()
        return@switchMap res
    }

    private val checkUpdateTrigger = MutableLiveData<Long>()
    val checkUpdateResult = Transformations.switchMap(checkUpdateTrigger) {
        if (localUserRepository.getLoggedInUser().isValid()) {
            return@switchMap managerRepository.checkUpdate(
                localUserRepository.getLoggedInUser().token!!,
                it,
                EASRepository.getInstance(application).getEasToken().stuId
            )
        } else {
            return@switchMap LiveDataUtils.getMutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }


    fun startRefreshUser() {
        localUserController.value = Trigger.actioning
    }


    fun checkForUpdate(versionCode: Long) {
        checkUpdateTrigger.value = versionCode
    }
}