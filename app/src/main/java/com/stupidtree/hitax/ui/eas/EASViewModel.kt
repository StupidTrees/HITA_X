package com.stupidtree.hitax.ui.eas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.base.Trigger
import com.stupidtree.hitax.utils.LiveDataUtils

class EASViewModel(application: Application) : AndroidViewModel(application){
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

//    /**
//     * LiveData区
//     */
//
//    private val termsController:MutableLiveData<Trigger> = MutableLiveData()
//    val termsLiveData:LiveData<DataState<List<TermItem>>>
//        get() {
//            return Transformations.switchMap(termsController){
//                return@switchMap easRepository.getAllTerms()
//            }
//        }

    private val loginCheckController = MutableLiveData<Trigger>()
    val loginCheckResult:LiveData<DataState<Boolean>> = Transformations.switchMap(loginCheckController){
        if(it.isActioning){
            return@switchMap easRepository.loginCheck()
        }
        return@switchMap LiveDataUtils.getMutableLiveData(DataState(DataState.STATE.NOTHING))
    }

    /**
     * 方法区
     */
//    fun startRefreshTerms(){
//        termsController.value = Trigger.actioning
//    }


    fun startLoginCheck(){
        loginCheckController.value = Trigger.actioning
    }

}