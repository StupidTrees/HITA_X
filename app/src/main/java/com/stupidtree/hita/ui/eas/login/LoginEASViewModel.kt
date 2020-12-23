package com.stupidtree.hita.ui.eas.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.repository.EASRepository
import com.stupidtree.hita.ui.base.DataState

class LoginEASViewModel(application: Application) : AndroidViewModel(application){

    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */
    private val loginController:MutableLiveData<LoginTrigger> = MutableLiveData()
    val loginResultLiveData:LiveData<DataState<Boolean>>
        get() {
            return Transformations.switchMap(loginController){
                return@switchMap easRepository.login(it.username,it.password)
            }
        }


    /**
     * 方法区
     */

    fun startLogin(username:String,password:String){
        loginController.value = LoginTrigger.getActioning(username,password,null)
    }

}