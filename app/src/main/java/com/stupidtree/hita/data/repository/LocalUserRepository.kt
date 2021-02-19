package com.stupidtree.hita.data.repository

import android.app.Application
import android.content.Context
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.AppDatabase.Companion.getDatabase
import com.stupidtree.hita.data.model.service.UserLocal
import com.stupidtree.hita.data.source.preference.UserPreferenceSource

/**
 * 层次：Repository
 * ”我的“页面的Repository，同时也是全局的本地用户仓库
 */
class LocalUserRepository(application: Application) {

    //数据源：SharedPreference性质的本地状态数据源
    var mePreferenceSource: UserPreferenceSource = UserPreferenceSource(application)

    //将已登录用户缓存在内存里
    private var loggedInUser: UserLocal? = null
    private val appDatabase: AppDatabase = getDatabase(application)

    /**
     * 登出
     */
    fun logout(context: Context) {
        loggedInUser = null
        mePreferenceSource.clearLocalUser()
        //本地缓存清空
        Thread {
//            appDatabase.chatMessageDao().clearTable()
//            appDatabase.userRelationDao().clearTable()
//            appDatabase.conversationDao().clearTable()
//            appDatabase.userProfileDao().clearTable()
        }.start()
    }

    /**
     * 更改该本地缓存的头像地址
     * @param newAvatar 头像地址
     */
    fun changeLocalAvatar(newAvatar: String?) {
        mePreferenceSource.saveAvatar(newAvatar)
        loggedInUser = mePreferenceSource.localUser
        // getThis().getSharedPreferences("Glide", Context.MODE_PRIVATE).edit().
    }

    /**
     * 更改本地缓存的昵称
     * @param nickname 新昵称
     */
    fun changeLocalNickname(nickname: String?) {
        mePreferenceSource.saveNickname(nickname)
        loggedInUser = mePreferenceSource.localUser
    }

    /**
     * 更改本地缓存的用户性别
     * @param gender 性别/MALE/FEMALE
     */
    fun changeLocalGender(gender: String?) {
        mePreferenceSource.saveGender(gender)
        loggedInUser = mePreferenceSource.localUser
    }



    /**
     * 更改本地缓存的签名
     * @param signature 新签名
     */
    fun changeLocalSignature(signature: String?) {
        mePreferenceSource.saveSignature(signature)
    }

    /**
     * 直接获取本地已登陆的用户对象
     * 同步获取
     * @return 本地用户对象
     */
    fun getLoggedInUser(): UserLocal {
        //Log.e("get_local_user", String.valueOf(loggedInUser));
        //if(loggedInUser==null){
        loggedInUser = mePreferenceSource.localUser
        // }
        return loggedInUser!!
    }


    companion object {
        //也是单例模式
        @Volatile
        private var instance: LocalUserRepository? = null
        fun getInstance(application: Application):LocalUserRepository {
            if (null == instance) {
                instance = LocalUserRepository(application)
            }
            return instance!!
        }
    }

}