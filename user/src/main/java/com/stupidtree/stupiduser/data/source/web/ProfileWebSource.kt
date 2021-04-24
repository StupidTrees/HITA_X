package com.stupidtree.stupiduser.data.source.web

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.LiveDataCallAdapter
import com.stupidtree.stupiduser.data.model.ApiResponse
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.hitax.data.source.web.service.ProfileService
import com.stupidtree.stupiduser.data.source.web.service.codes.SUCCESS
import com.stupidtree.stupiduser.data.source.web.service.codes.TOKEN_INVALID
import com.stupidtree.stupiduser.data.model.FollowResult
import com.stupidtree.stupiduser.util.HttpUtils
import com.stupidtree.sync.data.source.web.SyncWebSource
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * 层次：DataSource
 * 用户的数据源
 * 类型：网络数据
 * 数据：异步读，异步写
 */
class ProfileWebSource(context: Context) : BaseWebSource<ProfileService>(context
) {
    override fun getServiceClass(): Class<ProfileService> {
        return ProfileService::class.java
    }


    /**
     * 更换签名
     * @param token 令牌
     * @param signature 签名
     * @return 操作结果
     */
    fun changeSignature(token: String, signature: String): LiveData<DataState<String>> {
        return Transformations.map(
            service.changeSignature(
                signature,
                HttpUtils.getHeaderAuth(token)
            )
        ) { input: ApiResponse<Any?>? ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> {
                        println("SUCCEED")
                        return@map DataState<String>(DataState.STATE.SUCCESS)
                    }
                    TOKEN_INVALID -> return@map DataState<String>(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState<String>(DataState.STATE.FETCH_FAILED, input.msg)
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    /**
     * 换昵称
     * @param token 令牌
     * @param nickname 昵称
     * @return 操作结果
     */
    fun changeNickname(token: String, nickname: String): LiveData<DataState<String?>> {
        return Transformations.map(
            service.changeNickname(
                nickname,
                HttpUtils.getHeaderAuth(token)
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState<String?>(DataState.STATE.SUCCESS)
                    TOKEN_INVALID -> return@map DataState<String?>(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState<String?>(
                        DataState.STATE.FETCH_FAILED,
                        input.msg
                    )
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }

    /**
     * 获取用户资料
     * @param token 用户令牌
     * @return 资料
     */
    fun getUserProfile(token: String,userId:String): LiveData<DataState<UserProfile>> {
        return Transformations.map(service.getUserProfile(HttpUtils.getHeaderAuth(token),userId)) { input ->

            if (input != null) {
                when (input.code) {
                    SUCCESS -> input.data?.let { return@map DataState(it) }
                    TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(DataState.STATE.FETCH_FAILED)
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }
    fun follow(token: String,userId:String,follow:Boolean): LiveData<DataState<FollowResult>> {
        return Transformations.map(service.follow(HttpUtils.getHeaderAuth(token),userId,follow)) { input ->

            if (input != null) {
                when (input.code) {
                    SUCCESS -> input.data?.let { return@map DataState(it) }
                    TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState(DataState.STATE.FETCH_FAILED)
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }



    /**
     * 更换性别
     * @param token 令牌
     * @param gender 性别 MALE/FEMALE
     * @return 操作结果
     */
    fun changeGender(token: String, gender: String): LiveData<DataState<String>> {
        return Transformations.map(
            service.changeGender(
                gender,
                HttpUtils.getHeaderAuth(token)
            )
        ) { input ->
            if (input != null) {
                when (input.code) {
                    SUCCESS -> return@map DataState<String>(DataState.STATE.SUCCESS)
                    TOKEN_INVALID -> return@map DataState<String>(DataState.STATE.TOKEN_INVALID)
                    else -> return@map DataState<String>(
                        DataState.STATE.FETCH_FAILED,
                        input.msg
                    )
                }
            }
            DataState(DataState.STATE.FETCH_FAILED)
        }
    }


    /**
     * 更换头像
     * @param token 令牌
     * @param file 图片请求包
     * @return 返回
     */
    fun changeAvatar(token: String, file: MultipartBody.Part): LiveData<DataState<String>> {
        return Transformations.map(
            service.uploadAvatar(
                file,
                HttpUtils.getHeaderAuth(token)
            )
        ) { input ->
            Log.e("avatar", input.toString())
            if (input == null) {
                return@map DataState(DataState.STATE.FETCH_FAILED)
            }
            when (input.code) {
                SUCCESS -> {
                    if (input.data != null) {
                        return@map DataState(input.data!!)
                    } else {
                        return@map DataState(DataState.STATE.FETCH_FAILED)
                    }
                }
                TOKEN_INVALID -> return@map DataState(DataState.STATE.TOKEN_INVALID)
                else -> return@map DataState(DataState.STATE.FETCH_FAILED, input.msg)
            }
        }
    }
    companion object {
        var instance: ProfileWebSource? = null
        fun getInstance(context: Context): ProfileWebSource {
           synchronized(ProfileWebSource::class.java) {
                if (instance == null) {
                    instance = ProfileWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}