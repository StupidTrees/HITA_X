package com.stupidtree.hita.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.source.web.eas.EASource
import com.stupidtree.hita.data.source.web.service.EASService
import com.stupidtree.hita.ui.base.DataState

class EASRepository internal constructor(application: Application) {

    private val easService: EASService = EASource()
    private var token:EASToken? = null

    /**
     * 进行登录
     */
    fun login(username: String, password: String):LiveData<DataState<Boolean>>{
        return Transformations.map(easService.login(username,password,null)){
            if(it.state==DataState.STATE.SUCCESS && it.data!=null){
                token = it.data
                return@map DataState(true,DataState.STATE.SUCCESS)
            }
            return@map DataState(false,it.state)
        };
    }

    /**
     * 进行获取学年学期
     */
    fun getAllTerms():LiveData<DataState<List<TermItem>>>{
        return if(token!=null){
            easService.getAllTerms(token!!)
        }else{
            val res = MutableLiveData<DataState<List<TermItem>>>()
            res.value = DataState(DataState.STATE.NOT_LOGGED_IN)
            res
        }
    }



    companion object {
        private var instance: EASRepository? = null
        fun getInstance(application: Application): EASRepository {
            if (instance == null) instance = EASRepository(application)
            return instance!!
        }
    }
}