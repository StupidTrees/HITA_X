package com.stupidtree.hitax.utils

import androidx.compose.runtime.key
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 此类整合了一些JSON格式数据解析有关的函数
 */
object JsonUtils {
    fun getStringData(jo: JsonObject, key: String?): String? {
        return try {
            jo[key].asString
        } catch (ignored: Exception) {
            null
        }
    }

    fun getJsonObject(jo: String?): JSONObject? {
        return if(jo==null) null
        else try {
            JSONObject(jo)
        } catch (e: JSONException) {
            null
        }
    }

    fun jsonStringToMap(jo: String?): Map<String, String> {
        return jsonToMap(getJsonObject(jo))
    }
    fun jsonToMap(jo: JSONObject?): Map<String, String> {
        val res = mutableMapOf<String, String>()
        if (jo != null) {
            for (key in jo.keys()) {
                res[key] = jo[key].toString()
            }
        }
        return res
    }

    fun getJsonArray(ja: String): JSONArray? {
        return try {
            JSONArray(ja)
        } catch (e: JSONException) {
            null
        }
    }
}