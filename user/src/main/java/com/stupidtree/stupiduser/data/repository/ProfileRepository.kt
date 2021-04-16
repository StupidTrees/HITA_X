package com.stupidtree.stupiduser.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.component.data.DataState
import com.stupidtree.stupiduser.data.UserDatabase
import com.stupidtree.stupiduser.data.model.service.UserProfile
import com.stupidtree.stupiduser.data.source.web.ProfileWebSource
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Repository层：用户资料页面的Repository
 */
class ProfileRepository(application: Application) {
    //数据源1：网络类型数据源，用户网络操作
    private val userProfileDao = UserDatabase.getDatabase(application).userProfileDao()
    private val localUserRepository: LocalUserRepository =
        LocalUserRepository.getInstance(application)

    /**
     * 获取用户资料
     *@param userId 用户id
     * @param token 令牌
     * @return 用户资料
     * 这里的用户资料本体是UserProfile类
     * 其中DataState用于包装这个本体，附带状态信息
     * MutableLiveData则是UI层面的，用于和ViewModel层沟通
     */
    fun getUserProfile(userId: String, token: String): LiveData<DataState<UserProfile>> {
        val result = MediatorLiveData<DataState<UserProfile>>()
        result.addSource(userProfileDao.queryProfile(userId)) { it ->
            it?.let {
                result.value = DataState(it)
            }
        }
        result.addSource(ProfileWebSource.getUserProfile(token)) {
            if (it.state == DataState.STATE.SUCCESS) {
                Thread {
                    it.data?.let { it1 -> userProfileDao.saveProfile(it1) }
                }.start()
            }
        }
        return result
    }

    /**
     * 更改用户头像
     *
     * @param token    令牌
     * @param filePath 头像路径
     * @return 操作结果
     */
    fun changeAvatar(token: String, filePath: String): LiveData<DataState<String>> {
        //读取图片文件
        val file = File(filePath)
        // MutableLiveData<DataState<String>> result = new MutableLiveData<>();
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        //构造一个图片格式的POST表单
        val body = MultipartBody.Part.createFormData("upload", file.name, requestFile)
        //调用网络数据源的服务，上传头像
        return Transformations.map(
            ProfileWebSource.changeAvatar(
                token,
                body
            )
        ) { input ->

            if (input.state === DataState.STATE.SUCCESS) {
                //通知本地用户更新资料
                localUserRepository.changeLocalAvatar(input.data)
            }
            input
        }
    }

    /**
     * 更改用户昵称
     *
     * @param token    令牌
     * @param nickname 新昵称
     * @return 操作结果
     */
    fun changeNickname(token: String, nickname: String): LiveData<DataState<String?>> {
        return Transformations.map(
            ProfileWebSource.changeNickname(
                token,
                nickname
            )
        ) { input: DataState<String?> ->
            if (input.state === DataState.STATE.SUCCESS) {
                localUserRepository.changeLocalNickname(nickname)
            }
            input
        }
    }

    /**
     * 更改用户性别
     *
     * @param token  令牌
     * @param gender 新性别 MALE/FEMALE
     * @return 操作结果
     */
    fun changeGender(token: String, gender: String): LiveData<DataState<String>> {
        return Transformations.map(
            ProfileWebSource.changeGender(
                token,
                gender
            )
        ) { input ->
            if (input.state === DataState.STATE.SUCCESS) {
                localUserRepository.changeLocalGender(gender)
            }
            input
        }
    }


    /**
     * 更改用户签名
     *
     * @param token     令牌
     * @param signature 新签名
     * @return 操作结果
     */
    fun changeSignature(token: String, signature: String): LiveData<DataState<String>> {
        return Transformations.map(ProfileWebSource.changeSignature(token, signature)) { input ->
            if (input.state === DataState.STATE.SUCCESS) {
                localUserRepository.changeLocalSignature(signature)
            }
            input
        }
    }


    companion object {
        @JvmStatic
        var instance: ProfileRepository? = null
            private set

        fun getInstance(application: Application): ProfileRepository {
            if (instance == null) {
                instance = ProfileRepository(application)
            }
            return instance!!
        }

    }

}