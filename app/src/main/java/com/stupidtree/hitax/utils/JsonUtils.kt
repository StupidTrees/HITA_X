package com.stupidtree.hitax.utils

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

    fun getJsonObject(jo: String): JSONObject? {
        return try {
            JSONObject(jo)
        } catch (e: JSONException) {
            null
        }
    }
    fun getJsonArray(ja: String): JSONArray? {
        return try {
            JSONArray(ja)
        } catch (e: JSONException) {
            null
        }
    }
}