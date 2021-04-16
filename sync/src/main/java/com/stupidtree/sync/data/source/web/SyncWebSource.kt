package com.stupidtree.sync.data.source.web

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.component.web.BaseWebSource
import com.stupidtree.sync.data.model.History
import com.stupidtree.sync.data.model.SyncResult
import com.stupidtree.sync.data.source.web.service.SyncService
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object SyncWebSource : BaseWebSource<SyncService>(
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://hita.store:39999").build()
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


}