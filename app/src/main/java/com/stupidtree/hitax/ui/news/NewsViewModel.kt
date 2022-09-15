package com.stupidtree.hitax.ui.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.StringTrigger
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.repository.AdditionalRepository

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    val addRepo = AdditionalRepository.getInstance(application)

    val refreshController = MutableLiveData<StringTrigger>()
    val metaData = Transformations.switchMap(refreshController) {
        return@switchMap addRepo.getNewsMeta(it.data)
    }

    fun refresh(link: String) {
        refreshController.value = StringTrigger.getActioning(link)
    }
}