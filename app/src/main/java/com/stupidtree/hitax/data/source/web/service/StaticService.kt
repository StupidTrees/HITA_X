package com.stupidtree.hitax.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.stupiduser.data.model.ApiResponse
import com.stupidtree.stupiduser.data.model.CheckUpdateResult
import com.stupidtree.stupiduser.data.model.UserLocal
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 层次：Service
 * 静态数据网络服务
 */
interface StaticService {

    @GET("/static/about")
    fun getAboutPage(): LiveData<ResponseBody?>

    @GET("/static/user_agreement")
    fun getUserAgreementPage(): LiveData<ResponseBody?>

    @GET("/static/privacy_policy")
    fun getPrivacyPolicyPage(): LiveData<ResponseBody?>

}