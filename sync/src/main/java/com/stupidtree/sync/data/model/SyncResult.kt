package com.stupidtree.sync.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlin.collections.List as List


class SyncResult {
    enum class ACTION { PUSH, PULL, NONE }

    var action: ACTION = ACTION.NONE
    var latestId: Long = 0
    var history: String = ""
    var data: String = ""
    override fun toString(): String {
        return "SyncResult(action=$action, latestId=$latestId, history='$history', data='$data')"
    }

    fun getHistories(): List<History> {
        val res = mutableListOf<History>()
        val tmpList = Gson().fromJson(history, List::class.java)
        for (t in tmpList) {
            val obj = Gson().fromJson(t.toString(), History::class.java)
            res.add(obj)
        }
        return res
    }

    fun getDataMap(): Map<String, List<String>> {
        val res = mutableMapOf<String, List<String>>()
        val tmpMap = Gson().fromJson(data, Map::class.java)
        for ((k, v) in tmpMap) {
            v?.let {
                Log.e("v", it.toString())
                val list = Gson().fromJson(Gson().toJson(it), List::class.java)
                val strList = mutableListOf<String>()
                for (l in list) {
                    strList.add(l.toString())
                }
                res[k.toString()] = strList
            }
        }
        return res
    }

}