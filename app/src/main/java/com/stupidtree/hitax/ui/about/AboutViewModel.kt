package com.stupidtree.hitax.ui.about

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.data.repository.StaticRepository
import com.stupidtree.hitax.utils.LiveDataUtils
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.data.repository.ManagerRepository

class AboutViewModel(application: Application) : AndroidViewModel(application) {
    private val staticRepo = StaticRepository.getInstance(application)
    private val localUserRepository = LocalUserRepository.getInstance(application)
    private val managerRepository = ManagerRepository.getInstance(application)

    private val refreshController = MutableLiveData<Trigger>()

    val aboutPageLiveData = Transformations.switchMap(refreshController) {
        return@switchMap staticRepo.getAboutPage()
    }

    private val checkUpdateTrigger = MutableLiveData<Long>()
    val checkUpdateResult = Transformations.switchMap(checkUpdateTrigger) {
        if (localUserRepository.getLoggedInUser().isValid()) {
            return@switchMap managerRepository.checkUpdate(
                localUserRepository.getLoggedInUser().token?:"",
                it,
                EASRepository.getInstance(application).getEasToken().stuId
            )
        } else {
            return@switchMap LiveDataUtils.getMutableLiveData(DataState(DataState.STATE.NOT_LOGGED_IN))
        }
    }


    fun refresh() {
        refreshController.value = Trigger.actioning
    }

    fun checkForUpdate(versionCode: Long) {
        checkUpdateTrigger.value = versionCode
    }

}