package com.stupidtree.hitax.ui.eas.exam

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.MTransformations
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.ExamItem
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.data.source.web.service.EASService
import com.stupidtree.hitax.ui.eas.EASViewModel

class ExamViewModel(application: Application) : EASViewModel(application){
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */
    private val pageController = MutableLiveData<Trigger>()

    val examInfoLiveData:LiveData<DataState<List<ExamItem>>> =
        Transformations.switchMap(pageController) {
            return@switchMap easRepository.getExamInfo()
        }

    /**
     * 方法区
     */
    fun startRefresh() {
        pageController.value = Trigger.actioning
    }


}