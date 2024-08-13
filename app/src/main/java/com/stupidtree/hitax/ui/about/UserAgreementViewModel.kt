package com.stupidtree.hitax.ui.about

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.repository.StaticRepository

class UserAgreementViewModel(application: Application) : AndroidViewModel(application) {
    private val staticRepo = StaticRepository.getInstance(application)

    private val refreshController = MutableLiveData<Trigger>()

    val userAgreementPageLiveData =  refreshController.switchMap {
        return@switchMap staticRepo.getUAPage()
    }

    val privacyPolicyPageLiveData = refreshController.switchMap {
        return@switchMap staticRepo.getPPPage()
    }

    fun refresh() {
        refreshController.value = Trigger.actioning
    }

}