package com.stupidtree.stupiduser.data.source.web

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.component.web.LiveDataCallAdapter
import com.stupidtree.stupiduser.data.model.ApiResponse
import com.stupidtree.stupiduser.data.model.UserLocal
import com.stupidtree.hitax.data.source.web.service.UserService
import com.stupidtree.hitax.data.source.web.service.codes.SUCCESS
import com.stupidtree.hitax.data.source.web.service.codes.USER_ALREADY_EXISTS
import com.stupidtree.hitax.data.source.web.service.codes.WRONG_PASSWORD
import com.stupidtree.hitax.data.source.web.service.codes.WRONG_USERNAME
import com.stupidtree.stupiduser.R
import com.stupidtree.stupiduser.data.model.LoginResult
import com.stupidtree.stupiduser.data.model.SignUpResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 层次：DataSource
 * 用户的数据源
 * 类型：网络数据
 * 数据：异步读，异步写
 */
object UserWebSource : BaseWebSource<UserService>(
    Retrofit.Builder()
        .addCallAdapterFactory(LiveDataCallAdapter.LiveDataCallAdapterFactory.INSTANCE)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://hita.store:39999").build()
) {
    override fun getServiceClass(): Class<UserService> {
        return UserService::class.java
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    fun login(username: String, password: String): LiveData<LoginResult> {
        return Transformations.map(
            service.login(
                username,
                password
            )
        ) { input: ApiResponse<UserLocal>? ->
            Log.e("login", input.toString())
            val loginResult = LoginResult()
            if (null == input) {
                loginResult[LoginResult.STATES.ERROR] = R.string.login_failed
            } else {
                when (input.code) {
                    SUCCESS -> {
                        Log.e("RESPONSE", "登录成功")
                        if (null == input.data) {
                            Log.e("RESPONSE", "没有找到token")
                            loginResult[LoginResult.STATES.ERROR] = R.string.login_failed
                        } else {
                            loginResult[LoginResult.STATES.SUCCESS] = R.string.login_success
                            loginResult.userLocal = input.data
                        }
                    }
                    WRONG_USERNAME -> {
                        Log.e("RESPONSE", "用户名错误")
                        loginResult[LoginResult.STATES.WRONG_USERNAME] = R.string.wrong_username
                    }
                    WRONG_PASSWORD -> {
                        Log.e("RESPONSE", "密码错误")
                        loginResult[LoginResult.STATES.WRONG_PASSWORD] = R.string.wrong_password
                    }
                    else -> loginResult[LoginResult.STATES.ERROR] = R.string.login_failed
                }
            }
            loginResult
        }
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param gender 性别 MALE/FEMALE
     * @param nickname 昵称
     * @return 注册结果
     */
    fun signUp(
        username: String?,
        password: String?,
        gender: String?,
        nickname: String?
    ): LiveData<SignUpResult?> {
        return Transformations.map(
            service.signUp(username, password, gender, nickname)
        ) { input ->
            val signUpResult = SignUpResult()
            if (input != null) {
                when (input.code) {
                    SUCCESS -> {
                        if (null == input.data) {
                            Log.e("RESPONSE", "没有找到token")
                            signUpResult[SignUpResult.STATES.ERROR] = R.string.signup_confirm_password
                        } else {
                            signUpResult[SignUpResult.STATES.SUCCESS] = R.string.sign_up_success
                        }
                        signUpResult.userLocal = input.data
                    }
                    USER_ALREADY_EXISTS -> signUpResult[SignUpResult.STATES.USER_EXISTS] =
                        R.string.user_already_exists
                    else -> signUpResult[SignUpResult.STATES.ERROR] = R.string.sign_up_failed
                }
            } else {
                signUpResult[SignUpResult.STATES.ERROR] = R.string.sign_up_failed
            }
            signUpResult
        }
    }

}