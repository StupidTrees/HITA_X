package com.stupidtree.hita.data.source.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.stupidtree.hita.data.model.eas.EASToken

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
            .apply()
    }


    fun clearEasToken() {
        preference.edit().putString("cookie",null).apply()
    }

    fun getEasToken(): EASToken {
        val result = EASToken()
        result.username = preference.getString("username", null)
        result.password = preference.getString("password", null)
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