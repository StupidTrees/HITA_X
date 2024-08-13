package com.stupidtree.hitax.data.source.web

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.hitax.data.source.web.service.StaticService


class StaticWebSource(context: Context):BaseWebSource<StaticService>(context){

    /**
     * 获得"关于"页面
     */
    fun getAboutPage(): LiveData<DataState<String?>> {
        return service.getAboutPage().map { input ->
            if (input != null) {
                DataState(input.string())
            }else DataState(DataState.STATE.FETCH_FAILED)
        }
    }
    /**
     * 获得"用户协议"页面
     */
    fun getUserAgreementPage(): LiveData<DataState<String?>> {
        return service.getUserAgreementPage().map { input ->
            if (input != null) {
                DataState(input.string())
            }else DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    /**
     * 获得"隐私政策"页面
     */
    fun getPrivacyPolicyPage(): LiveData<DataState<String?>> {
        return service.getPrivacyPolicyPage().map { input ->
            if (input != null) {
                DataState(input.string())
            }else DataState(DataState.STATE.FETCH_FAILED)
        }
    }
    companion object {
        var instance: StaticWebSource? = null
        fun getInstance(context: Context): StaticWebSource {
            synchronized(StaticWebSource::class.java) {
                if (instance == null) {
                    instance = StaticWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

    override fun getServiceClass(): Class<StaticService> {
        return StaticService::class.java
    }
}