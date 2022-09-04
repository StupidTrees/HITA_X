package com.stupidtree.hitax.data.source.web

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.LiveDataCallAdapter
import com.stupidtree.component.web.SslContextFactory
import com.stupidtree.hitax.data.source.web.service.StaticService
import com.stupidtree.stupiduser.data.model.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSocketFactory


class StaticWebSource(context: Context):BaseWebSource<StaticService>(context){

    /**
     * 获得"关于"页面
     */
    fun getAboutPage(): LiveData<DataState<String?>> {
        return Transformations.map(service.getAboutPage()) { input ->
            if (input != null) {
                DataState(input.string())
            }else DataState(DataState.STATE.FETCH_FAILED)
        }
    }
    /**
     * 获得"用户协议"页面
     */
    fun getUserAgreementPage(): LiveData<DataState<String?>> {
        return Transformations.map(service.getUserAgreementPage()) { input ->
            if (input != null) {
                DataState(input.string())
            }else DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    /**
     * 获得"隐私政策"页面
     */
    fun getPrivacyPolicyPage(): LiveData<DataState<String?>> {
        return Transformations.map(service.getPrivacyPolicyPage()) { input ->
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