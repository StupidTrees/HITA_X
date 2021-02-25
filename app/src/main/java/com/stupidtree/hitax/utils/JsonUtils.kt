package com.stupidtree.hitax.utils

import com.google.gson.JsonObject

/**
 * 此类整合了一些JSON格式数据解析有关的函数
 */
object JsonUtils {
    fun getIntegerData(jo: JsonObject, key: String?): Int? {
        return try {
            jo[key].asInt
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getStringData(jo: JsonObject, key: String?): String? {
        return try {
            jo[key].asString
        } catch (ignored: Exception) {
            null
        }
    }

    fun getObjectData(jo: JsonObject, key: String?): JsonObject? {
        return try {
            jo[key].asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}