package com.stupidtree.sync.data.source.web

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.sync.data.model.History
import com.stupidtree.sync.data.model.SyncResult
import com.stupidtree.sync.data.source.web.service.SyncService
import retrofit2.Call


class SyncWebSource(context: Context) : BaseWebSource<SyncService>(
    context,
    livedata = false
) {

    fun sync(uid: String, latestId: Long): Call<ApiResponse<SyncResult>?> {
        return service.sync(uid, latestId)
    }

    fun push(
        uid: String,
        history: List<History>,
        data: Map<String, List<JsonObject>>
    ): Call<ApiResponse<Boolean>?> {
        val ja = mutableListOf<History.Avatar>()
        for (h in history) {
            ja.add(h.getAvatar())
        }
        return service.push(uid, Gson().toJson(ja), Gson().toJson(data))
    }

    override fun getServiceClass(): Class<SyncService> {
        return SyncService::class.java
    }

    companion object {
        var instance: SyncWebSource? = null
        fun getInstance(context: Context): SyncWebSource {
            synchronized(SyncWebSource::class.java) {
                if (instance == null) {
                    instance = SyncWebSource(context.applicationContext)
                }
                return instance!!
            }
        }
    }

}