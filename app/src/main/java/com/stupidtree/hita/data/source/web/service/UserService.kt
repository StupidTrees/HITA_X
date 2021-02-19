package com.stupidtree.hita.data.source.web.service

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.stupidtree.hita.data.model.service.ApiResponse
import com.stupidtree.hita.data.model.service.UserLocal
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * 层次：Service
 * 用户网络服务
 */
interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return retrofit的Call，其中response里包含的是登录成功发放的token
     */
    @FormUrlEncoded
    @POST("/user/log_in")
    fun login(@Field("username") username: String?,
              @Field("password") password: String?): LiveData<ApiResponse<UserLocal>?>

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param gender 性别
     * @param nickname 昵称
     * @return call，其中response里包含token
     */
    @FormUrlEncoded
    @POST("/user/sign_up")
    fun signUp(@Field("username") username: String?,
               @Field("password") password: String?,
               @Field("gender") gender: String?,
               @Field("nickname") nickname: String?): LiveData<ApiResponse<UserLocal>?>

}