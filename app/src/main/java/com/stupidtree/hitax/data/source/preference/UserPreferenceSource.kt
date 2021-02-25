package com.stupidtree.hitax.data.source.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.stupidtree.hitax.data.model.service.UserLocal
import java.util.*

/**
 * 层次：DataSource
 * 本地用户的数据源
 * 类型：SharedPreference
 * 数据：同步读取，异步写入
 */
class UserPreferenceSource(private val context: Context) {
    private var sharedPreferences: SharedPreferences? = null
    private val preference: SharedPreferences =
        context.getSharedPreferences(SP_NAME_LOCAL_USER, Context.MODE_PRIVATE)

    fun saveLocalUser(user: UserLocal) {
        preference.edit()
            .putString("id", user.id)
            .putString("username", user.username)
            .putString("nickname", user.nickname)
            .putString("gender", user.gender.toString())
            .putString("publicKey", user.publicKey)
            .putString("signature", user.signature) //获取签名
            .putString("token", user.token)
            .putString("avatar", user.avatar)
            .putString("studentId", user.studentId)
            .putString("school", user.school)
            .apply()
    }

    fun saveAvatar(newAvatar: String?) {
        preference.edit()
            .putString("avatar", newAvatar)
            .apply()
        changeMyAvatarGlideSignature()
    }

    fun saveNickname(nickname: String?) {
        preference.edit()
            .putString("nickname", nickname)
            .apply()
    }

    fun saveGender(gender: String?) {
        preference.edit()
            .putString("gender", gender)
            .apply()
    }

    fun saveSignature(signature: String?) {
        preference.edit()
            .putString("signature", signature)
            .apply()
    }


    fun clearLocalUser() {
        val preferences = context.getSharedPreferences(SP_NAME_LOCAL_USER, Context.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }

    val localUser: UserLocal
        get() {
            val preferences = preference
            val result = UserLocal()
            result.id = preferences.getString("id", null)
            result.username = preferences.getString("username", null)
            result.nickname = preferences.getString("nickname", null)
            result.signature = preferences.getString("signature", null)
            result.token = preferences.getString("token", null)
            result.publicKey = preferences.getString("publicKey", null)
            result.setGender(preferences.getString("gender", "MALE"))
            result.avatar = preferences.getString("avatar", null)
            result.studentId = preferences.getString("studentId", null)
            result.school = preferences.getString("school", null)
            return result
        }

    private fun changeMyAvatarGlideSignature() {
        preference.edit().putString("my_avatar", UUID.randomUUID().toString()).apply()
    }

    val myAvatarGlideSignature: String
        get() {
            var signature = preference.getString("my_avatar", null)
            if (signature == null) {
                signature = UUID.randomUUID().toString()
                preference.edit().putString("my_avatar", signature).apply()
            }
            return signature
        }

    companion object {
        private const val SP_NAME_LOCAL_USER = "local_user_profile"

        @SuppressLint("StaticFieldLeak")
        private var instance: UserPreferenceSource? = null

        @JvmStatic
        fun getInstance(context: Context): UserPreferenceSource {
            if (instance == null) {
                instance = UserPreferenceSource(context.applicationContext)
            }
            return instance!!
        }
    }

}