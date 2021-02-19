package com.stupidtree.hita.data.source.web.service

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.stupidtree.hita.data.model.service.ApiResponse
import com.stupidtree.hita.data.model.service.UserLocal
import com.stupidtree.hita.data.model.service.UserProfile
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * 层次：Service
 * 用户网络服务
 */
interface ProfileService {
    /**
     * 用户资料获取
     * @param token 登录状态的token
     * @return call，返回的会是用户基本资料
     */
    @GET("/profile/get")
    fun getUserProfile(@Header("Authorization") token: String): LiveData<ApiResponse<UserProfile>?>

    /**
     * 上传头像
     * @param file 头像体
     * @param token 登录状态的token
     * @return 返回操作结果和文件名
     */
    @Multipart
    @POST("/profile/upload_avatar")
    fun uploadAvatar(
        @Part file: MultipartBody.Part?,
        @Header("Authorization") token: String?
    ): LiveData<ApiResponse<String>?>

    /**
     * 更换昵称
     * @param nickname 新昵称
     * @param token 登录状态的token（表征了用户身份）
     * @return 操作结果
     */
    @FormUrlEncoded
    @POST("/profile/change_nickname")
    fun changeNickname(
        @Field("nickname") nickname: String?,
        @Header("Authorization") token: String?
    ): LiveData<ApiResponse<Any?>?>

    /**
     * 更换性别
     * @param gender 新性别 MALE/FEMALE
     * @param token 登录状态的token
     * @return 操作结果
     */
    @FormUrlEncoded
    @POST("/profile/change_gender")
    fun changeGender(
        @Field("gender") gender: String?,
        @Header("Authorization") token: String?
    ): LiveData<ApiResponse<Any?>?>


    /**
     * 更换签名
     * @param signature 新签名
     * @param token 登录状态的token（表征了用户身份）
     * @return 操作结果
     */
    @FormUrlEncoded
    @POST("/profile/change_signature")
    fun changeSignature(@Field("signature") signature: String?, @Header("Authorization") token: String?): LiveData<ApiResponse<Any?>?>

}