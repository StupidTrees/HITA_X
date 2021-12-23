package com.stupidtree.hitax.ui.eas.score

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.data.MTransformations
import com.stupidtree.component.data.Trigger
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.ui.eas.EASViewModel
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem

class ScoreInquiryViewModel(application: Application) : EASViewModel(application) {
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */
    private val pageController = MutableLiveData<Trigger>()

    val termsLiveData : LiveData<DataState<List<TermItem>>> =
        Transformations.switchMap(pageController) {
            return@switchMap easRepository.getAllTerms()
        }

    val selectedTermLiveData: MutableLiveData<TermItem> = MutableLiveData()
    val selectedTestCategory: MutableLiveData<EASRepository.TestCategory?> = MutableLiveData()

    val scoresLiveData: LiveData<DataState<List<CourseScoreItem>>> =
        MTransformations.switchMap(selectedTermLiveData) {
            return@switchMap easRepository.getPersonalScores(it)
        }//TODO

    /**
     * 方法区
     */
    fun startRefresh() {
        pageController.value = Trigger.actioning
    }


}