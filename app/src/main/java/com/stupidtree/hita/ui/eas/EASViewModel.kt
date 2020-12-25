package com.stupidtree.hita.ui.eas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.repository.EASRepository
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.base.Trigger

class EASViewModel(application: Application) : AndroidViewModel(application){
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */

    private val termsController:MutableLiveData<Trigger> = MutableLiveData()
    val termsLiveData:LiveData<DataState<List<TermItem>>>
        get() {
            return Transformations.switchMap(termsController){
                return@switchMap easRepository.getAllTerms()
            }
        }

    /**
     * 方法区
     */


    fun startRefreshTerms(){
        termsController.value = Trigger.actioning
    }

}