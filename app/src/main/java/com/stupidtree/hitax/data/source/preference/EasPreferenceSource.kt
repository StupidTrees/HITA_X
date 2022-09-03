package com.stupidtree.hitax.data.source.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.stupidtree.hitax.data.model.eas.EASToken

/**
 * 层次：DataSource
 * 教务登录状态的数据源
 * 类型：SharedPreference
 * 数据：同步读取，异步写入
 */
class EasPreferenceSource(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences(SP_NAME_EAS_TOKEN, Context.MODE_PRIVATE)

    fun saveEasToken(token: EASToken) {
        preference.edit()
            .putString("username", token.username)
            .putString("password", token.password)
            .putString("cookies", Gson().toJson(token.cookies))
            .putString("stutype", token.getStudentType())
            .putString("picture", token.picture)
            .putString("id", token.id)
            .putString("stuId", token.stuId)
            .putString("school", token.school)
            .putString("major", token.major)
            .putString("grade", token.grade)
            .putString("sfxsx", token.sfxsx)
            .putString("email", token.email)
            .putString("phone", token.phone)
            .apply()
    }


    fun clearEasToken() {
        preference.edit().putString("cookies", null).apply()
    }

    fun getEasToken(): EASToken {
        val result = EASToken()
        result.username = preference.getString("username", null)
        result.password = preference.getString("password", null)
        result.stutype = if (preference.getString("stutype", "1")
                .equals("1")
        ) EASToken.TYPE.UNDERGRAD else EASToken.TYPE.GRAD
        result.picture = preference.getString("picture", null)
        result.id = preference.getString("id", null)
        result.stuId = preference.getString("stuId", null)
        result.school = preference.getString("school", null)
        result.major = preference.getString("major", null)
        result.grade = preference.getString("grade", "")
        result.sfxsx = preference.getString("sfxsx", null)
        result.email = preference.getString("email", null)
        result.phone = preference.getString("phone", null)
        val map = Gson().fromJson(preference.getString("cookies", "{}"), HashMap::class.java)
        for (e in map.entries) {
            result.cookies[e.key.toString()] = e.value.toString()
        }
        return result
    }


    companion object {
        private const val SP_NAME_EAS_TOKEN = "local_eas_token"

        @SuppressLint("StaticFieldLeak")
        private var instance: EasPreferenceSource? = null

        @JvmStatic
        fun getInstance(context: Context): EasPreferenceSource {
            if (instance == null) {
                instance = EasPreferenceSource(context.applicationContext)
            }
            return instance!!
        }
    }

}